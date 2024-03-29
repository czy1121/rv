
package me.reezy.cosmo.rv.itemtype

import android.view.ViewGroup
import me.reezy.cosmo.rv.itemtype.holder.ItemHolder


interface ItemType<Item, Holder : ItemHolder> {

    fun create(parent: ViewGroup): Holder

    fun matches(item: Any): Boolean

    fun bind(holder: Holder, item: Item)
}