package com.sherdle.webtoapp.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sherdle.webtoapp.App;
import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.adapter.NavigationAdapter;
import com.sherdle.webtoapp.drawer.menu.Action;
import com.sherdle.webtoapp.drawer.menu.MenuItemCallback;
import com.sherdle.webtoapp.drawer.menu.SimpleMenu;
import com.sherdle.webtoapp.fragment.WebFragment;
import com.sherdle.webtoapp.util.ThemeUtils;
import com.sherdle.webtoapp.widget.AdvancedWebView;
import com.sherdle.webtoapp.widget.SwipeableViewPager;
import com.sherdle.webtoapp.widget.webview.WebToAppWebClient;
import com.tjeannin.apprate.AppRate;
import java.util.Iterator;
import java.util.Objects;

import ru.thegalaxymap.app.R;


public class MainActivity extends AppCompatActivity implements MenuItemCallback {
    private static final int HIDING = 1;
    private static final int NO = 0;
    private static final int SHOWING = 2;
    private WebFragment CurrentAnimatingFragment = null;
    private int CurrentAnimation = 0;
    private int interstitialCount = -1;
    private NavigationAdapter mAdapter;
    public View mHeaderView;
    //private InterstitialAd mInterstitialAd;
    public TabLayout mSlidingTabLayout;
    public Toolbar mToolbar;
    public SwipeableViewPager mViewPager;
    private SimpleMenu menu;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //ThemeUtils.setTheme(this);
        setContentView(R.layout.activity_main);
        this.mToolbar = findViewById(R.id.toolbar);
        this.mHeaderView = findViewById(R.id.header_container);
        this.mSlidingTabLayout = findViewById(R.id.tabs);
        this.mViewPager = findViewById(R.id.pager);
        setSupportActionBar(this.mToolbar);
        this.mAdapter = new NavigationAdapter(getSupportFragmentManager(), this);
        Intent intent = getIntent();
        if ("android.intent.action.VIEW".equals(intent.getAction())) {
            ((App) getApplication()).setPushUrl(intent.getDataString());
        }
        if (Config.HIDE_ACTIONBAR) {
            getSupportActionBar().hide();
        }
        if (getHideTabs()) {
            this.mSlidingTabLayout.setVisibility(View.GONE);
        }
        hasPermissionToDo(this, Config.PERMISSIONS_REQUIRED);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mViewPager.getLayoutParams();
        if ((Config.HIDE_ACTIONBAR && getHideTabs()) || ((Config.HIDE_ACTIONBAR || getHideTabs()) && getCollapsingActionBar())) {
            layoutParams.topMargin = 0;
        } else if (Config.HIDE_ACTIONBAR || getHideTabs() || (!Config.HIDE_ACTIONBAR && !getHideTabs() && getCollapsingActionBar())) {
            layoutParams.topMargin = getActionBarHeight();
        } else if (!Config.HIDE_ACTIONBAR && !getHideTabs()) {
            layoutParams.topMargin = getActionBarHeight() * 2;
        }
        this.mViewPager.setLayoutParams(layoutParams);
        this.mViewPager.setAdapter(this.mAdapter);

        this.mViewPager.setOffscreenPageLimit(this.mViewPager.getAdapter().getCount() - 1);
        this.mSlidingTabLayout.setupWithViewPager(this.mViewPager);

