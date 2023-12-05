package me.reezy.cosmo.rv.itemtype.util

import me.reezy.cosmo.rv.itemtype.ItemType
import me.reezy.cosmo.rv.itemtype.holder.ItemHolder

class ItemTypeRegistry {
    private val types = mutableListOf<ItemType<Any, ItemHolder>>()

    fun <Item : Any, Holder : ItemHolder> add(type: ItemType<Item, Holder>) {
        @Suppress("UNCHECKED_CAST")
        types.add(type as ItemType<Any, ItemHolder>)
    }

    fun toArray() = types.toTypedArray()
}