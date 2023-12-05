package me.reezy.cosmo.rv.polymorph.data

import androidx.annotation.Keep
import me.reezy.cosmo.rv.itemtype.ItemSubtype
import me.reezy.cosmo.rv.polymorph.PolymorphItem


data class Sublist(
    override val subtype: Int = 0,
    @Keep val data: List<PolymorphItem>,
) : ItemSubtype, PolymorphItem