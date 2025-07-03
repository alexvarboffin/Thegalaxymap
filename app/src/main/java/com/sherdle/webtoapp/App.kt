package com.sherdle.webtoapp;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDexApplication;
//import com.onesignal.OneSignal;


public class App extends MultiDexApplication {

    private static final String ONESIGNAL_APP_ID = "";

    //private FirebaseAnalytics mFirebaseAnalytics;
    private String push_url = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (Config.ANALYTICS_ID.length() > 0) {
//            this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        }
        if (TextUtils.isEmpty(ONESIGNAL_APP_ID)) {
            return;
        }
        

        //OneSignal.init(this, "REMOTE", getString(R.string.onesignal_app_id), new NotificationHandler());
        //OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

    }


//    class NotificationHandler implements OneSignal.NotificationOpenedHandler {
//        // This fires when a notification is opened by tapping on it.
//        @Override
//        public void notificationOpened(OSNotificationOpenResult result) {
//            try {
//                JSONObject data = result.notification.payload.additionalData;
//                String url = (data != null) ? data.optString("url", null) : null;
//                if (url != null) {
//                    if (result.notification.isAppInFocus) {
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(browserIntent);
//                        Log.v("INFO", "Received notification while app was on foreground");
//                    } else {
//                        push_url = url;
//                    }
//                } else if (!result.notification.isAppInFocus) {
//                    Intent mainIntent;
//                    mainIntent = new Intent(App.this, MainActivity.class);
//                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(mainIntent);
//                }
//
//
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
//
//    }

    public synchronized String getPushUrl() {
        String str;
        str = this.push_url;
        this.push_url = null;
        return str;
    }

    public synchronized void setPushUrl(String str) {
        this.push_url = str;
    }
}
