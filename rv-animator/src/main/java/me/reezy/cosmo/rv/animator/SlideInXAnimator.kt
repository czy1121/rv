package me.reezy.cosmo.rv.animator

import androidx.recyclerview.widget.RecyclerView.ViewHolder

class SlideInXAnimator(private val from: Float = -1f) : BaseItemAnimator() {

    override fun onAnimateLeave(holder: ViewHolder) {
        holder.itemView.animate().alpha(0f).translationX(from * holder.itemView.width.toFloat())
    }

    override fun onPrepareEnter(holder: ViewHolder) {
        holder.itemView.alpha = 0f
        holder.itemView.translationX = from * holder.itemView.width.toFloat()
    }

    override fun onAnimateEnter(holder: ViewHolder) {
        holder.itemView.animate().alpha(1f).translationX(0f)
    }
}