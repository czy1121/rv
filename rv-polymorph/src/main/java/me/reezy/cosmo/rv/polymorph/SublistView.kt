package me.reezy.cosmo.rv.polymorph

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.ancestors
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlin.math.abs


class SublistView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RecyclerView(context, attrs) {

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        overScrollMode = OVER_SCROLL_NEVER
        isNestedScrollingEnabled = false
        itemAnimator = null
    }

    private var initialized: Boolean = false
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (initialized) {
            val prv = ancestors.find { it is RecyclerView } as? RecyclerView ?: return
            setRecycledViewPool(prv.recycledViewPool)
            initialized = true
        }
    }

    /**
     * layout => -1: flex-start, -2: flex-space-between, 0: horizontal, 1:
     * vertical 2: grid-2, 3: grid-3, 4: grid-4, 5: grid-5
     */
    var layout: Int = Int.MAX_VALUE
        set(value) {
            if (value == field) return
            field = value
            layoutManager = when {
                value > 1 -> GridLayoutManager(context, value)
                value == 1 -> LinearLayoutManager(context, VERTICAL, false)
                value == 0 -> LinearLayoutManager(context, HORIZONTAL, false)
                value == -1 -> FlexboxLayoutManagerCompat(context).apply {
                    justifyContent = JustifyContent.FLEX_START
                }
                else -> FlexboxLayoutManagerCompat(context).apply {
                    justifyContent = JustifyContent.SPACE_BETWEEN
                }
            }

            attach(this)
        }

    private var listener: HorizonOnItemTouchListener? = null


    private fun attach(rv: RecyclerView) {
        listener?.let {
            removeOnItemTouchListener(it)
        }

        val manager = rv.layoutManager
        listener = if (manager is LinearLayoutManager && manager.canScrollHorizontally()) {
            HorizonOnItemTouchListener(manager) { isUserInputEnabled ->
                (rv.ancestors.find { it is ViewPager2 } as? ViewPager2)?.isUserInputEnabled = isUserInputEnabled
            }
        } else null

        listener?.let {
            addOnItemTouchListener(it)
        }
    }


    private class HorizonOnItemTouchListener(private val manager: LinearLayoutManager, private val setParentTouchable: (Boolean) -> Unit) : OnItemTouchListener {


        private var lastX = 0f
        private var lastY = 0f
        private var isMoving = false

        private fun canScrollHorizontally(delta: Float): Boolean {
            return when {
                // scroll right
                delta < 0f -> manager.findLastCompletelyVisibleItemPosition() < (manager.itemCount - 1)
                // scroll left
                delta > 0f -> manager.findFirstCompletelyVisibleItemPosition() > 0
                else -> false
            }
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = e.x
                    lastY = e.y
                    setParentTouchable(false)
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = e.x - lastX
                    val deltaY = e.y - lastY
                    if (abs(deltaX) > abs(deltaY) * 0.5f && !isMoving) {
                        setParentTouchable(!canScrollHorizontally(deltaX))
                        isMoving = true
                    }
                    lastX = e.x
                    lastY = e.y
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    setParentTouchable(true)
                    isMoving = false
                    lastX = 0f
                    lastY = 0f
                }
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

    private class FlexboxLayoutManagerCompat(context: Context, flexDirection: Int = FlexDirection.ROW, flexWrap: Int = FlexWrap.WRAP) : FlexboxLayoutManager(context, flexDirection, flexWrap) {

        override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
            return when (lp) {
                is LayoutParams -> lp
                else -> LayoutParams(lp)
            }
        }
    }


}