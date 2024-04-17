package me.reezy.cosmo.rv.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
class ColumnDividerDecoration : RecyclerView.ItemDecoration() {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var size: Float = 0f

    private var insetStart: Float = 0f
    private var insetEnd: Float = 0f

    fun stroke(size: Float = 1f, color: Int = Color.LTGRAY): ColumnDividerDecoration {
        paint.strokeWidth = size
        paint.color = color
        return this
    }

    fun size(size: Float): ColumnDividerDecoration {
        this.size = size
        return this
    }

    fun inset(start: Float, end: Float = start): ColumnDividerDecoration {
        insetStart = start
        insetEnd = end
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

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position >= itemCount - 1) {
                continue
            }
            parent.getDecoratedBoundsWithMargins(child, bounds)

            val col = position % spanCount + 1
            if (col < spanCount) {
                val top = child.translationY + bounds.top + insetStart
                val bottom = when {
                    size > 0f -> top + size
                    else -> child.translationY + bounds.bottom - insetEnd
                }
                val dx = bounds.right.toFloat()
                canvas.drawLine(dx, top, dx, bottom, paint)
            }
        }
        canvas.restore()
    }

}