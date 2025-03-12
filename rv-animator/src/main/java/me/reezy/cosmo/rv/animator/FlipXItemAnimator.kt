package me.reezy.cosmo.rv.animator

import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * 沿X轴翻转，入场角度(rotation->0)，离场角度(rotation->0)
 * */
open class FlipXItemAnimator(private val rotation: Float = -90f) : BaseItemAnimator() {

    override fun onAnimateLeave(holder: ViewHolder) {
        holder.itemView.animate().alpha(0f).rotationX(rotation)
    }

    override fun onPrepareEnter(holder: ViewHolder) {
        holder.itemView.alpha = 0f
        holder.itemView.rotationX = rotation
    }

    override fun onAnimateEnter(holder: ViewHolder) {
        holder.itemView.animate().alpha(1f).rotationX(0f)
    }
}