package com.example.fffaaaa.activity

import android.content.SharedPreferences
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
import com.example.fffaaaa.presenter.*
import com.example.fffaaaa.room.SDao
import com.example.fffaaaa.room.SDatabase
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TDao
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_reminer_two.*
import kotlinx.android.synthetic.main.create_reminer_two.view.*

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
        /* setSupportActionBar(tb)
         val actionBar: ActionBar? = supportActionBar
         actionBar?.apply {
             setDisplayHomeAsUpEnabled(true)
             setHomeAsUpIndicator(R.drawable.menu_small)
             title = "QuickDeals"
         }
         tb.setNavigationOnClickListener {

         }*/
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
        sectorRview.adapter?.notifyItemRangeChanged(0, size)
        sectorRview.adapter?.notifyItemInserted(position)
    }

    override fun deleteSector(position: Int, size: Int) {
        sectorRview.adapter?.notifyItemRangeRemoved(position, size)
        sectorRview.adapter?.notifyItemRemoved(position)
    }

    override fun updateSectors(position: Int, size: Int) {
        sectorRview.adapter?.notifyItemRangeChanged(0, size)
        sectorRview.adapter?.notifyItemChanged(position)
    }

    override fun onSearchResult(sectors: List<SectorEntity>) {
        TODO("Not yet implemented")
    }

    override fun showSectorInfoDialog(sectorInfo: String) {
    }

    override fun hideSectorInfo() {
        TODO("Not yet implemented")
    }

    override fun showSectorInfo() {
        TODO("Not yet implemented")
    }

    override fun openReminderScreen(sectorId: Long) {
        Toast.makeText(this.baseContext, "ALALLALA", Toast.LENGTH_SHORT).show()
    }

    override fun openNewReminderScreen() {
        //startActivityForResult(NewReminderFragment.buildIntent(this, dao), NEW_NOTE_RESULT_CODE)
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
            supportFragmentManager.beginTransaction()
                .replace(com.example.fffaaaa.R.id.sector_info_fragment, secInfoFragment)
                .hide(secInfoFragment).commit()
        }
    }

    override fun hide(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(fragment).commit()
    }

    override fun show(fragment: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment).commit()
    }
}