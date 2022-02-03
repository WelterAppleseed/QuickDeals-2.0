package com.example.fffaaaa.activity

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fffaaaa.R
import com.example.fffaaaa.adapter.SectorAdapter
import com.example.fffaaaa.contract.FragmentInterface
import com.example.fffaaaa.contract.NavigationContract
import com.example.fffaaaa.contract.SSI
import com.example.fffaaaa.contract.StartContract
import com.example.fffaaaa.fragments.NewReminderFragment
import com.example.fffaaaa.fragments.SectorInfoFragment
import com.example.fffaaaa.notifications.AlarmManagerBroadcastReceiver
import com.example.fffaaaa.notifications.NotificationUtils
import com.example.fffaaaa.presenter.*
import com.example.fffaaaa.room.*
import com.example.fffaaaa.room.daos.SDao
import com.example.fffaaaa.room.daos.TDao
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.enitites.TaskEntity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_reminer_two.*
import kotlinx.android.synthetic.main.create_reminer_two.view.*
import kotlinx.coroutines.DelicateCoroutinesApi


class MainActivity : AppCompatActivity(),
    StartContract.View, FragmentInterface.View {
    private lateinit var sDao: SDao
    private lateinit var tDao: TDao
    private var sectorInfoPresenter: SectorInfoPresenter? = null
    private var fragmentPresenter: FragmentPresenter? = null
    private var navigationPresenter: NavigationPresenter? = null
    private lateinit var newReminderFragment: NewReminderFragment
    private lateinit var pref: SharedPreferences
    private lateinit var sectorInfoFragment: SectorInfoFragment
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tb.setOnMenuItemClickListener {
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
        addButton.setOnClickListener {
            navigationPresenter!!.showFragment(
                newReminderFragment
            )
        }
    }


    @DelicateCoroutinesApi
    @SuppressLint("NotifyDataSetChanged")
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

    override fun dismissNotification(id: Long) {
        NotificationUtils().dismissNotification(id, this)
    }

    override fun update(firstFragment: Fragment, secondFragment: Fragment) {
        if (firstFragment is NavigationContract.View) {
            navigationPresenter?.let { firstFragment.attachNavigationPresenter(it) }
            fragmentPresenter?.let { firstFragment.attachFragmentPresenter(it) }
            supportFragmentManager.beginTransaction().replace(R.id.new_rem_fragment, firstFragment)
                .hide(firstFragment).commit()
        }
        if (secondFragment is SSI.View && secondFragment is NavigationContract.View) {
            navigationPresenter?.let { secondFragment.attachNavigationPresenter(it) }
            sectorInfoPresenter?.let { secondFragment.attachSectorInfoPresenter(it) }
            fragmentPresenter?.let { secondFragment.attachFragmentPresenter(it) }
            supportFragmentManager.beginTransaction()
                .replace(R.id.sector_info_fragment, secondFragment)
                .hide(secondFragment).commit()
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