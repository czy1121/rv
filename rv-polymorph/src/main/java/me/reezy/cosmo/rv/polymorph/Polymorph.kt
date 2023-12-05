package me.reezy.cosmo.rv.polymorph

import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import me.reezy.cosmo.rv.itemtype.util.ItemTypeRegistry

object Polymorph {
    val registry = ItemTypeRegistry()

    private val types: MutableList<Pair<Class<out PolymorphItem>, String>> = mutableListOf()

    fun addType(clazz: Class<out PolymorphItem>, label: String = clazz.simpleName) {
        types.add(clazz to label)
    }

    fun createJsonAdapterFactory(): PolymorphicJsonAdapterFactory<PolymorphItem> {
        return types.fold(PolymorphicJsonAdapterFactory.of(PolymorphItem::class.java, "_type")) { acc, type ->
            acc.withSubtype(type.first, type.second)
        }
    }
}