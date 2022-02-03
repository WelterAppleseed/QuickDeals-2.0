package com.example.fffaaaa.contract

import android.content.SharedPreferences
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.adapter.SectorAdapter
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.enitites.TaskEntity

interface FragmentContract {
    interface Presenter {
        fun attachAdapter(adapter: SectorAdapter)
        fun onSectorClicked(sector: SectorEntity)
        fun insertSectorToStartView(sector: SectorEntity, position: Int)
        fun toolbarModify(sharedPreferences: SharedPreferences, menuItem: MenuItem)
        fun onSortComparatorChanged(list: ArrayList<SectorEntity>) {}
        fun attachComparator(sharedPreferences: SharedPreferences)
        fun updateSectorInfoTaskCount(count: Int)
        fun onExistingTasksCreating() : RecyclerView
        fun delete(sector: SectorEntity)
        fun onNotificationEventReceived(taskEntity: TaskEntity, icon: Int)
    }
}