package com.sherdle.webtoapp.widget.webview;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.fragment.WebFragment;
import com.sherdle.webtoapp.widget.AdvancedWebView;
import ru.thegalaxymap.app.R;


public class WebToAppWebClient extends WebViewClient {
    WebView browser;
    WebFragment fragment;

    @Override // android.webkit.WebViewClient
    public void onPageFinished(WebView webView, String str) {
    }

    public WebToAppWebClient(WebFragment webFragment, WebView webView) {
        this.fragment = webFragment;
        this.browser = webView;
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        return shouldOverrideUrlLoading(webView, webResourceRequest.getUrl().toString());
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideUrlLoading(WebView webView, String str) {
        if (urlShouldOpenExternally(str)) {
            try {
                webView.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            } catch (ActivityNotFoundException unused) {
                if (str.startsWith("intent://")) {
                    webView.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str.replace("intent://", "http://"))));
                } else {
                    Toast.makeText(this.fragment.getActivity(), this.fragment.getActivity().getResources().getString(R.string.no_app_message), 1).show();
                }
            }
            return true;
        }
        if (str.endsWith(".mp4") || str.endsWith(".avi") || str.endsWith(".flv")) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(Uri.parse(str), "video/mp4");
                webView.getContext().startActivity(intent);
            } catch (Exception unused2) {
            }
            return true;
        }
        if (str.endsWith(".mp3") || str.endsWith(".wav")) {
            try {
                Intent intent2 = new Intent("android.intent.action.VIEW");
                intent2.setDataAndType(Uri.parse(str), "audio/mp3");
                webView.getContext().startActivity(intent2);
            } catch (Exception unused3) {
            }
            return true;
        }
        return super.shouldOverrideUrlLoading(webView, str);
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        onReceivedError(webView, webResourceError.getErrorCode(), webResourceError.getDescription().toString(), webResourceRequest.getUrl().toString());
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedSslError(WebView webView, final SslErrorHandler sslErrorHandler, SslError sslError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.fragment.getActivity());
        builder.setMessage(R.string.notification_error_ssl_cert_invalid);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // from class: com.sherdle.webtoapp.widget.webview.WebToAppWebClient.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                sslErrorHandler.proceed();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.sherdle.webtoapp.widget.webview.WebToAppWebClient.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                sslErrorHandler.cancel();
            }
        });
        builder.create().show();
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedError(WebView webView, int i, String str, String str2) {
        if (hasConnectivity("", false) && !str2.equals(((AdvancedWebView) webView).lastDownloadUrl)) {
            WebFragment webFragment = this.fragment;
            webFragment.showErrorScreen(webFragment.getActivity().getString(R.string.error));
        } else {
            if (str2.startsWith("file:///android_asset")) {
                return;
            }
            hasConnectivity("", true);
        }
    }

    public boolean hasConnectivity(String str, boolean z) {
        boolean z2 = true;
        if (str.startsWith("file:///android_asset")) {
            return true;
        }
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.fragment.getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected() || !activeNetworkInfo.isAvailable()) {
            z2 = false;
            if (z) {
                if (Config.NO_CONNECTION_PAGE.length() > 0 && Config.NO_CONNECTION_PAGE.startsWith("file:///android_asset")) {
                    this.browser.loadUrl(Config.NO_CONNECTION_PAGE);
                } else {
                    WebFragment webFragment = this.fragment;
                    webFragment.showErrorScreen(webFragment.getActivity().getString(R.string.no_connection));
                }
            }
        }
        return z2;
    }

    public static boolean urlShouldOpenExternally(String str) {
        if (Config.OPEN_ALL_OUTSIDE_EXCEPT.length > 0) {
            for (String str2 : Config.OPEN_ALL_OUTSIDE_EXCEPT) {
                if (!str.contains(str2)) {
                    return true;
                }
            }
        }
        for (String str3 : Config.OPEN_OUTSIDE_WEBVIEW) {
            if (str.contains(str3)) {
                return true;
            }
        }
        return false;
    }
}
