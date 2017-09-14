package com.jeevan.inshorts.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.jeevan.inshorts.fragments.BookmarksFragment;
import com.jeevan.inshorts.fragments.HomePageFragment;

/**
 * Created by jeevan on 9/14/17.
 */

public class HomePageAdapter extends FragmentStatePagerAdapter {

    public HomePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new HomePageFragment();
            case 1: return new BookmarksFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "FEED";
            case 1: return "BOOKMARKS";
        }
        return "";
    }

    @Override
    public int getCount() {
        return 2;
    }
}
