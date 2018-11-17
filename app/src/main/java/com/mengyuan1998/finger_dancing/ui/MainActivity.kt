package com.mengyuan1998.finger_dancing.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mengyuan1998.finger_dancing.R
import com.mengyuan1998.finger_dancing.Utilities.MessageManager
import com.mengyuan1998.finger_dancing.ui.export.ExportFragment
import com.mengyuan1998.finger_dancing.ui.scan.ScanDeviceFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private const val PREFS_GLOBAL = "global"
private const val KEY_COMPLETED_ONBOARDING = "completed_onboarding"

open class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        /*val localLayoutParams = window.attributes
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags*/

        // Checking if we should on-board the user the first time.
        /*val prefs = getSharedPreferences(PREFS_GLOBAL, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_COMPLETED_ONBOARDING, false)) {
            finish()
            startActivity(Intent(this, IntroActivity::class.java))
        }*/

        requestPermission()

        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.new_toolbar))

        val fragmentList = listOf<Fragment>(
                //ScanDeviceFragment.newInstance(),
                //ControlDeviceFragment.newInstance(),
                ExportFragment.newInstance()
        )
        MessageManager.getInstance().initTTS(applicationContext)

        view_pager.adapter = MyAdapter(supportFragmentManager, fragmentList)
        view_pager.offscreenPageLimit = 2
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var prevMenuItem: MenuItem? = null
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem?.isChecked = false
                } else {
                    //bottom_navigation.menu.getItem(0).isChecked = false
                }
               // bottom_navigation.menu.getItem(position).isChecked = true
               // prevMenuItem = bottom_navigation.menu.getItem(position)
            }

        })
        /*bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_scan -> view_pager.currentItem = 0
                R.id.item_control->view_pager.currentItem = 1
                R.id.item_export -> view_pager.currentItem = 2

            }
            false
        }*/
    }

    fun navigateToPage(pageId: Int) {
        view_pager.currentItem = pageId
    }

    private fun requestPermission() {
        val hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1)
    }

    override fun onDestroy() {
        MessageManager.getInstance().releaseTTS()
        super.onDestroy()
    }


    class MyAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }
    }



}