        this.mSlidingTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { // from class: com.sherdle.webtoapp.activity.MainActivity.1
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (MainActivity.getCollapsingActionBar()) {
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.showToolbar(mainActivity.getFragment());
                }
                MainActivity.this.mViewPager.setCurrentItem(tab.getPosition());
                MainActivity.this.showInterstitial();
            }
        });
        for (int i = 0; i < this.mSlidingTabLayout.getTabCount(); i++) {
            if (Config.ICONS.length > i && Config.ICONS[i] != 0) {
                this.mSlidingTabLayout.getTabAt(i).setIcon(Config.ICONS[i]);
            }
        }
        if (Config.USE_DRAWER) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, this.mToolbar, 0, 0);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            this.navigationView = findViewById(R.id.nav_view);
            SimpleMenu simpleMenu = new SimpleMenu(this.navigationView.getMenu(), this);
            this.menu = simpleMenu;
            configureMenu(simpleMenu);
            if (Config.HIDE_DRAWER_HEADER) {
                this.navigationView.getHeaderView(0).setVisibility(View.GONE);
                this.navigationView.setFitsSystemWindows(false);
            } else if (Config.DRAWER_ICON != R.mipmap.ic_launcher) {
                ((ImageView) this.navigationView.getHeaderView(0).findViewById(R.id.drawer_icon)).setImageResource(Config.DRAWER_ICON);
            } else {
                this.navigationView.getHeaderView(0).findViewById(R.id.launcher_icon).setVisibility(View.VISIBLE);
                this.navigationView.getHeaderView(0).findViewById(R.id.drawer_icon).setVisibility(View.INVISIBLE);
            }
        } else {
            ((DrawerLayout) findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        // from class: com.sherdle.webtoapp.activity.MainActivity.2
// com.google.android.gms.ads.initialization.OnInitializationCompleteListener
//        MobileAds.initialize(this, initializationStatus -> {
//        });
//        if (!getResources().getString(R.string.ad_banner_id).equals("")) {
//            ((AdView) findViewById(R.id.adView)).loadAd(new AdRequest.Builder()
//                    .addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build());
//        } else {
//            findViewById(R.id.adView).setVisibility(View.GONE);
//        }
        if (getResources().getString(R.string.ad_interstitial_id).length() > 0) {
//            InterstitialAd interstitialAd = new InterstitialAd(this);
//            this.mInterstitialAd = interstitialAd;
//            interstitialAd.setAdUnitId(getResources().getString(R.string.ad_interstitial_id));
//            this.mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build());
//            this.mInterstitialAd.setAdListener(new AdListener() { // from class: com.sherdle.webtoapp.activity.MainActivity.3
//                @Override // com.google.android.gms.ads.AdListener
//                public void onAdClosed() {
//                    MainActivity.this.mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build());
//                }
//            });
        }
        new AppRate(this)
                .setShowIfAppHasCrashed(false)
                .setMinDaysUntilPrompt(2L)
                .setMinLaunchesUntilPrompt(2L)
                .setCustomDialog(new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.rate_title))
                        .setMessage(String.format(getString(R.string.rate_message), getString(R.string.app_name))).setPositiveButton(getString(R.string.rate_yes), null).setNegativeButton(getString(R.string.rate_never), null).setNeutralButton(getString(R.string.rate_later), null)).init();
        if (Config.SPLASH) {
            Log.d("myTag", "This is my message");
            findViewById(R.id.imageLoading1).setVisibility(View.VISIBLE);
        }
        if (Config.TOOLBAR_ICON != 0) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("");
            ImageView imageView = findViewById(R.id.toolbar_icon);
            imageView.setImageResource(Config.TOOLBAR_ICON);
            imageView.setVisibility(View.VISIBLE);
            if (Config.USE_DRAWER) {
                return;
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        WebChromeClient.CustomViewCallback customViewCallback;
        View view = null;
        if (getFragment().chromeClient != null) {
            view = getFragment().chromeClient.getCustomView();
            customViewCallback = getFragment().chromeClient.getCustomViewCallback();
        } else {
            customViewCallback = null;
        }
        if (view == null && getFragment().browser.canGoBack()) {
            getFragment().browser.goBack();
        } else if (view != null && customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
        } else {
            super.onBackPressed();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if (Config.HIDE_MENU_SHARE) {
            menu.findItem(R.id.share).setVisible(false);
        }
        if (Config.HIDE_MENU_HOME) {
            menu.findItem(R.id.home).setVisible(false);
        }
        if (Config.HIDE_MENU_NAVIGATION) {
            menu.findItem(R.id.previous).setVisible(false);
            menu.findItem(R.id.next).setVisible(false);
        }
        if (!Config.SHOW_NOTIFICATION_SETTINGS || Build.VERSION.SDK_INT < 21) {
            menu.findItem(R.id.notification_settings).setVisible(false);
        }
        ThemeUtils.tintAllIcons(menu, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        AdvancedWebView advancedWebView = getFragment().browser;
        if (menuItem.getItemId() == R.id.next) {
            advancedWebView.goForward();
            return true;
        }
        if (menuItem.getItemId() == R.id.previous) {
            advancedWebView.goBack();
            return true;
        }
        if (menuItem.getItemId() == R.id.share) {
            getFragment().shareURL();
            return true;
        }
        if (menuItem.getItemId() == R.id.home) {
            advancedWebView.loadUrl(getFragment().mainUrl);
            return true;
        }
        if (menuItem.getItemId() == R.id.close) {
            finish();
            Toast.makeText(getApplicationContext(), getText(R.string.exit_message), 0).show();
            return true;
        }
        if (menuItem.getItemId() == R.id.notification_settings) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    public void onPause() {
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    public void onResume() {
        super.onResume();
    }

    private void AboutDialog() {
        TextView textView = new TextView(this);
        Linkify.addLinks(new SpannableString(getText(R.string.dialog_about)), 1);
        textView.setTextSize(15.0f);
        int round = Math.round(getResources().getDisplayMetrics().density * 20.0f);
        textView.setPadding(round, 15, round, 15);
        textView.setText(Html.fromHtml(getString(R.string.dialog_about)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        new AlertDialog.Builder(this).setTitle(Html.fromHtml(getString(R.string.about))).setCancelable(true).setPositiveButton("ok", null).setView(textView).create().show();
    }

    public void setTitle(String str) {
        NavigationAdapter navigationAdapter = this.mAdapter;
        if (navigationAdapter == null || navigationAdapter.getCount() != 1 || Config.USE_DRAWER || Config.STATIC_TOOLBAR_TITLE) {
            return;
        }
        getSupportActionBar().setTitle(str);
    }

    public WebFragment getFragment() {
        return (WebFragment) this.mAdapter.getCurrentFragment();
    }


    public void hideSplash() {
        if (Config.SPLASH && findViewById(R.id.imageLoading1).getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(() -> MainActivity.this.findViewById(R.id.imageLoading1).setVisibility(View.GONE), Config.SPLASH_SCREEN_DELAY);
        }
    }

    public void hideToolbar() {
        int i = this.CurrentAnimation;
        int i2 = HIDING;
        if (i != i2) {
            this.CurrentAnimation = i2;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(getFragment().rl, "y", 0.0f), ObjectAnimator.ofFloat(this.mHeaderView, "y", -getActionBarHeight()));
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() { // from class: com.sherdle.webtoapp.activity.MainActivity.5
                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    MainActivity.this.CurrentAnimation = MainActivity.NO;
                }
            });
        }
    }

    public void showToolbar(WebFragment webFragment) {
        int i = this.CurrentAnimation;
        int i2 = SHOWING;
        if (i == i2 && webFragment == this.CurrentAnimatingFragment) {
            return;
        }
        this.CurrentAnimation = i2;
        this.CurrentAnimatingFragment = webFragment;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(webFragment.rl, "y", getActionBarHeight()), ObjectAnimator.ofFloat(this.mHeaderView, "y", 0.0f));
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() { // from class: com.sherdle.webtoapp.activity.MainActivity.6
            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                MainActivity.this.CurrentAnimation = MainActivity.NO;
                MainActivity.this.CurrentAnimatingFragment = null;
            }
        });
    }

    public int getActionBarHeight() {
        int height = this.mToolbar.getHeight();
        if (height != 0) {
            return height;
        }
        TypedValue typedValue = new TypedValue();
        return getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true) ? TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics()) : height;
    }

    boolean getHideTabs() {
        if (this.mAdapter.getCount() == 1 || Config.USE_DRAWER) {
            return true;
        }
        return Config.HIDE_TABS;
    }

    public static boolean getCollapsingActionBar() {
        return Config.COLLAPSING_ACTIONBAR && !Config.HIDE_ACTIONBAR;
    }

    private static boolean hasPermissionToDo(final Activity activity, final String[] strArr) {
        boolean z = false;
        for (String str : strArr) {
            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(activity, str) != 0) {
                z = true;
            }
        }
        if (!z) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.common_permission_explaination);
        builder.setPositiveButton(R.string.common_permission_grant, new DialogInterface.OnClickListener() { // from class: com.sherdle.webtoapp.activity.MainActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= 23) {
                    activity.requestPermissions(strArr, 1);
                }
            }
        });
        builder.create().show();
        return false;
    }

    public void showInterstitial() {
//        int i = this.interstitialCount;
//        if (i == 1) {
//            InterstitialAd interstitialAd = this.mInterstitialAd;
//            if (interstitialAd != null && interstitialAd.isLoaded()) {
//                this.mInterstitialAd.show();
//            }
//            this.interstitialCount = 0;
//            return;
//        }
//        this.interstitialCount = i + 1;
    }

    public void configureMenu(SimpleMenu simpleMenu) {
        String str;
        int i = 0;
        while (i < Config.TITLES.length) {
            Object obj = Config.TITLES[i];
            if ((obj instanceof Integer) && !obj.equals(0)) {
                str = getResources().getString(((Integer) obj).intValue());
            } else {
                str = (String) obj;
            }
            simpleMenu.add((String) Config.TITLES[i], Config.ICONS.length > i ? Config.ICONS[i] : 0, new Action(str, Config.URLS[i]));
            i++;
        }
        menuItemClicked(simpleMenu.getFirstMenuItem().getValue(), simpleMenu.getFirstMenuItem().getKey());
    }

    @Override // com.sherdle.webtoapp.drawer.menu.MenuItemCallback
    public void menuItemClicked(Action action, MenuItem menuItem) {
        if (WebToAppWebClient.urlShouldOpenExternally(action.url)) {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(action.url)));
                return;
            } catch (ActivityNotFoundException unused) {
                if (action.url.startsWith("intent://")) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse(action.url.replace("intent://", "http://"))));
                    return;
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_app_message), 1).show();
                    return;
                }
            }
        }
        Iterator<MenuItem> it = this.menu.getMenuItems().iterator();
        while (it.hasNext()) {
            it.next().setChecked(false);
        }
        menuItem.setChecked(true);
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        if (getFragment() == null) {
            return;
        }
        getFragment().browser.loadUrl("about:blank");
        getFragment().setBaseUrl(action.url);
        showInterstitial();
        Log.v("INFO", "Drawer Item Selected");
    }
}
