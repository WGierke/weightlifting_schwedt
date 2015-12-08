package de.schwedt.weightlifting.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.Table;
import de.schwedt.weightlifting.app.buli.Team;
import de.schwedt.weightlifting.app.gallery.Galleries;
import de.schwedt.weightlifting.app.helper.UiHelper;
import de.schwedt.weightlifting.app.news.Events;
import de.schwedt.weightlifting.app.news.News;
import de.schwedt.weightlifting.app.service.RegistrationIntentService;

public class MainActivity extends AppCompatActivity {

    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_NEWS = 1;
    public static final int FRAGMENT_BULI = 2;
    public static final int FRAGMENT_GALLERY = 3;
    public static final int FRAGMENT_FAQ = 4;
    public static final int FRAGMENT_CONTACT = 5;
    //home, (news, events), (team, competitions, table), (gallery)
    public static int counter[][] = {{}, {0, 0}, {0, 0, 0}, {0}};
    private WeightliftingApp app;
    private Toolbar mToolbar;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Can show an indeterminate progress circle in the action bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        app = (WeightliftingApp) getApplicationContext();
        app.setActivity(this);

        initNavigation(savedInstanceState);
        markElementsInNavAndRefresh();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int fragmentId = extras.getInt("fragmentId");
            if (fragmentId != 0) {
                Log.d(WeightliftingApp.TAG, "Fragment to open: " + fragmentId);
                showFragment(fragmentId);
            }
        }
    }

    public void markNewElementsInNav() {
        UiHelper.refreshCounterNav(News.navigationPosition, News.subPosition, News.itemsToMark.size());
        UiHelper.refreshCounterNav(Events.navigationPosition, Events.subPosition, Events.itemsToMark.size());
        UiHelper.refreshCounterNav(Team.navigationPosition, Team.subPosition, Team.itemsToMark.size());
        UiHelper.refreshCounterNav(Competitions.navigationPosition, Competitions.subPosition, Competitions.itemsToMark.size());
        UiHelper.refreshCounterNav(Table.navigationPosition, Table.subPosition, Table.itemsToMark.size());
        UiHelper.refreshCounterNav(Galleries.navigationPosition, Galleries.subPosition, Galleries.itemsToMark.size());
    }

    public void markElementsInNavAndRefresh() {
        markNewElementsInNav();
//        ((NavDrawerListAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
    }

    private void initNavigation(Bundle savedInstanceState) {

        PrimaryDrawerItem nav_home = new PrimaryDrawerItem().withName(R.string.nav_home);
        PrimaryDrawerItem nav_news = new PrimaryDrawerItem().withName(R.string.nav_news);
        PrimaryDrawerItem nav_buli = new PrimaryDrawerItem().withName(R.string.nav_buli);
        PrimaryDrawerItem nav_gallery = new PrimaryDrawerItem().withName(R.string.nav_gallery);
        PrimaryDrawerItem nav_faq = new PrimaryDrawerItem().withName(R.string.nav_faq);
        PrimaryDrawerItem nav_contact = new PrimaryDrawerItem().withName(R.string.nav_contact);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        nav_home,
                        new DividerDrawerItem(),
                        nav_news,
                        new DividerDrawerItem(),
                        nav_buli,
                        new DividerDrawerItem(),
                        nav_gallery,
                        new DividerDrawerItem(),
                        nav_faq,
                        new DividerDrawerItem(),
                        nav_contact,
                        new DividerDrawerItem()
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        showFragment(position/2);
                        return false;
                    }
                })
                .build();

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            showFragment(FRAGMENT_HOME);
        }

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_refresh).setVisible(true);
        return true;
    }

    private void showAsyncUpdateResults() {
        switch (app.getUpdateStatus()) {
            case WeightliftingApp.UPDATE_STATUS_PENDING:
                Runnable refreshRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showAsyncUpdateResults();
                    }
                };
                Handler refreshHandler = new Handler();
                refreshHandler.postDelayed(refreshRunnable, 200);
                return;
            case WeightliftingApp.UPDATE_STATUS_SUCCESSFUL:
                showCountedNewElements(true);
                markElementsInNavAndRefresh();
                break;
            case WeightliftingApp.UPDATE_STATUS_FAILED:
                showCountedNewElements(false);
                break;
        }
    }

    private void showCountedNewElements(boolean updatedSuccessfully) {
        int newElements = News.itemsToMark.size() + Events.itemsToMark.size() + Team.itemsToMark.size() + Competitions.itemsToMark.size() + Table.itemsToMark.size() + Galleries.itemsToMark.size();
        if (updatedSuccessfully)
            UiHelper.showToast(getResources().getString(R.string.updated_all_successfully), getApplicationContext());
        else
            UiHelper.showToast(getResources().getString(R.string.updated_all_unsuccessfully), getApplicationContext());
        UiHelper.showToast(getResources().getQuantityString(R.plurals.new_elements, newElements, newElements), getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (WeightliftingApp.isUpdatingAll) {
                    UiHelper.showToast(getResources().getString(R.string.updating_in_progress), getApplicationContext());
                } else {
                    WeightliftingApp.isUpdatingAll = true;
                    app.setFinishUpdateFlags(false);
                    try {
                        app.updateDataForcefully();
                        showAsyncUpdateResults();
                    } catch (Exception e) {
                        Log.d(WeightliftingApp.TAG, "Error while updating all");
                        e.printStackTrace();
                        UiHelper.showToast(getResources().getString(R.string.updated_all_unsuccessfully), getApplicationContext());
                    }
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
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
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
            case FRAGMENT_CONTACT:
                fragment = new ContactFragment();
                setTitle(getString(R.string.nav_contact));
                break;
            default:
                break;
        }

        replaceFragment(fragment, mTitle.toString());

        // update selected item and title, then close the drawer
        /*mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);*/
        invalidateOptionsMenu();
    }

    public void replaceFragment(Fragment fragment, String title) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_container, fragment, title);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
        } else {
            Log.e(WeightliftingApp.TAG, "Fragment is null");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
//        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
     //   mDrawerToggle.onConfigurationChanged(newConfig);
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
