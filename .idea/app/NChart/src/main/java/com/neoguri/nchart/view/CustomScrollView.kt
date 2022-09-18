package com.neoguri.nchart.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class CustomScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ScrollView(context, attrs) {
    private var isScrollable = true

    fun setScrollingEnabled(scrollable: Boolean) {
        isScrollable = scrollable
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isScrollable) super.onTouchEvent(ev) else isScrollable
            }
            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (!isScrollable) false else super.onInterceptTouchEvent(ev)
    }
}