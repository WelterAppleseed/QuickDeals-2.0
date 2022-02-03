package com.example.fffaaaa.contract

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.view.MenuItem
import android.widget.Toolbar
import androidx.annotation.Nullable
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.activity.NewReminderFragment
import com.example.fffaaaa.adapter.SectorAdapter
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TaskEntity
import com.google.android.material.navigation.NavigationView
import java.time.LocalDateTime

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