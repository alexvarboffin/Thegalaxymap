package com.sherdle.webtoapp.widget.webview

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import com.sherdle.webtoapp.Config
import com.sherdle.webtoapp.fragment.WebFragment
import com.sherdle.webtoapp.widget.AdvancedWebView
import ru.thegalaxymap.app.R
import androidx.core.net.toUri

class WebToAppWebClient(var fragment: WebFragment, var browser: WebView) : WebViewClient() {
    
    override fun onPageFinished(webView: WebView?, str: String?) {
    }

    
    override fun shouldOverrideUrlLoading(
        webView: WebView,
        webResourceRequest: WebResourceRequest
    ): Boolean {
        return shouldOverrideUrlLoading(webView, webResourceRequest.getUrl().toString())
    }

    
    override fun shouldOverrideUrlLoading(webView: WebView, str: String): Boolean {
        if (urlShouldOpenExternally(str)) {
            try {
                webView.getContext()
                    .startActivity(Intent("android.intent.action.VIEW", Uri.parse(str)))
            } catch (unused: ActivityNotFoundException) {
                if (str.startsWith("intent://")) {
                    webView.getContext().startActivity(
                        Intent(
                            "android.intent.action.VIEW",
                            str.replace("intent://", "http://").toUri()
                        )
                    )
                } else {
                    Toast.makeText(
                        this.fragment.getActivity(),
                        this.fragment.requireActivity().getResources().getString(
                            R.string.no_app_message
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            return true
        }
        if (str.endsWith(".mp4") || str.endsWith(".avi") || str.endsWith(".flv")) {
            try {
                val intent = Intent("android.intent.action.VIEW")
                intent.setDataAndType(Uri.parse(str), "video/mp4")
                webView.getContext().startActivity(intent)
            } catch (unused2: Exception) {
            }
            return true
        }
        if (str.endsWith(".mp3") || str.endsWith(".wav")) {
            try {
                val intent2 = Intent("android.intent.action.VIEW")
                intent2.setDataAndType(str.toUri(), "audio/mp3")
                webView.getContext().startActivity(intent2)
            } catch (unused3: Exception) {
            }
            return true
        }
        return super.shouldOverrideUrlLoading(webView, str)
    }

    
    override fun onReceivedError(
        webView: WebView,
        webResourceRequest: WebResourceRequest,
        webResourceError: WebResourceError
    ) {
        onReceivedError(
            webView,
            webResourceError.getErrorCode(),
            webResourceError.getDescription().toString(),
            webResourceRequest.getUrl().toString()
        )
    }

    
    override fun onReceivedSslError(
        webView: WebView?,
        sslErrorHandler: SslErrorHandler,
        sslError: SslError?
    ) {
        val builder = AlertDialog.Builder(this.fragment.requireActivity())
        builder.setMessage(R.string.notification_error_ssl_cert_invalid)
        builder.setPositiveButton(R.string.yes, object : DialogInterface.OnClickListener {
            // from class: com.sherdle.webtoapp.widget.webview.WebToAppWebClient.1
            // android.content.DialogInterface.OnClickListener
            override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                sslErrorHandler.proceed()
            }
        })
        builder.setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
            // from class: com.sherdle.webtoapp.widget.webview.WebToAppWebClient.2
            // android.content.DialogInterface.OnClickListener
            override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                sslErrorHandler.cancel()
            }
        })
        builder.create().show()
    }

    
    override fun onReceivedError(webView: WebView, i: Int, str: String?, str2: String) {
        if (hasConnectivity("", false) && str2 != (webView as AdvancedWebView).lastDownloadUrl) {
            val webFragment = this.fragment
            webFragment.showErrorScreen(webFragment.requireActivity().getString(R.string.error))
        } else {
            if (str2.startsWith("file:///android_asset")) {
                return
            }
            hasConnectivity("", true)
        }
    }

    fun hasConnectivity(str: String, z: Boolean): Boolean {
        var z2 = true
        if (str.startsWith("file:///android_asset")) {
            return true
        }
        val activeNetworkInfo = (this.fragment.requireActivity()
            .getSystemService("connectivity") as ConnectivityManager).getActiveNetworkInfo()
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected() || !activeNetworkInfo.isAvailable()) {
            z2 = false
            if (z) {
                if (Config.NO_CONNECTION_PAGE.length > 0 && Config.NO_CONNECTION_PAGE.startsWith("file:///android_asset")) {
                    this.browser.loadUrl(Config.NO_CONNECTION_PAGE)
                } else {
                    val webFragment = this.fragment
                    webFragment.showErrorScreen(
                        webFragment.requireActivity().getString(R.string.no_connection)
                    )
                }
            }
        }
        return z2
    }

    companion object {
        @JvmStatic
        fun urlShouldOpenExternally(str: String): Boolean {
            if (Config.OPEN_ALL_OUTSIDE_EXCEPT.size > 0) {
                for (str2 in Config.OPEN_ALL_OUTSIDE_EXCEPT) {
                    if (!str.contains(str2!!)) {
                        return true
                    }
                }
            }
            for (str3 in Config.OPEN_OUTSIDE_WEBVIEW) {
                if (str.contains(str3!!)) {
                    return true
                }
            }
            return false
        }
    }
}
