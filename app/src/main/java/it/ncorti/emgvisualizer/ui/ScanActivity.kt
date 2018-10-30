package it.ncorti.emgvisualizer.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import it.ncorti.emgvisualizer.R
import it.ncorti.emgvisualizer.ui.export.ExportFragment
import it.ncorti.emgvisualizer.ui.scan.ScanDeviceFragment
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class ScanActivity : MainActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_scan)
        //setSupportActionBar(findViewById(R.id.new_toolbar))

        val fragmentList = listOf<Fragment>(
                ScanDeviceFragment.newInstance()
                //ControlDeviceFragment.newInstance(),

        )

        view_pager.adapter = MyAdapter(supportFragmentManager, fragmentList)
        view_pager.offscreenPageLimit = 1
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


}
