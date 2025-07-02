package com.sherdle.webtoapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class SwipeableViewPager(context: Context, attributeSet: AttributeSet?) :
    ViewPager(context, attributeSet) {

    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        if (swipeEnabled()) {
            return super.onTouchEvent(motionEvent)
        }
        return false
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent?): Boolean {
        if (swipeEnabled()) {
            return super.onInterceptTouchEvent(motionEvent)
        }
        return false
    }

    fun swipeEnabled(): Boolean {
        return if (adapter!!.count == 1 || ALWAYS_IGNORE_SWIPE) false else true
    }

    companion object {
        private const val ALWAYS_IGNORE_SWIPE = false
    }
}
