package com.sherdle.webtoapp

import android.text.TextUtils
import androidx.multidex.MultiDexApplication

//import com.onesignal.OneSignal;
class App : MultiDexApplication() {
    //private FirebaseAnalytics mFirebaseAnalytics;
    private var push_url: String? = null

    override fun onCreate() {
        super.onCreate()
//        if (Config.ANALYTICS_ID.length() > 0) {
//            this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        }
        if (TextUtils.isEmpty(ONESIGNAL_APP_ID)) {
            return
        }


        //OneSignal.init(this, "REMOTE", getString(R.string.onesignal_app_id), new NotificationHandler());
        //OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
    }


    @get:Synchronized
    @set:Synchronized
    var pushUrl: String?
        //    class NotificationHandler implements OneSignal.NotificationOpenedHandler {
        get() {
            val str: String?
            str = this.push_url
            this.push_url = null
            return str
        }
        set(str) {
            this.push_url = str
        }

    companion object {
        private const val ONESIGNAL_APP_ID = ""
    }
}
