package com.example.fffaaaa.presenter

import com.example.fffaaaa.Sectors
import com.example.fffaaaa.adapter.SectorAdapter
import com.example.fffaaaa.contract.StartContract
import com.example.fffaaaa.room.SDao
import com.example.fffaaaa.room.SectorEntity

class StartPresenter(
    private var mainView: StartContract.View?,
    private var dao: SDao,
) : StartContract.Presenter {
    /*init {
        mainView?.onSectorsLoaded(sectorList, this)
        mainView?.updateView()
    }*/

    override fun onSectorClicked(sectorEntity: SectorEntity) {
    }

    override fun onAddButtonClicked() {
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

}