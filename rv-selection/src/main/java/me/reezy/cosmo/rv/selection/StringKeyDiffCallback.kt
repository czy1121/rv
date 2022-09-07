package me.reezy.cosmo.rv.selection

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class StringKeyDiffCallback() : DiffUtil.ItemCallback<StringKey>() {
    override fun areItemsTheSame(oldItem: StringKey, newItem: StringKey): Boolean {
        return oldItem.stringKey == newItem.stringKey
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: StringKey, newItem: StringKey): Boolean {
        return oldItem == newItem
    }
}