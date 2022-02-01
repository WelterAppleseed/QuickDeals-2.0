package com.example.fffaaaa.contract

import android.app.DatePickerDialog
import androidx.lifecycle.LiveData
import com.example.fffaaaa.adapter.SectorAdapter
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.presenter.StartPresenter
import com.example.fffaaaa.room.SectorEntity

interface StartContract {
    interface View {
        //Method to update RecyclerView
        fun onSectorsLoaded(sectors: ArrayList<SectorEntity>, fragmentPresenter: FragmentPresenter)

        fun showNewSector(position: Int, size: Int)

        fun deleteSector(position: Int, size: Int)

        fun updateSectors(position: Int, size: Int)

        fun onSearchResult(sectors: List<SectorEntity>)

        fun showSectorInfoDialog(sectorInfo: String)

        fun hideSectorInfo()

        fun showSectorInfo()

        fun openReminderScreen(sectorId: Long)

        fun openNewReminderScreen()
    }
    interface Presenter {
        // method to be called when
        // the button is clicked
        fun onSectorClicked(sectorEntity: SectorEntity)

        fun onAddButtonClicked()

        // method to destroy
        // lifecycle of MainActivity
        fun onDestroy()

    }
    interface SectorModel {
            fun updateSector(taskCount : Int)

    }
}