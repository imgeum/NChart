package com.neoguri.nchart.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import androidx.annotation.AttrRes
import com.neoguri.nchart.chart.NChart

class CustomHorizontalScrollView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : HorizontalScrollView (context, attrs, defStyleAttr), View.OnScrollChangeListener {

    private var mDrawView: NChart? = null
    private var mScrollable = true

    init {
        setOnScrollChangeListener(this)
    }

    fun setDrawView(nChart: NChart) {
        mDrawView = nChart
    }

    override fun onScrollChange(
        v: View?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        mDrawView?.horizontalXScrollSet(scrollX)
    }

    fun setScrollingEnabled(enabled: Boolean) {
        mScrollable = enabled
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN ->{
                mScrollable && super.onTouchEvent(ev)
            }
            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return mScrollable && super.onInterceptTouchEvent(ev)
    }

}