package me.reezy.cosmo.rv.itemtype

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.core.app.ComponentActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner


inline fun <reified Item, reified V> viewType(subtype: Int = 0): ItemType<Item, ViewHolder<V>> where V: View, V: ItemView<Item> {
    return ViewItemType(Item::class.java, V::class.java, subtype) { holder, item ->
        holder.view.bind(item)
    }
}

inline fun <reified Item> layoutType(@LayoutRes layoutResId: Int, subtype: Int = 0, noinline binder: (ItemHolder, Item) -> Unit): ItemType<Item, ItemHolder> {
    return LayoutItemType(Item::class.java, layoutResId, subtype, binder)
}

inline fun <reified Item> bindingType(@LayoutRes layoutResId: Int, subtype: Int = 0, noinline onClick: ((ItemHolder, Item) -> Unit)? = null): ItemType<Item, ItemHolder> {
    return LayoutItemType(Item::class.java, layoutResId, subtype) { holder, item ->
        onClick?.let {
            holder.itemView.setOnClickListener {
                onClick(holder, item)
            }
        }
        DataBindingUtil.bind<ViewDataBinding>(holder.itemView)?.let {
            it.setVariable(LayoutItemType.brItem, item)
            if (it.lifecycleOwner == null) {
                it.lifecycleOwner = holder.itemView.getLifecycleOwner()
            }
        }
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@PublishedApi
internal fun View.getLifecycleOwner(): LifecycleOwner {
    if (this is LifecycleOwner) return this
    var obj: Context? = context
    do {
        if (obj is ComponentActivity) return obj
        obj = if (obj is ContextWrapper) obj.baseContext else null
    } while (obj != null)
    throw Exception("can not find LifecycleOwner")
}