package me.pjq.rpicar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem

import me.pjq.rpicar.realm.Settings

class MainNavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AboutFragment.OnFragmentInteractionListener {
    private var onBackKeyListener: OnBackKeyListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_navigation)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        initSettings()
        initFirstFragment()
    }

    private fun initSettings() {
        DataManager.init(applicationContext)
        val settings = DataManager.realm.where(Settings::class.java).findFirst()
        if (null != settings && !TextUtils.isEmpty(settings.getHost())) {
            CarControllerApiService.Config.HOST = settings.getHost()
        }
    }

    private fun initFirstFragment() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = CarControllerMainFragment.newInstance()
        fragmentTransaction.replace(R.id.content, fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = CarControllerMainFragment.newInstance()
            fragmentTransaction.replace(R.id.content, fragment)
            //            fragmentTransaction.addToBackStack(SettingsFragment.class.getSimpleName());
            fragmentTransaction.commitAllowingStateLoss()
            onBackKeyListener = null
        } else if (id == R.id.nav_slideshow) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = CaptureVideoFragment.newInstance()
            fragmentTransaction.replace(R.id.content, fragment)
            //            fragmentTransaction.addToBackStack(CaptureVideoFragment.class.getSimpleName());
            fragmentTransaction.commitAllowingStateLoss()
            onBackKeyListener = fragment

        } else if (id == R.id.nav_settings) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = SettingsFragment.newInstance()
            fragmentTransaction.replace(R.id.content, fragment)
            //            fragmentTransaction.addToBackStack(SettingsFragment.class.getSimpleName());
            fragmentTransaction.commitAllowingStateLoss()
            //            setTitle("Settings");
            onBackKeyListener = null
        } else if (id == R.id.nav_about) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = AboutFragment.newInstance()
            fragmentTransaction.replace(R.id.content, fragment)
            //            fragmentTransaction.addToBackStack(AboutFragment.class.getSimpleName());
            fragmentTransaction.commitAllowingStateLoss()
            onBackKeyListener = null
        } else if (id == R.id.nav_temp) {
            val intent = Intent()
            intent.setClass(this, TemperatureChartTimeActivity::class.java)
            startActivity(intent)
            onBackKeyListener = null
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (null != onBackKeyListener) {
                return onBackKeyListener!!.onBackKeyDown()
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    interface OnBackKeyListener {
        fun onBackKeyDown(): Boolean
    }
}
