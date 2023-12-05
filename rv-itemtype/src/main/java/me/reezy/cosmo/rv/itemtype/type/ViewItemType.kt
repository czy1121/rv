package me.reezy.cosmo.rv.itemtype.type

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.reezy.cosmo.rv.itemtype.ItemType
import me.reezy.cosmo.rv.itemtype.ItemSubtype
import me.reezy.cosmo.rv.itemtype.holder.ViewHolder

class ViewItemType<Item, V: View>(
    private val itemClass: Class<Item>,
    private val viewClass: Class<V>,
    private val subtype: Int = 0,
    private val binder: (ViewHolder<V>, Item) -> Unit
) : ItemType<Item, ViewHolder<V>> {

    override fun create(parent: ViewGroup): ViewHolder<V> {
        val view = viewClass.getConstructor(Context::class.java).newInstance(parent.context)
        if (view.layoutParams == null) {
            view.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        }
        return ViewHolder(view)
    }

    override fun matches(item: Any) = itemClass.isInstance(item) && (subtype == 0 || (item is ItemSubtype && item.subtype == subtype))

    override fun bind(holder: ViewHolder<V>, item: Item) {
        binder(holder, item)
    }
}