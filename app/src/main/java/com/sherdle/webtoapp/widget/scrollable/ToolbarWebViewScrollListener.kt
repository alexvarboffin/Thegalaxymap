package com.sherdle.webtoapp.widget.scrollable

import com.sherdle.webtoapp.widget.AdvancedWebView
import com.sherdle.webtoapp.widget.AdvancedWebView.ScrollInterface

abstract class ToolbarWebViewScrollListener : ScrollInterface {
    private var mScrolledDistance = 0
    private var mControlsVisible = true

    abstract fun onHide()

    abstract fun onShow()

    // com.sherdle.webtoapp.widget.AdvancedWebView.ScrollInterface
    override fun onScrollChanged(
        advancedWebView: AdvancedWebView,
        i: Int,
        i2: Int,
        i3: Int,
        i4: Int
    ) {
        if (advancedWebView.scrollY == 0) {
            if (!this.mControlsVisible) {
                onShow()
                this.mControlsVisible = true
            }
        } else {
            val i5 = this.mScrolledDistance
            if (i5 > HIDE_THRESHOLD && this.mControlsVisible) {
                onHide()
                this.mControlsVisible = false
                this.mScrolledDistance = 0
            } else if (i5 < -150 && !this.mControlsVisible) {
                onShow()
                this.mControlsVisible = true
                this.mScrolledDistance = 0
            }
        }
        val z = this.mControlsVisible
        if ((!z || i2 - i4 <= 0) && (z || i2 - i4 >= 0)) {
            return
        }
        this.mScrolledDistance += i2 - i4
    }

    companion object {
        private const val HIDE_THRESHOLD = 150
    }
}
