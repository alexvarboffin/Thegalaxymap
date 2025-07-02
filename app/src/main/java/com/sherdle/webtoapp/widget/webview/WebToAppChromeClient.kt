package com.sherdle.webtoapp.widget.webview

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Message
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sherdle.webtoapp.Config
import com.sherdle.webtoapp.activity.MainActivity
import com.sherdle.webtoapp.fragment.WebFragment
import com.sherdle.webtoapp.widget.AdvancedWebView
import ru.thegalaxymap.app.R

class WebToAppChromeClient(
    protected var fragment: WebFragment,
    protected var container: FrameLayout,
    protected var browser: AdvancedWebView,
    var swipeLayout: SwipeRefreshLayout,
    var progressBar: ProgressBar
) : WebChromeClient() {
    var customView: View? = null
    var customViewCallback: CustomViewCallback? = null
    private var mOriginalOrientation = 0
    private var mOriginalSystemUiVisibility = 0
    protected var popupView: WebView? = null

    // android.webkit.WebChromeClient
    override fun onCreateWindow(
        webView: WebView?,
        z: Boolean,
        z2: Boolean,
        message: Message
    ): Boolean {
        this.browser.setVisibility(View.GONE)
        val webView2 = WebView(this.fragment.requireActivity())
        this.popupView = webView2
        webView2.getSettings().setJavaScriptEnabled(true)
        this.popupView!!.setWebChromeClient(this)
        this.popupView!!.setWebViewClient(WebToAppWebClient(this.fragment, this.popupView))
        this.popupView!!.setLayoutParams(RelativeLayout.LayoutParams(-1, -1))
        this.container.addView(this.popupView)
        (message.obj as WebView.WebViewTransport).setWebView(this.popupView)
        message.sendToTarget()
        return true
    }

    // android.webkit.WebChromeClient
    override fun onCloseWindow(webView: WebView?) {
        this.popupView!!.setVisibility(View.GONE)
        this.browser.setVisibility(View.VISIBLE)
    }

    // android.webkit.WebChromeClient
    override fun onProgressChanged(webView: WebView?, i: Int) {

        if (Config.LOAD_AS_PULL) {
            swipeLayout.setRefreshing(true)
            if (i == 100) {
                this.swipeLayout.setRefreshing(false)
                return
            }
            return
        }
        this.progressBar.setProgress(0)
        this.progressBar.setVisibility(View.VISIBLE)
        this.progressBar.setProgress(i)
        this.progressBar.incrementProgressBy(i)
        if (i > 99) {
            this.progressBar.setVisibility(View.GONE)
            val swipeRefreshLayout2 = this.swipeLayout
            if (swipeRefreshLayout2 != null && swipeRefreshLayout2.isRefreshing()) {
                this.swipeLayout.setRefreshing(false)
            }
        }
        if (i > 80) {
            try {
                (this.fragment.getActivity() as MainActivity).hideSplash()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // android.webkit.WebChromeClient
    override fun onPermissionRequest(permissionRequest: PermissionRequest) {
        if (Build.VERSION.SDK_INT >= 21) {
            permissionRequest.grant(permissionRequest.getResources())
        }
    }

    // android.webkit.WebChromeClient
    override fun onReceivedTitle(webView: WebView?, str: String?) {
        (this.fragment.getActivity() as MainActivity).setTitle(this.browser.getTitle())
    }

    // android.webkit.WebChromeClient
    override fun getDefaultVideoPoster(): Bitmap? {
        if (this.fragment.getActivity() == null) {
            return null
        }
        return BitmapFactory.decodeResource(
            this.fragment.requireActivity().getApplicationContext().getResources(),
            R.drawable.vert_loading
        )
    }

    // android.webkit.WebChromeClient
    override fun onShowCustomView(view: View, customViewCallback: CustomViewCallback?) {
        if (this.customView != null) {
            onHideCustomView()
            return
        }
        this.customView = view
        view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK)
        if (Build.VERSION.SDK_INT >= 11) {
            this.mOriginalSystemUiVisibility =
                this.fragment.requireActivity().getWindow().getDecorView().getSystemUiVisibility()
        }
        this.mOriginalOrientation = this.fragment.requireActivity().getRequestedOrientation()
        this.customViewCallback = customViewCallback
        (this.fragment.requireActivity().getWindow().getDecorView() as FrameLayout).addView(
            this.customView,
            FrameLayout.LayoutParams(-1, -1)
        )
        this.fragment.requireActivity().getWindow().getDecorView().setSystemUiVisibility(3846)
        this.fragment.requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    }

    // android.webkit.WebChromeClient
    override fun onHideCustomView() {
        (this.fragment.requireActivity().getWindow()
            .getDecorView() as FrameLayout).removeView(this.customView)
        this.customView = null
        if (Build.VERSION.SDK_INT >= 11) {
            this.fragment.requireActivity().getWindow().getDecorView()
                .setSystemUiVisibility(this.mOriginalSystemUiVisibility)
        }
        this.fragment.requireActivity().setRequestedOrientation(this.mOriginalOrientation)
        this.customViewCallback!!.onCustomViewHidden()
        this.customViewCallback = null
    }
}
