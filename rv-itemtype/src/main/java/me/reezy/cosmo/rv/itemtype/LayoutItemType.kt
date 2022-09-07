package me.reezy.cosmo.rv.itemtype

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes

class LayoutItemType<Item>(
    private val itemClass: Class<Item>,
    @LayoutRes
    private val layoutResId: Int,
    private val subtype: Int = 0,
    private val binder: (ItemHolder, Item) -> Unit
) : ItemType<Item, ItemHolder> {

    companion object {
        var brItem: Int = 0
    }

    override fun create(parent: ViewGroup): ItemHolder = ItemHolder(LayoutInflater.from(parent.context).inflate(layoutResId, parent, false))

    override fun matches(item: Any) = itemClass.isInstance(item) && (subtype == 0 || (item is ItemSubtype && item.subtype == subtype))

    override fun bind(holder: ItemHolder, item: Item) {
        binder(holder, item)
    }
}