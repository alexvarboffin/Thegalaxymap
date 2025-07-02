package com.tjeannin.apprate;

import android.content.Context;
import android.content.SharedPreferences;
import java.lang.Thread;


public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;
    SharedPreferences preferences;

    public ExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Context context) {
        this.preferences = context.getSharedPreferences(PrefsContract.SHARED_PREFS_NAME, 0);
        this.defaultExceptionHandler = uncaughtExceptionHandler;
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        this.preferences.edit().putBoolean(PrefsContract.PREF_APP_HAS_CRASHED, true).apply();
        this.defaultExceptionHandler.uncaughtException(thread, th);
    }
}
