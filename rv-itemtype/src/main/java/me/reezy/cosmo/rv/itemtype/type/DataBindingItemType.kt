package me.reezy.cosmo.rv.itemtype.type

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import me.reezy.cosmo.rv.itemtype.ItemSubtype
import me.reezy.cosmo.rv.itemtype.ItemType
import me.reezy.cosmo.rv.itemtype.holder.DataBindingHolder

class DataBindingItemType<Binding: ViewDataBinding, Item>(
    private val itemClass: Class<Item>,
    @LayoutRes
    private val layoutResId: Int,
    private val subtype: Int = 0,
    private val binder: (DataBindingHolder<Binding>, Item) -> Unit,
) : ItemType<Item, DataBindingHolder<Binding>> {

    override fun create(parent: ViewGroup): DataBindingHolder<Binding> = DataBindingHolder(LayoutInflater.from(parent.context).inflate(layoutResId, parent, false))

    override fun matches(item: Any) = itemClass.isInstance(item) && (subtype == 0 || (item is ItemSubtype && item.subtype == subtype))

    override fun bind(holder: DataBindingHolder<Binding>, item: Item) {
        binder(holder, item)
    }
}