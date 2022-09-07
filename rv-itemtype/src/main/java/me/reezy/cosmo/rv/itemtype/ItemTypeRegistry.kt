package me.reezy.cosmo.rv.itemtype

@Suppress("UNCHECKED_CAST")
class ItemTypeRegistry {
    private val types = mutableListOf<ItemType<Any, ItemHolder>>()

    fun <Item : Any, Holder : ItemHolder> add(vararg list: ItemType<Item, Holder>) {
        list.forEach {
            types.add(it as ItemType<Any, ItemHolder>)
        }
    }

    fun toArray() = types.toTypedArray()
}