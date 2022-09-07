package me.reezy.cosmo.rv.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager

class SpaceDecoration(
    private val hSpacing: Int,
    private val vSpacing: Int = hSpacing
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val lm = parent.layoutManager
        val position = parent.getChildAdapterPosition(view)
        if (lm is GridLayoutManager) {
            val spanCount = lm.spanCount
            val spanSize = lm.spanSizeLookup.getSpanSize(position)
            val spanIndex = lm.spanSizeLookup.getSpanIndex(position, spanCount)
            outRect.left = spanIndex * hSpacing / spanCount
            outRect.right = hSpacing - (spanIndex + spanSize) * hSpacing / spanCount

            val groupIndex = lm.spanSizeLookup.getSpanGroupIndex(position, spanCount)
            if (groupIndex > 0) {
                outRect.top = vSpacing
            }
        } else if (lm is LinearLayoutManager) {
            if (position > 0) {
                if (lm.orientation == RecyclerView.VERTICAL) {
                    outRect.top = vSpacing
                } else {
                    outRect.left = hSpacing
                }
            }
        } else if (lm is FlexboxLayoutManager) {
            outRect.right = hSpacing

            val lines = lm.flexLines
            if (lines.isNotEmpty() && position >= lines[0].itemCount) {
                outRect.top = vSpacing
            }
        }
    }

}