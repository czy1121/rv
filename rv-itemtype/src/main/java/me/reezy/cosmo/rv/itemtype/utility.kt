@file:Suppress("NOTHING_TO_INLINE")

package me.reezy.cosmo.rv.itemtype

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.core.app.ComponentActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import me.reezy.cosmo.rv.itemtype.adapter.ItemTypeAdapter
import me.reezy.cosmo.rv.itemtype.holder.DataBindingHolder
import me.reezy.cosmo.rv.itemtype.holder.ItemHolder
import me.reezy.cosmo.rv.itemtype.holder.ViewHolder
import me.reezy.cosmo.rv.itemtype.type.DataBindingItemType
import me.reezy.cosmo.rv.itemtype.type.LayoutItemType
import me.reezy.cosmo.rv.itemtype.type.ViewItemType
import me.reezy.cosmo.rv.itemtype.util.ItemTypeRegistry
import java.lang.RuntimeException


fun <Item, T : ItemTypeAdapter<Item>> T.setup(registry: ItemTypeRegistry? = null, append: ItemTypeRegistry.() -> Unit = {}): T {
    val r = ItemTypeRegistry()
    registry?.toArray()?.forEach {
        r.add(it)
    }
    r.append()
    setItemTypes(*r.toArray())
    return this
}

inline fun <Item: Any> differ(noinline areSameItems: (o: Item, n: Item) -> Boolean) = AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return areSameItems(oldItem, newItem)
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}).build()

inline fun <reified Item, reified V : View> viewType(subtype: Int = 0, noinline binder: (ViewHolder<V>, Item) -> Unit): ItemType<Item, ViewHolder<V>> {
    return ViewItemType(Item::class.java, V::class.java, subtype, binder)
}

inline fun <reified Item> layoutType(@LayoutRes layoutResId: Int, subtype: Int = 0, noinline binder: (ItemHolder, Item) -> Unit): ItemType<Item, ItemHolder> {
    return LayoutItemType(Item::class.java, layoutResId, subtype, binder)
}

inline fun <Binding : ViewDataBinding, reified Item> dataBindingType(
    @LayoutRes layoutResId: Int,
    subtype: Int = 0,
    noinline binder: (DataBindingHolder<Binding>, Item) -> Unit,
): ItemType<Item, DataBindingHolder<Binding>> {
    return DataBindingItemType(Item::class.java, layoutResId, subtype, binder)
}

inline fun <reified Item> bindingType(@LayoutRes layoutResId: Int, subtype: Int = 0, clickViewId: Int = 0, noinline onClick: ((ItemHolder, Item) -> Unit)? = null): ItemType<Item, ItemHolder> {
    return bindingType(Item::class.java, layoutResId, subtype, clickViewId, onClick)
}



@RestrictTo(RestrictTo.Scope.LIBRARY)
@PublishedApi
internal fun <Item> bindingType(clazz: Class<Item>, @LayoutRes layoutResId: Int, subtype: Int = 0, clickViewId: Int = 0, onClick: ((ItemHolder, Item) -> Unit)? = null): ItemType<Item, ItemHolder> {
    return LayoutItemType(clazz, layoutResId, subtype) { holder, item ->
        onClick?.let {
            if (clickViewId > 0) {
                holder.itemView.findViewById<View>(clickViewId)?.setOnClickListener {
                    onClick(holder, item)
                }
            } else {
                holder.itemView.setOnClickListener {
                    onClick(holder, item)
                }
            }
        }

        DataBindingUtil.bind<ViewDataBinding>(holder.itemView)?.let {

            if (LayoutItemType.brItem < 0) {
                LayoutItemType.brItem = holder.itemView.context.getBrItem()
            }

            if (LayoutItemType.brItem < 1) {
                throw RuntimeException("invalid brItem[${LayoutItemType.brItem}]")
            }

            it.setVariable(LayoutItemType.brItem, item)

            if (it.lifecycleOwner == null) {
                it.lifecycleOwner = holder.itemView.getLifecycleOwner()
            }
        }
    }
}

private fun Context.getBrItem(): Int {
    return try {
        val clazz = Class.forName("$packageName.BR")
        val field = clazz.getDeclaredField("item")
        field.isAccessible = true
        field.getInt(null)
    } catch (e: Throwable) {
        0
    }
}

private fun View.getLifecycleOwner(): LifecycleOwner {
    if (this is LifecycleOwner) return this
    var obj: Context? = context
    do {
        if (obj is ComponentActivity) return obj
        obj = if (obj is ContextWrapper) obj.baseContext else null
    } while (obj != null)
    throw Exception("can not find LifecycleOwner")
}