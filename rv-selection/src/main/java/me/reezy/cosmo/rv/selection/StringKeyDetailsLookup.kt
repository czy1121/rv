package me.reezy.cosmo.rv.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class StringKeyDetailsLookup(
        private val rv: RecyclerView,
        private val provider: ItemKeyProvider<String>
) : ItemDetailsLookup<String>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<String> {
        val view = rv.findChildViewUnder(e.x, e.y) ?: return StringKeyItemDetails.EMPTY
        val holder = rv.getChildViewHolder(view) ?: return StringKeyItemDetails.EMPTY
        val position = holder.adapterPosition
        val key = provider.getKey(position) ?: return StringKeyItemDetails.EMPTY
        return StringKeyItemDetails(position, key)
    }


}
