package me.reezy.cosmo.rv.itemtype.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import me.reezy.cosmo.rv.itemtype.ItemDiffCallback
import me.reezy.cosmo.rv.itemtype.ItemHolder
import me.reezy.cosmo.rv.itemtype.ItemType


class SingleTypeAdapter<Item : Any, Holder : ItemHolder>(
    type: ItemType<Item, Holder>,
    config: AsyncDifferConfig<Item> = AsyncDifferConfig.Builder<Item>(ItemDiffCallback()).build()
) : ListAdapter<Item, Holder>(config) {

    private val itemType: ItemType<Item, Holder> = type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return itemType.create(parent)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        itemType.bind(holder, getItem(position))
    }
}