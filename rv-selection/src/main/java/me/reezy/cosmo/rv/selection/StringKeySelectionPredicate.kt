package me.reezy.cosmo.rv.selection

import androidx.recyclerview.selection.SelectionTracker

class StringKeySelectionPredicate(private val multiple: Boolean = true) : SelectionTracker.SelectionPredicate<String>() {
    override fun canSelectMultiple(): Boolean = multiple
    override fun canSetStateForKey(key: String, nextState: Boolean): Boolean = key != StringKeyItemDetails.EMPTY.selectionKey
    override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean = position != StringKeyItemDetails.EMPTY.position
}