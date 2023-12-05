package me.reezy.cosmo.rv.polymorph

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import me.reezy.cosmo.rv.itemtype.*
import me.reezy.cosmo.rv.itemtype.adapter.ItemTypeAdapter
import me.reezy.cosmo.rv.itemtype.holder.ViewHolder
import me.reezy.cosmo.rv.itemtype.util.ItemDiffCallback
import me.reezy.cosmo.rv.polymorph.data.Sublist
import java.util.concurrent.Executor


class SublistType(
    private val subtype: Int,
    private val setup: SublistView.() -> Unit = {}
) : ItemType<Sublist, ViewHolder<SublistView>> {

    private val executor = Executor { it.run() }

    @SuppressLint("RestrictedApi")
    private val config = AsyncDifferConfig.Builder(ItemDiffCallback())
        .setBackgroundThreadExecutor(executor)
        .setMainThreadExecutor(executor)
        .build()

    override fun create(parent: ViewGroup): ViewHolder<SublistView> {
        val view = SublistView(parent.context)
        view.adapter = ItemTypeAdapter(config).setup(Polymorph.registry)
        view.setup()
        return ViewHolder(view)
    }

    override fun matches(item: Any) = item is Sublist && item.subtype == subtype

    override fun bind(holder: ViewHolder<SublistView>, item: Sublist) {
        val adapter = holder.view.adapter
        if (adapter is ListAdapter<*, *>) {
            @Suppress("UNCHECKED_CAST")
            (adapter as ListAdapter<PolymorphItem, *>).submitList(item.data)
        }
    }
}