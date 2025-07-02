package com.tjeannin.apprate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import java.lang.Thread;


public class AppRate implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
    private static final String TAG = "AppRater";
    private DialogInterface.OnClickListener clickListener;
    private Activity hostActivity;
    private SharedPreferences preferences;
    private AlertDialog.Builder dialogBuilder = null;
    private long minLaunchesUntilPrompt = 0;
    private long minDaysUntilPrompt = 0;
    private boolean showIfHasCrashed = true;

    public AppRate(Activity activity) {
        this.hostActivity = activity;
        this.preferences = activity.getSharedPreferences(PrefsContract.SHARED_PREFS_NAME, 0);
    }

    public AppRate setMinLaunchesUntilPrompt(long j) {
        this.minLaunchesUntilPrompt = j;
        return this;
    }

    public AppRate setMinDaysUntilPrompt(long j) {
        this.minDaysUntilPrompt = j;
        return this;
    }

    public AppRate setShowIfAppHasCrashed(boolean z) {
        this.showIfHasCrashed = z;
        return this;
    }

    public AppRate setCustomDialog(AlertDialog.Builder builder) {
        this.dialogBuilder = builder;
        return this;
    }

    public static void reset(Context context) {
        context.getSharedPreferences(PrefsContract.SHARED_PREFS_NAME, 0).edit().clear().commit();
        Log.d(TAG, "Cleared AppRate shared preferences.");
    }

    public void init() {
        Log.d(TAG, "Init AppRate");
        if (this.preferences.getBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, false)) {
            return;
        }
        if (!this.preferences.getBoolean(PrefsContract.PREF_APP_HAS_CRASHED, false) || this.showIfHasCrashed) {
            if (!this.showIfHasCrashed) {
                initExceptionHandler();
            }
            SharedPreferences.Editor edit = this.preferences.edit();
            long j = this.preferences.getLong(PrefsContract.PREF_LAUNCH_COUNT, 0L) + 1;
            edit.putLong(PrefsContract.PREF_LAUNCH_COUNT, j);
            Long valueOf = Long.valueOf(this.preferences.getLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, 0L));
            if (valueOf.longValue() == 0) {
                valueOf = Long.valueOf(System.currentTimeMillis());
                edit.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, valueOf.longValue());
            }
            if (j >= this.minLaunchesUntilPrompt && System.currentTimeMillis() >= valueOf.longValue() + (this.minDaysUntilPrompt * 86400000)) {
                AlertDialog.Builder builder = this.dialogBuilder;
                if (builder != null) {
                    showDialog(builder);
                } else {
                    showDefaultDialog();
                }
            }
            edit.commit();
        }
    }

    private void initExceptionHandler() {
        Log.d(TAG, "Init AppRate ExceptionHandler");
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultUncaughtExceptionHandler instanceof ExceptionHandler) {
            return;
        }
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(defaultUncaughtExceptionHandler, this.hostActivity));
    }

    private void showDefaultDialog() {
        Log.d(TAG, "Create default dialog.");
        new AlertDialog.Builder(this.hostActivity).setTitle("Rate " + getApplicationName(this.hostActivity.getApplicationContext())).setMessage("If you enjoy using " + getApplicationName(this.hostActivity.getApplicationContext()) + ", please take a moment to rate it. Thanks for your support!").setPositiveButton("Rate it !", this).setNegativeButton("No thanks", this).setNeutralButton("Remind me later", this).setOnCancelListener(this).create().show();
    }

    private void showDialog(AlertDialog.Builder builder) {
        Log.d(TAG, "Create custom dialog.");
        AlertDialog create = builder.create();
        create.show();
        String str = (String) create.getButton(-1).getText();
        String str2 = (String) create.getButton(-3).getText();
        String str3 = (String) create.getButton(-2).getText();
        create.setButton(-1, str, this);
        create.setButton(-3, str2, this);
        create.setButton(-2, str3, this);
        create.setOnCancelListener(this);
    }

    @Override // android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, System.currentTimeMillis());
        edit.putLong(PrefsContract.PREF_LAUNCH_COUNT, 0L);
        edit.commit();
    }

    public AppRate setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        this.clickListener = onClickListener;
        return this;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        SharedPreferences.Editor edit = this.preferences.edit();
        if (i == -3) {
            edit.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, System.currentTimeMillis());
            edit.putLong(PrefsContract.PREF_LAUNCH_COUNT, 0L);
        } else if (i == -2) {
            edit.putBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, true);
        } else if (i == -1) {
            this.hostActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + this.hostActivity.getPackageName())));
            edit.putBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, true);
        }
        edit.apply();
        dialogInterface.dismiss();
        DialogInterface.OnClickListener onClickListener = this.clickListener;
        if (onClickListener != null) {
            onClickListener.onClick(dialogInterface, i);
        }
    }

    private static final String getApplicationName(Context context) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException unused) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
    }
}
