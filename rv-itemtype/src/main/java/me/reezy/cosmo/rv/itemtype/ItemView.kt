package me.reezy.cosmo.rv.itemtype


interface ItemView<Item> {
    fun bind(item: Item)
}
