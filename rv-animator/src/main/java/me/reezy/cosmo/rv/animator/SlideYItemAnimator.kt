package me.reezy.cosmo.rv.animator

import androidx.recyclerview.widget.RecyclerView.ViewHolder

open class SlideYItemAnimator(private val from: Float = -1f) : BaseItemAnimator() {

    override fun onAnimateLeave(holder: ViewHolder) {
        holder.itemView.animate().alpha(0f).translationY(from * holder.itemView.height.toFloat())
    }

    override fun onPrepareEnter(holder: ViewHolder) {
        holder.itemView.alpha = 0f
        holder.itemView.translationY = from * holder.itemView.height.toFloat()
    }

    override fun onAnimateEnter(holder: ViewHolder) {
        holder.itemView.animate().alpha(1f).translationY(0f)
    }
}