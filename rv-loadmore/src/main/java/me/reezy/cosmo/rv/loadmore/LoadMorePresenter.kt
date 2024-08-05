package me.reezy.cosmo.rv.loadmore

import androidx.recyclerview.widget.RecyclerView
import me.reezy.cosmo.loadmore.R
import me.reezy.cosmo.statelayout.StateLayout
import me.reezy.cosmo.statelayout.StatePresenter

open class LoadMorePresenter(
    var loadingImageResId: Int = 0,
    var loadingTextResId: Int = R.string.load_more_loading,
    var loadingTextStyle: LoadMoreTextStyle? = null,

    var offlineImageResId: Int = 0,
    var offlineTextResId: Int = R.string.load_more_offline,
    var offlineTextStyle: LoadMoreTextStyle? = null,

    var emptyImageResId: Int = 0,
    var emptyTextResId: Int = R.string.load_more_empty,
    var emptyTextStyle: LoadMoreTextStyle? = null,

    var hasMoreImageResId: Int = 0,
    var hasMoreTextResId: Int = R.string.load_more_has_more,
    var hasMoreTextStyle: LoadMoreTextStyle? = null,

    var endedImageResId: Int = 0,
    var endedTextResId: Int = R.string.load_more_ended,
    var endedTextStyle: LoadMoreTextStyle? = null,

    var errorImageResId: Int = 0,
    var errorTextResId: Int = R.string.load_more_error,
    var errorTextStyle: LoadMoreTextStyle? = null,
) : StatePresenter {

    override fun show(layout: StateLayout, state: Int) {
        when (state) {
            // loadingAnimation
            LoadMoreAdapter.STATE_LOADING -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(true)
                setText(loadingTextResId)
                setImage(loadingImageResId)
                setTextStyle(loadingTextStyle)
            }
            // noNetworkImage, noNetworkText
            LoadMoreAdapter.STATE_OFFLINE -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(true)
                setText(offlineTextResId)
                setImage(offlineImageResId)
                setTextStyle(offlineTextStyle)
            }
            // emptyImage, emptyText
            LoadMoreAdapter.STATE_EMPTY -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(true)
                setText(emptyTextResId)
                setImage(emptyImageResId)
                setTextStyle(emptyTextStyle)
            }

            // loadMore: loading
            LoadMoreAdapter.STATE_HAS_MORE -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(false)
                setText(hasMoreTextResId)
                setImage(hasMoreImageResId)
                setTextStyle(hasMoreTextStyle)
            }
            // loadMore: ended
            LoadMoreAdapter.STATE_ENDED -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(false)
                setText(endedTextResId)
                setImage(endedImageResId)
                setTextStyle(endedTextStyle)
            }
            // loadMore: error
            LoadMoreAdapter.STATE_ERROR -> layout.showStateView<LoadMoreStateView>()?.apply {
                setFullHeight(false)
                setText(errorTextResId) {
                    ((layout.parent as? RecyclerView)?.adapter as? LoadMoreAdapter)?.startLoadMore()
                }
                setImage(errorImageResId)
                setTextStyle(errorTextStyle)
            }
        }
    }


}