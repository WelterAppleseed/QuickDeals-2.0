package com.example.fffaaaa.presenter

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.adapter.EmptyAdapter
import com.example.fffaaaa.adapter.Page
import com.example.fffaaaa.adapter.ParentAdapter
import com.example.fffaaaa.adapter.TasksAdapter
import com.example.fffaaaa.contract.SSI
import com.example.fffaaaa.getTime
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TDao
import com.example.fffaaaa.room.TaskEntity
import com.example.fffaaaa.updateSectorInfo
import com.example.fffaaaa.updatedPages
import kotlinx.android.synthetic.main.sector_info.view.*
import java.time.LocalDateTime

class SectorInfoPresenter(private var view: SSI.View, private var dao: TDao) : SSI.Presenter {

    override fun replaceInfo(
        layout: LinearLayout,
        sectorEntity: SectorEntity,
        position: Int,
        context: Context,
        fragmentPresenter: FragmentPresenter
    ) {
        Log.i("Function", "replaceInfo")
        val list = TaskEntity.getTaskListBySectorId(sectorEntity.id, dao)
        updateSectorInfo(
            layout,
            list,
            sectorEntity,
            position,
            context,
            LocalDateTime.now(),
            dao,
            fragmentPresenter
        )
    }

    fun updateRecyclers(taskEntity: TaskEntity, recyclerView: RecyclerView) {
        var relative: RelativeLayout
        val parentAdapter = view.getParentRec().adapter as ParentAdapter
        if (LocalDateTime.now() > taskEntity.taskDate) {
            Log.i("SectorInfoPresenter", "updateRecyclers: 1")
            relative = recyclerView.findViewHolderForLayoutPosition(0)?.itemView as RelativeLayout

            val childRecycler = (relative.findViewById<RecyclerView>(R.id.child_recycler_view)) as RecyclerView
            if (childRecycler.adapter is EmptyAdapter) {
                Log.i("SectorInfoPresenter", "updateRecyclers: 1.1")
                parentAdapter.addToFullTaskList(taskEntity)
                parentAdapter.notifyItemRangeChanged(0, 1)
            } else {
                Log.i("SectorInfoPresenter", "updateRecyclers: 1.2")
                ((childRecycler.adapter) as TasksAdapter).insertToList(
                    taskEntity
                )
                recyclerView.adapter?.notifyItemRangeChanged(0, 1)
            }
        } else {
            Log.i("SectorInfoPresenter", "updateRecyclers: 2")
            val pages = parentAdapter.getPages()
            if (pages[0].taskList.isEmpty()) {
                pages[0].taskList.add(taskEntity)
                pages[0].title = getTime(taskEntity.taskDate)
            } else
                updatedPages(taskEntity, parentAdapter.getPages())
            parentAdapter.setPages(pages)
            parentAdapter.notifyItemRangeChanged(1, 1)

        }
        parentAdapter.updateSectorCount(true)
    }
}