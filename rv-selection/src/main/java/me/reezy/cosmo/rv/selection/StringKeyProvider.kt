package me.reezy.cosmo.rv.selection

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
 
class StringKeyProvider(private val rv: RecyclerView) : ItemKeyProvider<String>(SCOPE_CACHED) {
    private val list: List<Any>? get() = (rv.adapter as? ListAdapter<*, *>)?.currentList

    override fun getKey(position: Int): String? = (list?.get(position) as? StringKey)?.stringKey
    override fun getPosition(key: String): Int = list?.indexOfFirst { it is StringKey && it.stringKey == key } ?: RecyclerView.NO_POSITION
}