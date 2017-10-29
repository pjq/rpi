package me.pjq.rpicar;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.pjq.rpicar.realm.Settings;

public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AboutFragment.OnFragmentInteractionListener {
    private OnBackKeyListener onBackKeyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initSettings();
        initFirstFragment();
    }

    private void initSettings() {
        DataManager.init(getApplicationContext());
        Settings settings = DataManager.getRealm().where(Settings.class).findFirst();
        if (null != settings && !TextUtils.isEmpty(settings.getHost())) {
            CarControllerApiService.Config.HOST = settings.getHost();
        }
    }

    private void initFirstFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = CameraControllerFragment.newInstance();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = CameraControllerFragment.newInstance();
            fragmentTransaction.replace(R.id.content, fragment);
//            fragmentTransaction.addToBackStack(SettingsFragment.class.getSimpleName());
            fragmentTransaction.commitAllowingStateLoss();
            onBackKeyListener = null;
        } else if (id == R.id.nav_slideshow) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = CaptureVideoFragment.newInstance();
            fragmentTransaction.replace(R.id.content, fragment);
//            fragmentTransaction.addToBackStack(CaptureVideoFragment.class.getSimpleName());
            fragmentTransaction.commitAllowingStateLoss();
             onBackKeyListener = (OnBackKeyListener) fragment;

        } else if (id == R.id.nav_settings) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = SettingsFragment.newInstance();
            fragmentTransaction.replace(R.id.content, fragment);
//            fragmentTransaction.addToBackStack(SettingsFragment.class.getSimpleName());
            fragmentTransaction.commitAllowingStateLoss();
//            setTitle("Settings");
            onBackKeyListener = null;
        } else if (id == R.id.nav_about) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = AboutFragment.newInstance();
            fragmentTransaction.replace(R.id.content, fragment);
//            fragmentTransaction.addToBackStack(AboutFragment.class.getSimpleName());
            fragmentTransaction.commitAllowingStateLoss();
            onBackKeyListener = null;
        } else if (id == R.id.nav_temp) {
            Intent intent = new Intent();
            intent.setClass(this, TemperatureChartTimeActivity.class);
            startActivity(intent);
            onBackKeyListener = null;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (null != onBackKeyListener) {
                return onBackKeyListener.onBackKeyDown();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public interface OnBackKeyListener {
        boolean onBackKeyDown();
    }
}
