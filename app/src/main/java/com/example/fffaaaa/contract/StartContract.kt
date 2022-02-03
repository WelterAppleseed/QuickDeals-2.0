package com.example.fffaaaa.contract

import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.enitites.TaskEntity

interface StartContract {
    interface View {
        //Method to update RecyclerView
        fun onSectorsLoaded(sectors: ArrayList<SectorEntity>, fragmentPresenter: FragmentPresenter)

        fun showNewSector(position: Int, size: Int)

        fun deleteSector(position: Int, size: Int)

        fun updateSectors(position: Int, size: Int)

        fun updateNotifications(taskEntity: TaskEntity, icon: Int)

        fun dismissNotification(id: Long)
    }
}