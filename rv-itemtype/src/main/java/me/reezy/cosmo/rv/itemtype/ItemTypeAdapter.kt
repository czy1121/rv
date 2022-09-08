package me.reezy.cosmo.rv.itemtype

import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

open class ItemTypeAdapter<Item: Any>(config: AsyncDifferConfig<Item> = AsyncDifferConfig.Builder<Item>(ItemDiffCallback()).build()) : ListAdapter<Item, ItemHolder>(config) {

    private lateinit var itemTypes: Array<out ItemType<Any, ItemHolder>>

    open fun setup(vararg types: ItemType<Any, ItemHolder>): ItemTypeAdapter<Item> {
        itemTypes = types
        return this
    }
    fun setup(registry: ItemTypeRegistry? = null, append: ItemTypeRegistry.() -> Unit = {}): ItemTypeAdapter<Item> {
        val r = ItemTypeRegistry()
        registry?.let {
            r.add(*it.toArray())
        }
        r.append()
        setup(*r.toArray())
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
            itemTypes[holder.itemViewType].bind(holder, getItem(position))
        }
    }
}