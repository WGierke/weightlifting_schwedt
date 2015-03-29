package de.schwedt.weightlifting.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_NEWS = 1;
    public static final int FRAGMENT_BULI = 2;
    public static final int FRAGMENT_GALLERY = 3;
    public static final int FRAGMENT_FAQ = 4;
    public static final int FRAGMENT_YOUTH = 5;
    public static final int FRAGMENT_CONTACT = 6;
    private WeightliftingApp app;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // store current fragment
    private int mCurFragment;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Can show an indeterminate progress circle in the action bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setProgressBarIndeterminateVisibility(false);

        app = (WeightliftingApp) getApplicationContext();
        if (!app.isInitialized) {
            app.initialize(this);
        }

        mTitle = mDrawerTitle = getTitle();

        initNavigation(savedInstanceState);
    }

    private void initNavigation(Bundle savedInstanceState) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slider);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem(getString(R.string.nav_home), R.drawable.nav_home, true, "1"));
        navDrawerItems.add(new NavDrawerItem(getString(R.string.nav_news), R.drawable.nav_news, true, "5"));
        navDrawerItems.add(new NavDrawerItem(getString(R.string.nav_buli), R.drawable.nav_buli));
        navDrawerItems.add(new NavDrawerItem(getString(R.string.nav_gallery), R.drawable.nav_galerie));
        navDrawerItems.add(new NavDrawerItem(getString(R.string.nav_faq), R.drawable.nav_help));
        navDrawerItems.add(new NavDrawerItem(getString(R.string.nav_youth), R.drawable.nav_help));
        navDrawerItems.add(new NavDrawerItem(getString(R.string.nav_contact), R.drawable.nav_contact));

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            showFragment(FRAGMENT_HOME);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_refresh).setVisible(!app.isOnline);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                app.checkConnection(false);
                if(app.isOnline) {
                    Toast.makeText(getApplicationContext(), R.string.connected, Toast.LENGTH_LONG).show();
                    showFragment(0);
                    item.setVisible(false);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.still_no_connection, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void showFragment(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (position) {
            case FRAGMENT_HOME:
                fragment = new HomeFragment();
                setTitle(getString(R.string.nav_home));
                break;
            case FRAGMENT_NEWS:
                fragment = new NewsFragment();
                setTitle(getString(R.string.nav_news));
                break;
            case FRAGMENT_BULI:
                fragment = new BuliFragment();
                setTitle(getString(R.string.nav_buli));
                break;
            case FRAGMENT_GALLERY:
                fragment = new GalleriesFragment();
                setTitle(getString(R.string.nav_gallery));
                break;
            case FRAGMENT_FAQ:
                fragment = new FaqFragment();
                setTitle(getString(R.string.nav_faq));
                break;
            case FRAGMENT_YOUTH:
                fragment = new YouthFragment();
                setTitle(getString(R.string.nav_youth));
                break;
            case FRAGMENT_CONTACT:
                fragment = new ContactFragment();
                setTitle(getString(R.string.nav_contact));
                break;
            default:
                break;
        }

        mCurFragment = position;
        replaceFragment(fragment, mTitle.toString());

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void replaceFragment(Fragment fragment, String title) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_container, fragment, title);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
            setTitle(title);

            fragmentManager.popBackStack();
        } else {
            Log.e(WeightliftingApp.TAG, "Fragment is null");
        }
    }

    public void addFragment(Fragment fragment, String title, Boolean backStack) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.add(R.id.frame_container, fragment, title);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            if (backStack)
                transaction.addToBackStack(title);

            transaction.commit();

            setTitle(title);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e(WeightliftingApp.TAG, "Fragment is null");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
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
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void onBackPressed() {
        if (mCurFragment == FRAGMENT_HOME) {
            finish();
        } else {
            mCurFragment = FRAGMENT_HOME;
            setTitle("Home");

            replaceFragment(new HomeFragment(), mTitle.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        app.isInForeground = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        app.isInForeground = true;
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showFragment(position);
        }
    }


}
