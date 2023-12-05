package me.reezy.cosmo.rv.decoration

import android.graphics.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridDividerDecoration() : RecyclerView.ItemDecoration() {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun stroke(size: Float = 1f, color: Int = Color.LTGRAY): GridDividerDecoration {
        paint.strokeWidth = size
        paint.color = color
        return this
    }

    fun dash(dashWidth: Float, dashGap: Float = dashWidth): GridDividerDecoration {
        paint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
        return this
    }

    private val bounds = Rect()

    init {
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.color = Color.LTGRAY
        paint.strokeWidth = 1f
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val lm = parent.layoutManager
        if (lm == null || lm !is GridLayoutManager || parent.adapter == null) {
            return
        }

        canvas.save()
        val spanCount = lm.spanCount
        val itemCount = parent.adapter!!.itemCount
        val rowCount = if (itemCount % spanCount == 0) itemCount / spanCount else (itemCount / spanCount + 1)

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position >= itemCount - 1) {
                continue
            }
            parent.getDecoratedBoundsWithMargins(child, bounds)

            val col = position % spanCount + 1
            if (col < spanCount) {
                val top = bounds.top + child.translationY
                val bottom = bounds.bottom + child.translationY
                val dx = bounds.right.toFloat()
                canvas.drawLine(dx, top, dx, bottom, paint)
            }
            val row = position / spanCount + 1
            if (row < rowCount){
                val left = bounds.left + child.translationX
                val right = bounds.right + child.translationX
                val dy = bounds.bottom.toFloat()
                canvas.drawLine(left, dy, right, dy, paint)
            }

        }
        canvas.restore()
    }

}