package com.sherdle.webtoapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.webkit.WebSettings
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.sherdle.webtoapp.App
import com.sherdle.webtoapp.Config
import com.sherdle.webtoapp.activity.MainActivity
import com.sherdle.webtoapp.util.GetFileInfo
import com.sherdle.webtoapp.widget.AdvancedWebView
import com.sherdle.webtoapp.widget.scrollable.ToolbarWebViewScrollListener
import com.sherdle.webtoapp.widget.webview.WebToAppChromeClient
import com.sherdle.webtoapp.widget.webview.WebToAppWebClient
import ru.thegalaxymap.app.R
import java.util.concurrent.ExecutionException
import androidx.core.view.isVisible

class WebFragment : Fragment(), AdvancedWebView.Listener, OnRefreshListener {
    @JvmField
    var browser: AdvancedWebView? = null
    @JvmField
    var chromeClient: WebToAppChromeClient? = null
    var progressBar: ProgressBar? = null
    @JvmField
    var rl: FrameLayout? = null
    var swipeLayout: SwipeRefreshLayout? = null
    var webClient: WebToAppWebClient? = null
    @JvmField
    var mainUrl: String? = null
    var firstLoad: Int = 0
    private var clearHistory = false

    override fun onExternalPageRequest(str: String?) {
    }

    override fun onPageError(i: Int, str: String?, str2: String?) {
    }

