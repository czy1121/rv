package me.reezy.cosmo.rv.loadmore

import androidx.recyclerview.widget.RecyclerView
import me.reezy.cosmo.loadmore.R
import me.reezy.cosmo.statelayout.StateLayout
import me.reezy.cosmo.statelayout.StatePresenter

class LoadMorePresenter(
    var loadingTextResId: Int = R.string.load_more_loading,
    var offlineTextResId: Int = R.string.load_more_offline,
    var emptyTextResId: Int = R.string.load_more_empty,
    var hasMoreTextResId: Int = R.string.load_more_has_more,
    var endedTextResId: Int = R.string.load_more_ended,
    var errorTextResId: Int = R.string.load_more_error,
    var loadingImageResId: Int = 0,
    var offlineImageResId: Int = 0,
    var emptyImageResId: Int = 0,
    var hasMoreImageResId: Int = 0,
    var endedImageResId: Int = 0,
    var errorImageResId: Int = 0,
) : StatePresenter {

    override fun show(layout: StateLayout, state: Int) {
        when (state) {
            // loadingAnimation
            LoadMoreAdapter.STATE_LOADING -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(true)
                setText(loadingTextResId)
                setImage(loadingImageResId)
            }
            // noNetworkImage, noNetworkText
            LoadMoreAdapter.STATE_OFFLINE -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(true)
                setText(offlineTextResId)
                setImage(offlineImageResId)
            }
            // emptyImage, emptyText
            LoadMoreAdapter.STATE_EMPTY -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(true)
                setText(emptyTextResId)
                setImage(emptyImageResId)
            }

            // loadMore: loading
            LoadMoreAdapter.STATE_HAS_MORE -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(false)
                setText(hasMoreTextResId)
                setImage(hasMoreImageResId)
            }
            // loadMore: ended
            LoadMoreAdapter.STATE_ENDED -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(false)
                setText(endedTextResId)
                setImage(endedImageResId)
            }
            // loadMore: error
            LoadMoreAdapter.STATE_ERROR -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(false)
                setText(errorTextResId) {
                    ((layout.parent as? RecyclerView)?.adapter as? LoadMoreAdapter)?.startLoadMore()
                }
                setImage(errorImageResId)
            }
        }
    }

}