package com.sherdle.webtoapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;


public class SwipeableViewPager extends ViewPager {
    private static boolean ALWAYS_IGNORE_SWIPE = false;

    public SwipeableViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (swipeEnabled()) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (swipeEnabled()) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    boolean swipeEnabled() {
        return (getAdapter().getCount() == 1 || ALWAYS_IGNORE_SWIPE) ? false : true;
    }
}
