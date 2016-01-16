package com.webview.nodebb.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.webview.nodebb.R;
import com.webview.nodebb.WebAppApplication;
import com.webview.nodebb.adapter.DrawerAdapter;
import com.webview.nodebb.fragment.MainFragment;


public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[] mTitles;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActionBar();
        setupDrawer(savedInstanceState);

        // Initialize Analytics Tracker
        ((WebAppApplication) getApplication()).getTracker();
    }


    @Override
    public void onStart() {
        super.onStart();

        // Start Analytics Instance
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();

        // Stop Analytics Instance
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Open/Close drawer if Home button is pressed
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Menu behaviour
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = MainActivity.newIntent(this);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        mDrawerToggle.onConfigurationChanged(newConfiguration);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
    }


    private void setupDrawer(Bundle savedInstanceState) {
        mTitle = getTitle();
        mDrawerTitle = getTitle();

        mTitles = getResources().getStringArray(R.array.navigation_title_list);

        // Icons
        TypedArray iconTypedArray = getResources().obtainTypedArray(R.array.navigation_icon_list);
        Integer[] icons = new Integer[iconTypedArray.length()];
        for (int i = 0; i < iconTypedArray.length(); i++) {
            icons[i] = iconTypedArray.getResourceId(i, -1);
        }
        iconTypedArray.recycle();

        // References
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_layout);
        mDrawerListView = (ListView) findViewById(R.id.activity_main_drawer);

        // Set Drawer
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // Disables Drawer!
        mDrawerListView.setAdapter(new DrawerAdapter(this, mTitles, icons));
        mDrawerListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View clickedView, int position, long id) {
                selectDrawerItem(position, false);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Show Fragment
        if (savedInstanceState == null) {
            selectDrawerItem(0, true);
        }
    }


    private void selectDrawerItem(int position, boolean init) {
        String[] urlList = getResources().getStringArray(R.array.navigation_url_list);

        Fragment fragment = MainFragment.newInstance(urlList[position]);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main_container, fragment).commitAllowingStateLoss();

        mDrawerListView.setItemChecked(position, true);
        if (!init) setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerListView);
    }
}
