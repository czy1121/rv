package me.reezy.cosmo.rv.animator


import androidx.recyclerview.widget.RecyclerView.ViewHolder

open class FadeItemAnimator : BaseItemAnimator() {
    override fun onAnimateLeave(holder: ViewHolder) {
        holder.itemView.animate().alpha(0f)
    }

    override fun onPrepareEnter(holder: ViewHolder) {
        holder.itemView.alpha = 0f
    }

    override fun onAnimateEnter(holder: ViewHolder) {
        holder.itemView.animate().alpha(1f)
    }
}







