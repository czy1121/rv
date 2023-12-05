package me.reezy.cosmo.rv.loadmore

import android.annotation.SuppressLint
import androidx.recyclerview.widget.*
import me.reezy.cosmo.rv.itemtype.*
import me.reezy.cosmo.rv.itemtype.adapter.ItemTypeAdapter
import me.reezy.cosmo.rv.itemtype.holder.ItemHolder
import me.reezy.cosmo.rv.itemtype.type.ViewItemType
import me.reezy.cosmo.rv.itemtype.util.ItemDiffCallback
import me.reezy.cosmo.statelayout.StateLayout
import me.reezy.cosmo.statelayout.StatePresenter

@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class LoadMoreAdapter(config: AsyncDifferConfig<Any> = AsyncDifferConfig.Builder(ItemDiffCallback()).build()) : ItemTypeAdapter<Any>(config) {

    companion object {
        const val STATE_LOADING = 0
        const val STATE_OFFLINE = 1
        const val STATE_EMPTY = 2

        const val STATE_HAS_MORE = 3
        const val STATE_ENDED = 4
        const val STATE_ERROR = 5
    }

    internal class LoadMoreItem {
        var state: Int = STATE_LOADING
    }


    private var loadMoreItem = LoadMoreItem()
    private var loadMoreItemType = ViewItemType(LoadMoreItem::class.java, StateLayout::class.java) { holder, item ->
        holder.view.isFocusable = false
        loadMorePresenter.show(holder.view, item.state)
    }

    private val loadMoreTrigger = LoadMoreTrigger()


    var loadMorePresenter: StatePresenter = LoadMorePresenter()

    var loadMoreVisible: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            if (value) {
                notifyItemInserted(itemCount)
            } else {
                notifyItemRemoved(itemCount - 1)
            }
        }

    fun setOnLoadMoreListener(listener: () -> Unit) {
        loadMoreTrigger.onLoadMore = listener
    }

    override fun setItemTypes(vararg types: ItemType<Any, ItemHolder>): ItemTypeAdapter<Any> {
        return super.setItemTypes(loadMoreItemType as ItemType<Any, ItemHolder>, *types)
    }


    fun startLoading(online: Boolean = true) {
        if (currentList.isEmpty()) {
            if (setStatus(if (online) STATE_LOADING else STATE_OFFLINE)) {
                notifyItemChanged(itemCount - 1)
            }
        }
    }

    fun startLoadMore() {
        if (!loadMoreTrigger.isLoading) {
            if (setStatus(STATE_HAS_MORE)) {
                notifyItemChanged(itemCount - 1)
            }
            loadMoreTrigger.loadMore()
        }
    }

    fun showError() {
        if (setStatus(STATE_ERROR)) {
            notifyItemChanged(itemCount - 1)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addList(list: List<Any>, hasMore: Boolean = false, isRefresh: Boolean = false) {

        val newList = if (isRefresh) list else (listOf<Any>() + currentList + list)

        val changed = when {
            newList.isEmpty() -> setStatus(STATE_EMPTY)
            hasMore -> setStatus(STATE_HAS_MORE)
            else -> setStatus(STATE_ENDED)
        }
        if (changed) {
            notifyItemRemoved(itemCount - 1)
        }
        submitList(newList) {
            if (changed) {
                notifyItemInserted(itemCount - 1)
            }
        }

    }

    private fun setStatus(status: Int): Boolean {
        val changed = loadMoreVisible && loadMoreItem.state != status
//        if (changed) {
//            notifyItemChanged(itemCount - 1)
//        }
        loadMoreItem.state = status
        loadMoreTrigger.hasMore = status == STATE_HAS_MORE
        loadMoreTrigger.isLoading = false
        return changed
    }


    override fun getItemCount(): Int {
        return super.getItemCount() + if (loadMoreVisible) 1 else 0
    }


    override fun getItem(position: Int): Any {
        return if (currentList.size == position) loadMoreItem else currentList[position]
    }

    override fun getItemId(position: Int): Long {
        return if (currentList.size == position) RecyclerView.NO_ID else super.getItemId(position)
    }

    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        super.onAttachedToRecyclerView(rv)
        val manager = rv.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (currentList.size == position) return manager.spanCount
                    val type = getItemViewType(position)
                    return if (type == RecyclerView.INVALID_TYPE) manager.spanCount else 1
                }
            }
        }
        loadMoreTrigger.attach(rv)
    }

    override fun onDetachedFromRecyclerView(rv: RecyclerView) {
        super.onDetachedFromRecyclerView(rv)
        loadMoreTrigger.detach(rv)
    }


    override fun onViewAttachedToWindow(holder: ItemHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp is StaggeredGridLayoutManager.LayoutParams && holder.itemView is StateLayout) {
            lp.isFullSpan = true
        }
    }
}