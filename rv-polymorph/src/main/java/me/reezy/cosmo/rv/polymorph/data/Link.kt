package me.reezy.cosmo.rv.polymorph.data

import androidx.annotation.Keep
import me.reezy.cosmo.rv.itemtype.ItemSubtype
import me.reezy.cosmo.rv.polymorph.PolymorphItem


data class Link(
    @Keep val text: String? = null,
    @Keep val image: String? = null,
    @Keep val desc: String? = null,
    @Keep val label: String? = null,
    @Keep val url: String? = null,
    override val subtype: Int = 0,
) : ItemSubtype, PolymorphItem {
    val hasLabel: Boolean get() = !label.isNullOrEmpty()
}