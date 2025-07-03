package com.sherdle.webtoapp


object Config {

    var ANALYTICS_ID: String = ""
    @JvmField
    var COLLAPSING_ACTIONBAR: Boolean = false
    @JvmField
    var DRAWER_ICON: Int = 2131165323
    @JvmField
    var HIDE_ACTIONBAR: Boolean = true
    @JvmField
    var HIDE_DRAWER_HEADER: Boolean = false
    @JvmField
    var HIDE_MENU_HOME: Boolean = false
    @JvmField
    var HIDE_MENU_NAVIGATION: Boolean = false
    @JvmField
    var HIDE_MENU_SHARE: Boolean = false
    @JvmField
    var HIDE_TABS: Boolean = true
    const val INTERSTITIAL_INTERVAL: Int = 2
    const val INTERSTITIAL_PAGE_LOAD: Boolean = true
    @JvmField
    var LIGHT_TOOLBAR_THEME: Boolean = false
    @JvmField
    var LOAD_AS_PULL: Boolean = true
    @JvmField
    var MULTI_WINDOWS: Boolean = false
    @JvmField
    var NO_CONNECTION_PAGE: String = ""
    @JvmField
    var PULL_TO_REFRESH: Boolean = false
    @JvmField
    var SHOW_NOTIFICATION_SETTINGS: Boolean = false
    @JvmField
    var SPLASH: Boolean = true
    @JvmField
    var SPLASH_SCREEN_DELAY: Int = 800
    @JvmField
    var STATIC_TOOLBAR_TITLE: Boolean = false
    @JvmField
    var TOOLBAR_ICON: Int = 0
    @JvmField
    var USE_DRAWER: Boolean = false
    @JvmField
    val TITLES: Array<Any> = arrayOf<Any>("")
    @JvmField
    val URLS: Array<String> = arrayOf<String>("https://thegalaxymap.ru")
    @JvmField
    val ICONS: IntArray = IntArray(0)
    @JvmField
    val OPEN_OUTSIDE_WEBVIEW: Array<String?> = arrayOfNulls<String>(0)
    @JvmField
    val OPEN_ALL_OUTSIDE_EXCEPT: Array<String?> = arrayOfNulls<String>(0)
    @JvmField
    var PERMISSIONS_REQUIRED: Array<String> = emptyArray()
}
