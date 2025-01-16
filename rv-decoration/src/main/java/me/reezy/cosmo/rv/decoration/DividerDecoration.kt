package me.reezy.cosmo.rv.decoration

import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt


class DividerDecoration(
    private val orientation: Int = RecyclerView.VERTICAL,
    private val isLastVisible: Boolean = false,
    private val isInside: Boolean = false
) : RecyclerView.ItemDecoration() {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var insetStart: Float = 0f
    private var insetEnd: Float = 0f


    fun stroke(size: Float = 1f, color: Int = Color.LTGRAY): DividerDecoration {
        paint.strokeWidth = size
        paint.color = color
        return this
    }

    fun dash(dashWidth: Float, dashGap: Float = dashWidth): DividerDecoration {
        paint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
        return this
    }

    fun inset(start: Float, end: Float = start): DividerDecoration {
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
        if (parent.layoutManager == null || parent.adapter == null) {
            return
        }

        canvas.save()

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            val position = parent.getChildAdapterPosition(child)
            val itemCount = parent.adapter!!.itemCount
            if (!isLastVisible && position >= itemCount - 1) {
                continue
            }
            parent.getDecoratedBoundsWithMargins(child, bounds)

            if (orientation == RecyclerView.VERTICAL) {
                val y = bounds.bottom + child.translationY - paint.strokeWidth / 2f
                canvas.drawLine(insetStart, y, parent.width - insetEnd, y, paint)
            } else {
                val x = bounds.right + child.translationX - paint.strokeWidth / 2f
                canvas.drawLine(x, insetStart, x, parent.height - insetEnd, paint)
            }

        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        if (isInside) {
            outRect.set(0, 0, 0, 0)
            return
        }
        if (orientation == RecyclerView.VERTICAL) {
            outRect.set(0, 0, 0, paint.strokeWidth.roundToInt())
        } else {
            outRect.set(0, 0, paint.strokeWidth.roundToInt(), 0)
        }
    }


}