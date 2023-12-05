package me.reezy.cosmo.rv.itemtype.adapter

import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.reezy.cosmo.rv.itemtype.util.ItemDiffCallback
import me.reezy.cosmo.rv.itemtype.ItemType
import me.reezy.cosmo.rv.itemtype.holder.ItemHolder


open class ItemTypeAdapter<Item : Any>(config: AsyncDifferConfig<Item> = AsyncDifferConfig.Builder<Item>(ItemDiffCallback()).build()) : ListAdapter<Item, ItemHolder>(config) {

    private lateinit var itemTypes: Array<out ItemType<Any, ItemHolder>>

    open fun setItemTypes(vararg types: ItemType<Any, ItemHolder>): ItemTypeAdapter<Item> {
        itemTypes = types
        return this
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        for (viewType in itemTypes.indices) {
            if (itemTypes[viewType].matches(item)) {
                return viewType
            }
        }
        return RecyclerView.INVALID_TYPE
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        if (viewType == RecyclerView.INVALID_TYPE) {
            return ItemHolder(Space(parent.context))
        }
        return itemTypes[viewType].create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (holder.itemViewType != RecyclerView.INVALID_TYPE) {
            val item = getItem(position)
            itemTypes[holder.itemViewType].bind(holder, item)
        }
    }
}