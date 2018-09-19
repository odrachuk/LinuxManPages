package com.softsandr.man.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.softsandr.man.R;
import com.softsandr.man.app.ManApplication;
import com.softsandr.man.ui.drawer.DrawerItem;
import com.softsandr.man.ui.drawer.DrawerListAdapter;
import com.softsandr.man.ui.drawer.DrawerMenuHeader;
import com.softsandr.man.ui.drawer.DrawerMenuItem;
import com.softsandr.man.ui.page.ManDataFragment;
import com.softsandr.man.ui.page.favorite.ManFavoriteFragment;
import com.softsandr.man.ui.page.history.ManHistoryFragment;
import com.softsandr.man.util.ThemeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ManMainActivity extends FragmentActivity {
    private static final String LOG_TAG = ManMainActivity.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationTitles;
    private int selectedSection = 1;

    private final BroadcastReceiver activityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ManPreferenceActivity.THEME_CHANGED_EVENT)) {
                ThemeUtils.changeTheme(ManMainActivity.this);
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOGGER.entering(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        getManApplication().onApplicationStart();
        setContentView(R.layout.man_activity_layout);
        initDrawer(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ManPreferenceActivity.THEME_CHANGED_EVENT);
        registerReceiver(activityBroadcastReceiver, filter);
    }

    private void initDrawer(Bundle savedInstanceState) {
        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mNavigationTitles = getResources().getStringArray(R.array.man_nav_items);
        List<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerMenuHeader(getString(R.string.man_upp_case)));
        for (int i = 0; i < 8; i++) {
            items.add(new DrawerMenuItem(mNavigationTitles[i]));
        }
        items.add(new DrawerMenuHeader(getString(R.string.other_upp_case)));
        for (int i = 8; i < mNavigationTitles.length; i++) {
            items.add(new DrawerMenuItem(mNavigationTitles[i]));
        }
        mDrawerList.setAdapter(new DrawerListAdapter(this, items));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // For the main activity, make sure the app icon in the action bar shown as a button
            getActionBar().setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            showPage(0, new ManDataFragment());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        MenuItem searchMenu = menu.findItem(R.id.action_command_search);
        if (searchMenu != null) {
            searchMenu.setVisible(!drawerOpen);
        }
        MenuItem shareMenu = menu.findItem(R.id.action_share);
        if (shareMenu != null) {
            shareMenu.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_command_search:
                // create intent to perform web search for this planet
                ManSearchDialog.showFilterDialog(ManMainActivity.this);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, ManPreferenceActivity.class));
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            switch (position) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    showPage(position - 1, new ManDataFragment());
                    selectedSection = position;
                    break;
                case 10:
                    showPage(position, ManHistoryFragment.newInstance());
                    break;
                case 11:
                    showPage(position, ManFavoriteFragment.newInstance());
                    break;
            }
        }
    }

    private void showPage(final int position, final android.support.v4.app.Fragment fragment) {
        // update selected item and title, then close the drawer
        if (position < 9) {
            mDrawerList.setItemChecked(position + 1, true);
            setTitle(mNavigationTitles[position]);
        } else {
            mDrawerList.setItemChecked(position, true);
            setTitle(mNavigationTitles[position - 2]);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();
            }
        }, 400);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }


    @Override
    public void finish() {
        LOGGER.info("finish");
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(activityBroadcastReceiver);
        super.onDestroy();
        getManApplication().onApplicationStop();
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public ManApplication getManApplication() {
        return (ManApplication) getApplication();
    }

    public int getSelectedSection() {
        return selectedSection;
    }
}
