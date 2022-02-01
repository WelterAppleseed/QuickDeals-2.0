package com.example.fffaaaa.presenter

import android.content.SharedPreferences
import android.util.Log
import android.view.MenuItem
import androidx.annotation.Nullable
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.Sectors
import com.example.fffaaaa.activity.SectorInfoFragment
import com.example.fffaaaa.adapter.SectorAdapter
import com.example.fffaaaa.contract.*
import com.example.fffaaaa.room.SDao
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TaskEntity
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class FragmentPresenter(
    private var startView: StartContract.View,
    private var remView: ReminderContract.View,
    private val secView: SSI.View,
    private val dao: SDao
) : FragmentContract.Presenter, SectorPrivateInterface {
    private var sectors = SectorEntity.getAllSectors(dao)
    lateinit var sectorAdapter: SectorAdapter
    private lateinit var comparator: Comparator<SectorEntity>

    init {
        if (sectors.isEmpty()) {
            val allSector =
                SectorEntity(Sectors.VARIOUS.ordinal.toLong(), R.drawable.file_def, "Various", 0)
            sectors.add(allSector)
            SectorEntity.insert(dao, allSector)
        }
        Collections.sort(sectors, SectorEntity.BY_ID)
        startView.onSectorsLoaded(sectors, this)
    }

    override fun attachAdapter(adapter: SectorAdapter) {
        Log.i("FragmentPresenter", "attachAdapter")
        sectorAdapter = adapter
    }

    fun delete() {
        /*SectorEntity.delete(dao, sector)
        val position = sectorAdapter.sectorList.indexOf(sector)
        sectorAdapter.sectorList.remove(sector)
        println("${sectorAdapter.sectorList} and ${NewReminderPresenter.sectors} and $sector")
        NewReminderPresenter.sectors.remove(sector.title)
        startView.deleteSector(position, sectorAdapter.sectorList.size)*/
    }

    override fun onSectorClicked(sector: SectorEntity) {
        Log.i("FragmentPresenter", "onSectorClicked")
        secView.update(sector, this, sectors.indexOf(sector))
    }

    override fun insertSectorToStartView(sector: SectorEntity, position: Int) {
        Log.i("FragmentPresenter", "insertSectorToStartView")
        sectorAdapter.sectorList.add(sector)
        startView.showNewSector(
            sectorAdapter.sectorList.size - 1,
            sectorAdapter.sectorList.size
        )
    }

    override fun changeSectorFromStartView(sectorEntity: SectorEntity, position: Int, taskEntity: TaskEntity, callsForAdding: Boolean) {
        Log.i("FragmentPresenter", "changeSectorFromStartView")
        if ((secView as SectorInfoFragment).isVisible && callsForAdding) {
            secView.taskListDynamicallyUpdate(taskEntity)
        }
        SectorEntity.update(dao, sectorEntity)
        sectorAdapter.sectorList[position] = sectorEntity
        startView.updateSectors(position, sectorAdapter.sectorList.size)
    }

    override fun toolbarModify(sharedPreferences: SharedPreferences, menuItem: MenuItem) {
        Log.i("FragmentPresenter", "toolbarModify")
        var prefV = sharedPreferences.getString("sort", "")
        when (menuItem.itemId) {
            R.id.alph_sort -> {
                println("1")
                if (prefV != "alphabet") {
                    sharedPreferences.edit().putString("sort", "alphabet").apply()
                    comparator = SectorEntity.BY_ALPHABET
                    onSortComparatorChanged(sectorAdapter.sectorList)
                }
            }
            R.id.def_sort -> {
                if (prefV != "default") {
                    sharedPreferences.edit().putString("sort", "default").apply()
                    comparator = SectorEntity.BY_ID
                    onSortComparatorChanged(sectorAdapter.sectorList)
                }
            }
        }


    }

    override fun onSortComparatorChanged(list: ArrayList<SectorEntity>) {
        Log.i("FragmentPresenter", "onSortComparatorChanged")
        Collections.sort(list, comparator)
        Collections.sort(sectors, comparator)
        remView.onSortList(list)
        startView.updateSectors(0, sectors.size)
    }
    fun ggf() {
    }

    override fun attachComparator(sharedPreferences: SharedPreferences) {
        Log.i("FragmentPresenter", "attachComparator")
        comparator = if (sharedPreferences.getString("sort", "") == "alphabet") {
            SectorEntity.BY_ALPHABET
        } else {
            SectorEntity.BY_ID
        }
    }

    override fun updateSectorInfoTaskCount(count: Int) {
        Log.i("FragmentPresenter", "updateSectorInfoTaskCount")
        secView.taskCountDynamicallyUpdate(count)
    }

    override fun onExistingTasksCreating() : RecyclerView {
        Log.i("FragmentPresenter", "onExistingTasksCreating")
        return secView.getParentRec()
    }
}