    fun setBaseUrl(str: String?) {
        this.mainUrl = str
        this.clearHistory = true
        this.browser!!.loadUrl(str!!)
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        if (getArguments() == null || this.mainUrl != null) {
            return
        }
        this.mainUrl = getArguments()!!.getString(URL)
        this.firstLoad = 0
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        val frameLayout = layoutInflater.inflate(
            R.layout.fragment_observable_web_view,
            viewGroup,
            false
        ) as FrameLayout
        this.rl = frameLayout
        this.progressBar = frameLayout.findViewById<ProgressBar>(R.id.progressbar)
        this.browser = this.rl!!.findViewById<AdvancedWebView>(R.id.scrollable)
        this.swipeLayout = this.rl!!.findViewById<SwipeRefreshLayout>(R.id.swipe_container)
        return this.rl!!
    }

    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        if (Config.PULL_TO_REFRESH) {
            this.swipeLayout!!.setOnRefreshListener(this)
        } else {
            this.swipeLayout!!.setEnabled(false)
        }
        this.browser!!.setListener(this, this)
        if (MainActivity.getCollapsingActionBar()) {
            (getActivity() as MainActivity).showToolbar(this)
            this.browser!!.setOnScrollChangeListener(
                this.browser,
                object : ToolbarWebViewScrollListener() {
                    // from class: com.sherdle.webtoapp.fragment.WebFragment.1
                    // com.sherdle.webtoapp.widget.scrollable.ToolbarWebViewScrollListener
                    public override fun onHide() {
                        (this@WebFragment.getActivity() as MainActivity).hideToolbar()
                    }

                    // com.sherdle.webtoapp.widget.scrollable.ToolbarWebViewScrollListener
                    public override fun onShow() {
                        (this@WebFragment.getActivity() as MainActivity).showToolbar(this@WebFragment)
                    }
                })
        }
        this.browser!!.requestFocus()
        this.browser!!.getSettings().setJavaScriptEnabled(true)
        this.browser!!.getSettings().setBuiltInZoomControls(false)
        //this.browser.getSettings().setAppCacheEnabled(true);
        this.browser!!.getSettings().setDatabaseEnabled(true)
        this.browser!!.getSettings().setDomStorageEnabled(true)
        this.browser!!.setGeolocationEnabled(true)
        this.browser!!.getSettings().setPluginState(WebSettings.PluginState.ON)
        if (Config.MULTI_WINDOWS) {
            this.browser!!.getSettings().setJavaScriptCanOpenWindowsAutomatically(true)
            this.browser!!.getSettings().setSupportMultipleWindows(true)
        }
        val webToAppWebClient = WebToAppWebClient(this, this.browser)
        this.webClient = webToAppWebClient
        this.browser!!.setWebViewClient(webToAppWebClient)
        val webToAppChromeClient = WebToAppChromeClient(
            this,
            this.rl!!,
            this.browser!!,
            this.swipeLayout!!,
            this.progressBar!!
        )
        this.chromeClient = webToAppChromeClient
        this.browser!!.setWebChromeClient(webToAppChromeClient)
        if (this.webClient!!.hasConnectivity(this.mainUrl, true)) {
            val pushUrl = (requireActivity().getApplication() as App).getPushUrl()
            if (pushUrl != null) {
                this.browser!!.loadUrl(pushUrl)
                return
            } else {
                this.browser!!.loadUrl(this.mainUrl!!)
                return
            }
        }
        try {
            (getActivity() as MainActivity).hideSplash()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
    override fun onRefresh() {
        this.browser!!.reload()
    }

    override fun onPause() {
        super.onPause()
        this.browser!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.browser!!.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        this.browser!!.onResume()
    }

    @SuppressLint("NewApi")
    override fun onDownloadRequested(
        url: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimetype: String?,
        contentLength: Long
    ) {
        if (!hasPermissionToDownload(requireActivity())) return

        var filename: String? = null
        try {
            filename = GetFileInfo().execute(url).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        if (filename == null) {
            val fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(url)
            filename = URLUtil.guessFileName(url, null, fileExtenstion)
        }


        if (AdvancedWebView.handleDownload(getActivity(), url, filename)) {
            Toast.makeText(
                getActivity(),
                getResources().getString(com.sherdle.webtoapp.R.string.download_done),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                getActivity(),
                getResources().getString(com.sherdle.webtoapp.R.string.download_fail),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPageStarted(str: String?, bitmap: Bitmap?) {
        Log.d(TAG, "onPageStarted: " + firstLoad)
        if (this.firstLoad == 0 && MainActivity.getCollapsingActionBar()) {
            (getActivity() as MainActivity).showToolbar(this)
            this.firstLoad = 1
        } else if (this.firstLoad == 0) {
            this.firstLoad = 1
        }
    }

    override fun onPageFinished(str: String) {
        if ((str != this.mainUrl) && getActivity() != null && (getActivity() is MainActivity)) {
            (getActivity() as MainActivity).showInterstitial()
        }
        Log.d(TAG, "onPageFinished: ")
        try {
            (getActivity() as MainActivity).hideSplash()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (this.clearHistory) {
            this.clearHistory = false
            this.browser!!.clearHistory()
        }
        hideErrorScreen()
    }

    override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        super.onActivityResult(i, i2, intent)
        this.browser!!.onActivityResult(i, i2, intent)
    }

    fun shareURL() {
        val intent = Intent("android.intent.action.SEND")
        intent.setType("text/plain")
        intent.putExtra(
            "android.intent.extra.TEXT", String.format(
                getString(R.string.share_body), this.browser!!.getTitle(), getString(
                    R.string.app_name
                ) + " https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName()
            )
        )
        startActivity(Intent.createChooser(intent, getText(R.string.sharetitle)))
    }

    fun showErrorScreen(str: String?) {
        val findViewById = this.rl!!.findViewById<View>(R.id.empty_view)
        findViewById.visibility = View.VISIBLE
        (findViewById.findViewById<View?>(R.id.title) as TextView).setText(str)
        // from class: com.sherdle.webtoapp.fragment.WebFragment.3
// android.view.View.OnClickListener
        findViewById.findViewById<View?>(R.id.retry_button)
            .setOnClickListener(View.OnClickListener { view: View? ->
                if (this@WebFragment.browser!!.getUrl() == null) {
                    this@WebFragment.browser!!.loadUrl(this@WebFragment.mainUrl!!)
                } else {
                    this@WebFragment.browser!!.loadUrl("javascript:document.open();document.close();")
                    this@WebFragment.browser!!.reload()
                }
            })
    }

    fun hideErrorScreen() {
        val findViewById = this.rl!!.findViewById<View>(R.id.empty_view)
        if (findViewById.isVisible) {
            findViewById.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "@@@"

        var URL: String = "url"
        @JvmStatic
        fun newInstance(str: String?): WebFragment {
            val webFragment = WebFragment()
            val bundle = Bundle()
            bundle.putString(URL, str)
            webFragment.setArguments(bundle)
            return webFragment
        }

        private fun hasPermissionToDownload(activity: Activity): Boolean {
            if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(
                    activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                ) == 0
            ) {
                return true
            }
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(R.string.download_permission_explaination)
            builder.setPositiveButton(
                R.string.common_permission_grant,
                object : DialogInterface.OnClickListener {
                    // from class: com.sherdle.webtoapp.fragment.WebFragment.2
                    // android.content.DialogInterface.OnClickListener
                    override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            activity.requestPermissions(
                                arrayOf<String>("android.permission.WRITE_EXTERNAL_STORAGE"),
                                1
                            )
                        }
                    }
                })
            builder.create().show()
            return false
        }
    }
}
