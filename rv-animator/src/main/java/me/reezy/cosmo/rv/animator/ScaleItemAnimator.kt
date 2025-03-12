package me.reezy.cosmo.rv.animator

import androidx.annotation.FloatRange
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class ScaleItemAnimator(
    private val scale: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0)
    private val pivotX: Float = 0.5f,
    @FloatRange(from = 0.0, to = 1.0)
    private val pivotY: Float = 0.5f,
) : BaseItemAnimator() {
    override fun onPrepareLeave(holder: ViewHolder) {
        holder.itemView.pivotX = pivotX * holder.itemView.width.toFloat()
        holder.itemView.pivotY = pivotY * holder.itemView.height.toFloat()
    }

    override fun onAnimateLeave(holder: ViewHolder) {
        holder.itemView.animate().alpha(0f).scaleX(scale).scaleY(scale)
    }

    override fun onPrepareEnter(holder: ViewHolder) {
        holder.itemView.pivotX = pivotX * holder.itemView.width.toFloat()
        holder.itemView.pivotY = pivotY * holder.itemView.height.toFloat()

        holder.itemView.alpha = 0f
        holder.itemView.scaleX = scale
        holder.itemView.scaleY = scale
    }

    override fun onAnimateEnter(holder: ViewHolder) {
        holder.itemView.animate().alpha(1f).scaleX(1f).scaleY(1f)
    }
}