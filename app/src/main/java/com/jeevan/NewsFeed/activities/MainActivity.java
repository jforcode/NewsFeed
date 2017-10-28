package com.jeevan.NewsFeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.jeevan.NewsFeed.R;
import com.jeevan.NewsFeed.dao.DbTransactions;
import com.jeevan.NewsFeed.dao.NewsFeed;
import com.jeevan.NewsFeed.fragments.BookmarksFragment;
import com.jeevan.NewsFeed.fragments.CreditsFragment;
import com.jeevan.NewsFeed.fragments.HomePageFragment;
import com.jeevan.NewsFeed.interfaces.BookmarkEventListener;
import com.jeevan.NewsFeed.interfaces.MainActivityChangeListener;
import com.jeevan.NewsFeed.util.Constants;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BookmarkEventListener, MainActivityChangeListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_page_content)
    FrameLayout mainPageContent;
    @BindView(R.id.drawer_layout)
    DrawerLayout navDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navDrawerMenu;

    ActionBarDrawerToggle drawerToggle;
    Handler handler;

    private int currMenuItemId;

    private Map<Integer, FragmentMetaData> mapFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        handler = new Handler();

        drawerToggle = new ActionBarDrawerToggle(this, navDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        navDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navDrawerMenu.setNavigationItemSelectedListener(this);

        mapFragments = new HashMap<>();
        HomePageFragment homePageFragment = new HomePageFragment();
        homePageFragment.setHasOptionsMenu(true);
        mapFragments.put(R.id.nav_menu_home, new FragmentMetaData(homePageFragment, "HOME"));
        mapFragments.put(R.id.nav_menu_bookmarks, new FragmentMetaData(new BookmarksFragment(), "BOOKMARKS"));
        mapFragments.put(R.id.nav_menu_credits, new FragmentMetaData(new CreditsFragment(), "CREDITS"));

        currMenuItemId = R.id.nav_menu_home;
        showCurrentPage();
        navDrawerMenu.setCheckedItem(currMenuItemId);
    }

    private void showCurrentPage() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.main_page_content, mapFragments.get(currMenuItemId).getFragInstance(), mapFragments.get(currMenuItemId).getTag())
                        .commit();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == currMenuItemId) {
            return false;
        }
        currMenuItemId = item.getItemId();
        showCurrentPage();
        navDrawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void bookmark(NewsFeed feed) {
        DbTransactions.getDbInstance(this).bookmark(feed);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    private class FragmentMetaData {
        Fragment fragInstance;
        String tag;

        public FragmentMetaData(Fragment fragInstance, String tag) {
            this.fragInstance = fragInstance;
            this.tag = tag;
        }

        public Fragment getFragInstance() {
            return fragInstance;
        }

        public String getTag() {
            return tag;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RQ_FILTER && resultCode == Constants.RT_FILTER) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("HOME");
            if (fragment != null && fragment instanceof HomePageFragment) {
                ((HomePageFragment) fragment).applySortAndFilter(data);
            }
        }
    }
}
