package com.sherdle.webtoapp.drawer.menu;

import android.view.Menu;
import android.view.MenuItem;


public class SimpleMenu extends SimpleAbstractMenu {
    public SimpleMenu(Menu menu, MenuItemCallback menuItemCallback) {
        this.menu = menu;
        this.callback = menuItemCallback;
    }

    public MenuItem add(String str, int i, Action action) {
        return add(this.menu, str, i, action);
    }
}
