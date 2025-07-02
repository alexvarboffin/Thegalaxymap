package com.sherdle.webtoapp.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sherdle.webtoapp.Config
import com.sherdle.webtoapp.fragment.WebFragment.Companion.newInstance

class NavigationAdapter(fm: FragmentManager?, private val mContext: Activity) :
    CacheFragmentStatePagerAdapter(fm) {
    var currentFragment: Fragment? = null
        private set

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (this.currentFragment !== `object`) {
            this.currentFragment = (`object` as Fragment)
        }
        super.setPrimaryItem(container, position, `object`)
    }

    override fun createItem(position: Int): Fragment {
        // Initialize fragments.
        // Please be sure to pass scroll position to each fragments using setArguments.
        val f: Fragment
        //final int pattern = position % 3;
        f = newInstance(Config.URLS[position])
        return f
    }

    override fun getCount(): Int {
        if (Config.USE_DRAWER) return 1
        else return Config.TITLES.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //If there is a localized title available, use it
        val title = Config.TITLES[position]
        if (title is Int && title != 0) {
            return mContext.getResources().getString(title)
        } else {
            return title as String
        }
    }
}