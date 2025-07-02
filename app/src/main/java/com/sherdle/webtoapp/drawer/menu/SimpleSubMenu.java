package com.sherdle.webtoapp.drawer.menu;

import android.view.MenuItem;
import android.view.SubMenu;


public class SimpleSubMenu {
    SimpleMenu parent;
    SubMenu subMenu;
    String subMenuTitle;

    public SimpleSubMenu(SimpleMenu simpleMenu, String str) {
        this.parent = simpleMenu;
        this.subMenuTitle = str;
        this.subMenu = simpleMenu.getMenu().addSubMenu(str);
    }

    public MenuItem add(String str, int i, Action action) {
        return this.parent.add(this.subMenu, str, i, action);
    }

    public String getSubMenuTitle() {
        return this.subMenuTitle;
    }
}
