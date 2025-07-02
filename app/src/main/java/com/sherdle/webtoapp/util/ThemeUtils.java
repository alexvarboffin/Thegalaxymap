package com.sherdle.webtoapp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.sherdle.webtoapp.Config;
import ru.thegalaxymap.app.R;


public class ThemeUtils {
    public static void tintAllIcons(Menu menu, Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.toolbarForeground, typedValue, true);
        tintAllIcons(menu, context, typedValue.data);
    }

    private static void tintAllIcons(Menu menu, Context context, int i) {
        for (int i2 = 0; i2 < menu.size(); i2++) {
            tintMenuItemIcon(i, menu.getItem(i2));
        }
    }

    public static int getPrimaryDarkColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    public static void setTheme(Activity activity) {
        activity.setTheme(Config.LIGHT_TOOLBAR_THEME ? 2131755016 : R.style.AppTheme);
    }

    public static boolean lightToolbarThemeActive(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.toolbarBackground, typedValue, true);
        return typedValue.data == ContextCompat.getColor(context, R.color.white);
    }

    public static void setToolbarContentColor(Toolbar toolbar, int i) {
        applyTintToDrawable(toolbar.getOverflowIcon(), i);
        applyTintToDrawable(toolbar.getNavigationIcon(), i);
        toolbar.setTitleTextColor(i);
        tintAllIcons(toolbar.getMenu(), toolbar.getContext(), i);
    }

    private static void applyTintToDrawable(Drawable drawable, int i) {
        if (drawable == null) {
            return;
        }
        Drawable wrap = DrawableCompat.wrap(drawable);
        drawable.mutate();
        DrawableCompat.setTint(wrap, i);
    }

    private static void tintMenuItemIcon(int i, MenuItem menuItem) {
        Drawable icon = menuItem.getIcon();
        if (icon != null) {
            Drawable wrap = DrawableCompat.wrap(icon);
            icon.mutate();
            DrawableCompat.setTint(wrap, i);
            menuItem.setIcon(icon);
        }
    }

    private static void tintMenuItemText(MenuItem menuItem, int i) {
        if (menuItem.getTitle().toString().isEmpty()) {
            return;
        }
        SpannableString spannableString = new SpannableString(menuItem.getTitle().toString());
        spannableString.setSpan(new ForegroundColorSpan(i), 0, spannableString.length(), 0);
        menuItem.setTitle(spannableString);
    }

    private static void tintShareIconIfPresent(int i, MenuItem menuItem) {
        View findViewById;
        ImageView imageView;
        if (menuItem.getActionView() == null || (findViewById = menuItem.getActionView().findViewById(R.id.expand_activities_button)) == null || (imageView = (ImageView) findViewById.findViewById(R.id.image)) == null) {
            return;
        }
        Drawable wrap = DrawableCompat.wrap(imageView.getDrawable());
        DrawableCompat.setTint(wrap, i);
        imageView.setImageDrawable(wrap);
    }
}
