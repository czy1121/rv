package me.reezy.cosmo.rv.animator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlin.math.abs


/**
 * - 自定义列表项的入场(add)/离场(remove)动画
 * - 复制 DefaultItemAnimator 代码，禁用 change 动画，不修改 move 动画
 * */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseItemAnimator : SimpleItemAnimator() {


    companion object {
        private const val DEBUG = false
    }


    private class MoveInfo(var holder: ViewHolder, var fromX: Int, var fromY: Int, var toX: Int, var toY: Int)

    private class ChangeInfo(var oldHolder: ViewHolder?, var newHolder: ViewHolder?, var fromX: Int, var fromY: Int, var toX: Int, var toY: Int)


    private val mPendingRemovals = ArrayList<ViewHolder>()
    private val mPendingAdditions = ArrayList<ViewHolder>()
    private val mPendingMoves = ArrayList<MoveInfo>()
    private val mPendingChanges = ArrayList<ChangeInfo>()

    private var mAdditionsList = ArrayList<ArrayList<ViewHolder>>()
    private var mMovesList = ArrayList<ArrayList<MoveInfo>>()
    private var mChangesList = ArrayList<ArrayList<ChangeInfo>>()

    private var mAddAnimations = ArrayList<ViewHolder?>()
    private var mMoveAnimations = ArrayList<ViewHolder?>()
    private var mRemoveAnimations = ArrayList<ViewHolder?>()
    private var mChangeAnimations = ArrayList<ViewHolder?>()

    private var mLeaveInterceptor: TimeInterpolator? = null
    private var mEnterInterceptor: TimeInterpolator? = null


    init {
        supportsChangeAnimations = false
    }

    fun setInterceptor(enter: TimeInterpolator? = null, leave: TimeInterpolator? = enter): BaseItemAnimator {
        mEnterInterceptor = enter
        mLeaveInterceptor = leave
        return this
    }

    protected fun getLeaveDelay(holder: ViewHolder): Long = abs(holder.oldPosition * removeDuration / 4)
    protected fun getEnterDelay(holder: ViewHolder): Long = abs(holder.bindingAdapterPosition * addDuration / 4)

    protected open fun onPrepareLeave(holder: ViewHolder) {}
    protected open fun onPrepareEnter(holder: ViewHolder) {}

    protected abstract fun onAnimateLeave(holder: ViewHolder)
    protected abstract fun onAnimateEnter(holder: ViewHolder)

    override fun runPendingAnimations() {
        val removalsPending = mPendingRemovals.isNotEmpty()
        val movesPending = mPendingMoves.isNotEmpty()
        val changesPending = mPendingChanges.isNotEmpty()
        val additionsPending = mPendingAdditions.isNotEmpty()
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            // nothing to animate
            return
        }
        // First, remove stuff
        for (holder in mPendingRemovals) {
            animateRemoveImpl(holder)
        }
        mPendingRemovals.clear()
        // Next, move stuff
        if (movesPending) {
            val moves = ArrayList<MoveInfo>()
            moves.addAll(mPendingMoves)
            mMovesList.add(moves)
            mPendingMoves.clear()
            val mover = Runnable {
                val removed = mMovesList.remove(moves)
                if (!removed) {
                    // already canceled
                    return@Runnable
                }
                for (moveInfo in moves) {
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY)
                }
                moves.clear()
            }
            if (removalsPending) {
                val view: View = moves[0].holder.itemView
                view.postOnAnimationDelayed(mover, removeDuration)
            } else {
                mover.run()
            }
        }
        // Next, change stuff, to run in parallel with move animations
        if (changesPending) {
            val changes = ArrayList<ChangeInfo>()
            changes.addAll(mPendingChanges)
            mChangesList.add(changes)
            mPendingChanges.clear()
            val changer = Runnable {
                val removed = mChangesList.remove(changes)
                if (!removed) {
                    // already canceled
                    return@Runnable
                }
                for (change in changes) {
                    animateChangeImpl(change)
                }
                changes.clear()
            }
            if (removalsPending) {
                val view: View = changes[0].oldHolder!!.itemView
                view.postOnAnimationDelayed(changer, removeDuration)
            } else {
                changer.run()
            }
        }
        // Next, add stuff
        if (additionsPending) {
            val additions = ArrayList<ViewHolder>()
            additions.addAll(mPendingAdditions)
            mAdditionsList.add(additions)
            mPendingAdditions.clear()
            val adder = Runnable {
                val removed = mAdditionsList.remove(additions)
                if (!removed) {
                    // already canceled
                    return@Runnable
                }
                for (holder in additions) {
                    animateAddImpl(holder)
                }
                additions.clear()
            }
            if (removalsPending || movesPending || changesPending) {
                val removeDuration = if (removalsPending) removeDuration else 0
                val moveDuration = if (movesPending) moveDuration else 0
                val changeDuration = if (changesPending) changeDuration else 0
                val totalDelay = removeDuration + Math.max(moveDuration, changeDuration)
                val view: View = additions[0].itemView
                view.postOnAnimationDelayed(adder, totalDelay)
            } else {
                adder.run()
            }
        }
    }

    override fun animateRemove(holder: ViewHolder): Boolean {
        endAnimation(holder)
        holder.itemView.reset()
        onPrepareLeave(holder)
        mPendingRemovals.add(holder)
        return true
    }


    private fun animateRemoveImpl(holder: ViewHolder) {

        onAnimateLeave(holder)
        holder.itemView.animate()
            .setDuration(removeDuration)
            .setInterpolator(mLeaveInterceptor)
            .setStartDelay(getLeaveDelay(holder))
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    dispatchRemoveStarting(holder)
                }

                override fun onAnimationCancel(animation: Animator) {
                    holder.itemView.reset()
                }

                override fun onAnimationEnd(animation: Animator) {
                    holder.itemView.reset()
                    dispatchRemoveFinished(holder)
                    mRemoveAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            })
            .start()
        mRemoveAnimations.add(holder)
    }

    override fun animateAdd(holder: ViewHolder): Boolean {
        endAnimation(holder)
        holder.itemView.reset()
        onPrepareEnter(holder)
        mPendingAdditions.add(holder)
        return true
    }

    private fun animateAddImpl(holder: ViewHolder) {
        onAnimateEnter(holder)
        holder.itemView.animate()
            .setDuration(addDuration)
            .setInterpolator(mEnterInterceptor)
            .setStartDelay(getEnterDelay(holder))
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    dispatchAddStarting(holder)
                }

                override fun onAnimationCancel(animation: Animator) {
                    holder.itemView.reset()
                }

                override fun onAnimationEnd(animation: Animator) {
                    holder.itemView.reset()
                    dispatchAddFinished(holder)
                    mAddAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            })
            .start()
        mAddAnimations.add(holder)
    }

    override fun animateMove(holder: ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        endAnimation(holder)
        val deltaX = toX - fromX - holder.itemView.translationX.toInt()
        val deltaY = toY - fromY - holder.itemView.translationY.toInt()
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder)
            return false
        }
        if (deltaX != 0) {
            holder.itemView.translationX = -deltaX.toFloat()
        }
        if (deltaY != 0) {
            holder.itemView.translationY = -deltaY.toFloat()
        }
        mPendingMoves.add(MoveInfo(holder, fromX, fromY, toX, toY))
        return true
    }

    private fun animateMoveImpl(holder: ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val view: View = holder.itemView
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX != 0) {
            view.animate().translationX(0f)
        }
        if (deltaY != 0) {
            view.animate().translationY(0f)
        }
        // TODO: make EndActions end listeners instead, since end actions aren't called when
        // vpas are canceled (and can't end them. why?)
        // need listener functionality in VPACompat for this. Ick.
        val animation = view.animate()
        mMoveAnimations.add(holder)
        animation.setDuration(moveDuration).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animator: Animator) {
                dispatchMoveStarting(holder)
            }

            override fun onAnimationCancel(animator: Animator) {
                if (deltaX != 0) {
                    view.translationX = 0f
                }
                if (deltaY != 0) {
                    view.translationY = 0f
                }
            }

            override fun onAnimationEnd(animator: Animator) {
                animation.setListener(null)
                dispatchMoveFinished(holder)
                mMoveAnimations.remove(holder)
                dispatchFinishedWhenDone()
            }
        }).start()
    }

    override fun animateChange(oldHolder: ViewHolder, newHolder: ViewHolder?, fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int): Boolean {
        if (oldHolder === newHolder) {
            // Don't know how to run change animations when the same view holder is re-used.
            // run a move animation to handle position changes.
            return animateMove(oldHolder, fromLeft, fromTop, toLeft, toTop)
        }
        val prevTranslationX: Float = oldHolder.itemView.translationX
        val prevTranslationY: Float = oldHolder.itemView.translationY
        val prevAlpha: Float = oldHolder.itemView.alpha
        endAnimation(oldHolder)
        val deltaX = (toLeft - fromLeft - prevTranslationX)
        val deltaY = (toTop - fromTop - prevTranslationY)
        // recover prev translation state after ending animation
        oldHolder.itemView.translationX = prevTranslationX
        oldHolder.itemView.translationY = prevTranslationY
        oldHolder.itemView.alpha = prevAlpha
        if (newHolder != null) {
            // carry over translation values
            endAnimation(newHolder)
            newHolder.itemView.translationX = -deltaX
            newHolder.itemView.translationY = -deltaY
            newHolder.itemView.alpha = 0f
        }
        mPendingChanges.add(ChangeInfo(oldHolder, newHolder, fromLeft, fromTop, toLeft, toTop))
        return true
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val oldView: View? = changeInfo.oldHolder?.itemView
        val newView: View? = changeInfo.newHolder?.itemView
        if (oldView != null) {
            mChangeAnimations.add(changeInfo.oldHolder)
            val oldViewAnim = oldView.animate().setDuration(changeDuration)
            oldViewAnim.translationX((changeInfo.toX - changeInfo.fromX).toFloat())
            oldViewAnim.translationY((changeInfo.toY - changeInfo.fromY).toFloat())
            oldViewAnim.alpha(0f).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    dispatchChangeStarting(changeInfo.oldHolder, true)
                }

                override fun onAnimationEnd(animator: Animator) {
                    oldViewAnim.setListener(null)
                    oldView.alpha = 1f
                    oldView.translationX = 0f
                    oldView.translationY = 0f
                    dispatchChangeFinished(changeInfo.oldHolder, true)
                    mChangeAnimations.remove(changeInfo.oldHolder)
                    dispatchFinishedWhenDone()
                }
            }).start()
        }
        if (newView != null) {
            val newViewAnimation = newView.animate()
            mChangeAnimations.add(changeInfo.newHolder)
            newViewAnimation.alpha(1f).translationX(0f).translationY(0f).setDuration(changeDuration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animator: Animator) {
                        dispatchChangeStarting(changeInfo.newHolder, false)
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        newViewAnimation.setListener(null)
                        newView.alpha = 1f
                        newView.translationX = 0f
                        newView.translationY = 0f
                        dispatchChangeFinished(changeInfo.newHolder, false)
                        mChangeAnimations.remove(changeInfo.newHolder)
                        dispatchFinishedWhenDone()
                    }
                }).start()
        }
    }

    private fun endChangeAnimation(infoList: MutableList<ChangeInfo>, item: ViewHolder) {
        for (i in infoList.indices.reversed()) {
            val changeInfo = infoList[i]
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo)
                }
            }
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo) {
        changeInfo.oldHolder?.let {
            endChangeAnimationIfNecessary(changeInfo, it)
        }
        changeInfo.newHolder?.let {
            endChangeAnimationIfNecessary(changeInfo, it)
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo, item: ViewHolder): Boolean {
        var oldItem = false
        if (changeInfo.newHolder === item) {
            changeInfo.newHolder = null
        } else if (changeInfo.oldHolder === item) {
            changeInfo.oldHolder = null
            oldItem = true
        } else {
            return false
        }
        item.itemView.alpha = 1f
        item.itemView.translationX = 0f
        item.itemView.translationY = 0f
        dispatchChangeFinished(item, oldItem)
        return true
    }


    override fun endAnimation(item: ViewHolder) {
        val view: View = item.itemView
        // this will trigger end callback which should set properties to their target values.
        view.animate().cancel()
        // TODO if some other animations are chained to end, how do we cancel them as well?
        for (i in mPendingMoves.indices.reversed()) {
            val moveInfo = mPendingMoves[i]
            if (moveInfo.holder === item) {
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(item)
                mPendingMoves.removeAt(i)
            }
        }
        endChangeAnimation(mPendingChanges, item)
        if (mPendingRemovals.remove(item)) {
            view.reset()
            dispatchRemoveFinished(item)
        }
        if (mPendingAdditions.remove(item)) {
            view.reset()
            dispatchAddFinished(item)
        }
        for (i in mChangesList.indices.reversed()) {
            val changes = mChangesList[i]
            endChangeAnimation(changes, item)
            if (changes.isEmpty()) {
                mChangesList.removeAt(i)
            }
        }
        for (i in mMovesList.indices.reversed()) {
            val moves = mMovesList[i]
            for (j in moves.indices.reversed()) {
                val moveInfo = moves[j]
                if (moveInfo.holder === item) {
                    view.translationY = 0f
                    view.translationX = 0f
                    dispatchMoveFinished(item)
                    moves.removeAt(j)
                    if (moves.isEmpty()) {
                        mMovesList.removeAt(i)
                    }
                    break
                }
            }
        }
        for (i in mAdditionsList.indices.reversed()) {
            val additions = mAdditionsList[i]
            if (additions.remove(item)) {
                view.reset()
                dispatchAddFinished(item)
                if (additions.isEmpty()) {
                    mAdditionsList.removeAt(i)
                }
            }
        }

        // animations should be ended by the cancel above.
        check(!(mRemoveAnimations.remove(item) && DEBUG)) {
            ("after animation is cancelled, item should not be in mRemoveAnimations list")
        }
        check(!(mAddAnimations.remove(item) && DEBUG)) {
            ("after animation is cancelled, item should not be in mAddAnimations list")
        }
        check(!(mChangeAnimations.remove(item) && DEBUG)) {
            ("after animation is cancelled, item should not be in mChangeAnimations list")
        }
        check(!(mMoveAnimations.remove(item) && DEBUG)) {
            ("after animation is cancelled, item should not be in mMoveAnimations list")
        }
        dispatchFinishedWhenDone()
    }

    override fun isRunning(): Boolean {
        return (mPendingAdditions.isNotEmpty()
                || mPendingChanges.isNotEmpty()
                || mPendingMoves.isNotEmpty()
                || mPendingRemovals.isNotEmpty()
                || mMoveAnimations.isNotEmpty()
                || mRemoveAnimations.isNotEmpty()
                || mAddAnimations.isNotEmpty()
                || mChangeAnimations.isNotEmpty()
                || mMovesList.isNotEmpty()
                || mAdditionsList.isNotEmpty()
                || mChangesList.isNotEmpty())
    }

    override fun endAnimations() {
        var count = mPendingMoves.size
        for (i in count - 1 downTo 0) {
            val item = mPendingMoves[i]
            val view: View = item.holder.itemView
            view.translationY = 0f
            view.translationX = 0f
            dispatchMoveFinished(item.holder)
            mPendingMoves.removeAt(i)
        }
        count = mPendingRemovals.size
        for (i in count - 1 downTo 0) {
            val item: ViewHolder = mPendingRemovals[i]
            dispatchRemoveFinished(item)
            mPendingRemovals.removeAt(i)
        }
        count = mPendingAdditions.size
        for (i in count - 1 downTo 0) {
            val item: ViewHolder = mPendingAdditions[i]
            item.itemView.reset()
            dispatchAddFinished(item)
            mPendingAdditions.removeAt(i)
        }
        count = mPendingChanges.size
        for (i in count - 1 downTo 0) {
            endChangeAnimationIfNecessary(mPendingChanges[i])
        }
        mPendingChanges.clear()
        if (!isRunning) {
            return
        }
        var listCount = mMovesList.size
        for (i in listCount - 1 downTo 0) {
            val moves = mMovesList[i]
            count = moves.size
            for (j in count - 1 downTo 0) {
                val moveInfo = moves[j]
                val item = moveInfo.holder
                val view = item.itemView
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(moveInfo.holder)
                moves.removeAt(j)
                if (moves.isEmpty()) {
                    mMovesList.remove(moves)
                }
            }
        }
        listCount = mAdditionsList.size
        for (i in listCount - 1 downTo 0) {
            val additions = mAdditionsList[i]
            count = additions.size
            for (j in count - 1 downTo 0) {
                val item = additions[j]
                val view = item.itemView
                view.alpha = 1f
                dispatchAddFinished(item)
                if (j < additions.size) {
                    additions.removeAt(j)
                }
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions)
                }
            }
        }
        listCount = mChangesList.size
        for (i in listCount - 1 downTo 0) {
            val changes = mChangesList[i]
            count = changes.size
            for (j in count - 1 downTo 0) {
                endChangeAnimationIfNecessary(changes[j])
                if (changes.isEmpty()) {
                    mChangesList.remove(changes)
                }
            }
        }
        cancelAll(mRemoveAnimations)
        cancelAll(mMoveAnimations)
        cancelAll(mAddAnimations)
        cancelAll(mChangeAnimations)
        dispatchAnimationsFinished()
    }

    override fun canReuseUpdatedViewHolder(viewHolder: ViewHolder, payloads: List<Any?>): Boolean {
        return payloads.isNotEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads)
    }

    private fun cancelAll(viewHolders: List<ViewHolder?>) {
        for (i in viewHolders.indices.reversed()) {
            val holder = viewHolders[i] ?: continue
            holder.itemView.animate().cancel()
        }
    }

    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    private fun View.reset() {
        alpha = 1f
        scaleX = 1f
        scaleY = 1f
        translationX = 0f
        translationY = 0f
        rotation = 0f
        rotationX = 0f
        rotationY = 0f
        pivotX = measuredWidth / 2f
        pivotY = measuredHeight / 2f
        animate().setInterpolator(null).setListener(null).setStartDelay(0)
    }

}