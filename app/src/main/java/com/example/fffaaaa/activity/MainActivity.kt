package com.example.fffaaaa.activity

import android.content.*
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fffaaaa.R
import com.example.fffaaaa.adapter.SectorAdapter
import com.example.fffaaaa.contract.FragmentInterface
import com.example.fffaaaa.contract.NavigationContract
import com.example.fffaaaa.contract.SSI
import com.example.fffaaaa.contract.StartContract
import com.example.fffaaaa.notifications.NotificationUtils
import com.example.fffaaaa.presenter.*
import com.example.fffaaaa.room.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_reminer_two.*
import kotlinx.android.synthetic.main.create_reminer_two.view.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import android.util.Log

import com.example.fffaaaa.notifications.AlarmManagerBroadcastReceiver




class MainActivity : AppCompatActivity(),
    StartContract.View, FragmentInterface.View {
    lateinit var sDao: SDao
    lateinit var tDao: TDao
    var sectorInfoPresenter: SectorInfoPresenter? = null
    var fragmentPresenter: FragmentPresenter? = null
    var navigationPresenter: NavigationPresenter? = null
    private lateinit var newReminderFragment: NewReminderFragment
    private lateinit var pref: SharedPreferences
    private lateinit var sectorInfoFragment: SectorInfoFragment
    private lateinit var sortStyle: Comparator<SectorEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tb.setOnMenuItemClickListener { it ->
            fragmentPresenter?.toolbarModify(pref, it)
            return@setOnMenuItemClickListener true
        }
        sDao = SDatabase.getAppDatabase(applicationContext)?.sectors()!!
        tDao = SDatabase.getAppDatabase(applicationContext)?.tasks()!!
        pref = this.getPreferences(MODE_PRIVATE)
        newReminderFragment = NewReminderFragment(sDao, tDao)
        sectorInfoFragment = SectorInfoFragment(tDao)
        navigationPresenter = NavigationPresenter(this)
        fragmentPresenter = FragmentPresenter(this, newReminderFragment, sectorInfoFragment, sDao)
        sectorInfoPresenter = SectorInfoPresenter(sectorInfoFragment, tDao)
        update(newReminderFragment, sectorInfoFragment)
        sectorRview.layoutManager = GridLayoutManager(this, 2)
        addButton.attachToRecyclerView(sectorRview)
        addButton.setOnClickListener(View.OnClickListener {
            navigationPresenter!!.showFragment(
                newReminderFragment
            )
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSectorsLoaded(
        sectors: ArrayList<SectorEntity>,
        fragmentPresenter: FragmentPresenter
    ) {
        NewReminderPresenter.sectors = SectorEntity.getAllSectorTitles(sDao)
        sectorRview.adapter = SectorAdapter(sectors, fragmentPresenter)
        fragmentPresenter.attachAdapter(sectorRview.adapter as SectorAdapter)
        fragmentPresenter.attachComparator(pref)
        sectorRview.adapter?.notifyDataSetChanged()
    }

    override fun showNewSector(position: Int, size: Int) {
        sectorRview.adapter?.notifyItemRangeChanged(position, size)
        sectorRview.adapter?.notifyItemInserted(position)
    }

    override fun deleteSector(position: Int, size: Int) {
        sectorRview.adapter?.notifyItemRangeRemoved(position, size)
        sectorRview.adapter?.notifyItemRemoved(position)
    }

    override fun updateSectors(position: Int, size: Int) {
        sectorRview.adapter?.notifyItemRangeChanged(position, size)
        sectorRview.adapter?.notifyItemChanged(position)
    }
    override fun updateNotifications(taskEntity: TaskEntity, icon: Int) {
        NotificationUtils().setNotification(taskEntity, icon, this)
    }

    override fun update(newRemFragment: Fragment, secInfoFragment: Fragment) {
        if (newRemFragment is NavigationContract.View) {
            navigationPresenter?.let { newRemFragment.attachNavigationPresenter(it) }
            fragmentPresenter?.let { newRemFragment.attachFragmentPresenter(it) }
            supportFragmentManager.beginTransaction().replace(com.example.fffaaaa.R.id.new_rem_fragment, newRemFragment)
                .hide(newRemFragment).commit()
        }
        if (secInfoFragment is SSI.View && secInfoFragment is NavigationContract.View) {
            navigationPresenter?.let { secInfoFragment.attachNavigationPresenter(it) }
            sectorInfoPresenter?.let { secInfoFragment.attachSectorInfoPresenter(it) }
            fragmentPresenter?.let { secInfoFragment.attachFragmentPresenter(it) }
            supportFragmentManager.beginTransaction()
                .replace(com.example.fffaaaa.R.id.sector_info_fragment, secInfoFragment)
                .hide(secInfoFragment).commit()
        }
    }

    override fun hide(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim, R.anim.nav_default_pop_enter_anim, R.anim.nav_default_pop_exit_anim)
            .addToBackStack(null)
            .hide(fragment)
            .commit()
    }

    override fun show(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim, R.anim.nav_default_pop_enter_anim, R.anim.nav_default_pop_exit_anim)
            .addToBackStack(null)
            .show(fragment)
            .commit()
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        AlarmManagerBroadcastReceiver.isWorking = hasFocus
        super.onWindowFocusChanged(hasFocus)
    }
    override fun onBackPressed() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }
}