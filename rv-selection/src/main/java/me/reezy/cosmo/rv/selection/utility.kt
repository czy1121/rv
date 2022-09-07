package me.reezy.cosmo.rv.selection

import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random


fun selectionTracker(
    rv: RecyclerView,
    multiple: Boolean = true,
    selectionId: String = Random.nextLong().toString(),
    builder: SelectionTracker.Builder<String>.() -> Unit = {}
): SelectionTracker<String> {
    val provider = StringKeyProvider(rv)
    return SelectionTracker.Builder(
        selectionId,
        rv,
        provider,
        StringKeyDetailsLookup(rv, provider),
        StorageStrategy.createStringStorage()
    ).withSelectionPredicate(StringKeySelectionPredicate(multiple)).apply(builder).build()
}

