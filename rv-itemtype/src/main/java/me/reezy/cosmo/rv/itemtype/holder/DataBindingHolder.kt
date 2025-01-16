package me.reezy.cosmo.rv.itemtype.holder

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

class DataBindingHolder<Binding : ViewDataBinding>(itemView: View) : ItemHolder(itemView) {
    val binding: Binding by lazy { DataBindingUtil.bind(itemView)!! }
}