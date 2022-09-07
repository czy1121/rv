package me.reezy.cosmo.rv.selection

import androidx.recyclerview.selection.ItemDetailsLookup

class StringKeyItemDetails(private val position: Int, private val stringId: String) : ItemDetailsLookup.ItemDetails<String>() {

    override fun getPosition(): Int = position

    override fun getSelectionKey(): String = stringId

    companion object {
        val EMPTY = StringKeyItemDetails(Int.MAX_VALUE, "28ed7d1616657cc971a7919d5da3749d28a81a964b9b8e2804593d8c8d801d91")
    }
}