package me.reezy.cosmo.rv.selection

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

open class SelectionViewModel(application: Application) : AndroidViewModel(application) {


    private val mOnAction = MutableSharedFlow<String>()
    private val mOnEditModeChanged = MutableSharedFlow<Boolean>()
    private val mOnSelectStateChanged = MutableSharedFlow<SelectionState>()

    val onAction: SharedFlow<String> = mOnAction
    val onEditModeChanged: SharedFlow<Boolean> = mOnEditModeChanged
    val onSelectStateChanged: SharedFlow<SelectionState> = mOnSelectStateChanged

    var isEditMode: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            viewModelScope.launch {
                mOnEditModeChanged.emit(value)
            }
        }
    var totalCount: Int = 0
        set(value) {
            if (field == value) return
            field = value
            updateSelectState(selectedCount, field)
        }
    var selectedCount: Int = 0
        set(value) {
            if (field == value) return
            field = value
            updateSelectState(field, totalCount)
        }
    var selectState: SelectionState = SelectionState.None
        private set

    private fun updateSelectState(selectedCount: Int, totalCount: Int) {
        selectState = when (selectedCount) {
            0 -> SelectionState.None
            totalCount -> SelectionState.All
            else -> SelectionState.Partial
        }

        viewModelScope.launch {
            mOnSelectStateChanged.emit(selectState)
        }
    }


    fun delete() {
        viewModelScope.launch {
            mOnAction.emit("delete")
        }
    }


    fun action(action: String) {
        viewModelScope.launch {
            mOnAction.emit(action)
        }
    }
}