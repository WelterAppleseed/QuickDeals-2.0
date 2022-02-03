package com.example.fffaaaa.contract

import android.content.Context
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.presenter.SectorInfoPresenter
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TDao
import com.example.fffaaaa.room.TaskEntity
import java.util.*

interface SSI {
    interface View {
        fun update(sectorEntity: SectorEntity, fragmentPresenter: FragmentPresenter, position: Int)
        fun taskCountDynamicallyUpdate(count: Int)
        fun taskListDynamicallyUpdate(taskEntity: TaskEntity)
        fun attachSectorInfoPresenter(sectorInfoPresenter: SectorInfoPresenter)
        fun getParentRec() : RecyclerView
        fun requestSectorDeleting(sector: SectorEntity)
    }

    interface Presenter {
        fun replaceInfo(
            layout: LinearLayout,
            sectorEntity: SectorEntity,
            position: Int,
            context: Context,
            fragmentPresenter: FragmentPresenter
        )
        fun modifyToolbar(context: Context, menuItem: MenuItem)
    }
}