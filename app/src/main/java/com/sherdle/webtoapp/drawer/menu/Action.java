package com.sherdle.webtoapp.drawer.menu;

import java.io.Serializable;


public class Action implements Serializable {
    public String name;
    public String url;

    public Action(String str, String str2) {
        this.name = str;
        this.url = str2;
    }
}
