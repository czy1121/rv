package me.reezy.cosmo.rv.loadmore

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class LoadMoreTrigger : RecyclerView.OnScrollListener(), NestedScrollView.OnScrollChangeListener, View.OnAttachStateChangeListener {
    var onLoadMore: () -> Unit = {}
    var hasMore = false
    var isLoading = false
    var visibleThreshold = 3

    var recyclerView: RecyclerView? = null
    private var nestedScrollView: NestedScrollView? = null

    fun loadMore() {
        isLoading = true
        onLoadMore()
    }

    fun attach(rv: RecyclerView) {
        recyclerView = rv
        rv.addOnAttachStateChangeListener(this)

        if (rv.isNestedScrollingEnabled) {
            rv.addOnScrollListener(this)
        } else {
            attachNestedScrollView()
        }
    }

    fun detach(rv: RecyclerView) {
        recyclerView?.removeOnAttachStateChangeListener(this)
        recyclerView?.removeOnScrollListener(this)
        recyclerView = null

        detachNestedScrollView()
    }

    private fun attachNestedScrollView() {
        var p = recyclerView?.parent
        while (p is View) {
            if (p is NestedScrollView) {
                p.setOnScrollChangeListener(this)
                nestedScrollView = p
                break
            }
            p = p.getParent()
        }
    }

    private fun detachNestedScrollView() {
        val l: NestedScrollView.OnScrollChangeListener? = null
        nestedScrollView?.setOnScrollChangeListener(l)
        nestedScrollView = null
    }


    override fun onViewAttachedToWindow(v: View) {
        attachNestedScrollView()
    }

    override fun onViewDetachedFromWindow(v: View) {
        detachNestedScrollView()
    }

    override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
        super.onScrollStateChanged(rv, newState)
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            return
        }
        scroll(rv)
    }
    override fun onScrollChange(scroll: NestedScrollView, nowX: Int, nowY: Int, oldX: Int, oldY: Int) {
        val child = scroll.getChildAt(scroll.childCount - 1)
        val distanceToEnd = child.bottom - (scroll.height + scroll.scrollY)
        if (distanceToEnd == 0 && nowY > oldY) {
            recyclerView?.let {
                scroll(it)
            }
        }
    }


    private fun scroll(rv: RecyclerView) {
        if (isLoading || !hasMore) return

        if (rv.layoutManager is LinearLayoutManager) {
            val manager = rv.layoutManager as LinearLayoutManager?
            val count = rv.childCount
            val total = manager!!.itemCount
            val last = manager.findLastVisibleItemPosition()
            if (needLoadMore(count, total, last)) {
                loadMore()
            }
        } else if (rv.layoutManager is GridLayoutManager) {
            val manager = rv.layoutManager as GridLayoutManager?
            val count = rv.childCount
            val total = manager!!.itemCount
            val last = manager.findLastVisibleItemPosition()
            if (needLoadMore(count, total, last)) {
                loadMore()
            }
        }
    }

    private fun needLoadMore(count: Int, total: Int, last: Int): Boolean {
        return count > 0 && last > total - visibleThreshold
    }

}