package com.example.fffaaaa.presenter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.fragments.SectorInfoFragment
import com.example.fffaaaa.adapter.EmptyAdapter
import com.example.fffaaaa.adapter.ParentAdapter
import com.example.fffaaaa.adapter.TasksAdapter
import com.example.fffaaaa.contract.SSI
import com.example.fffaaaa.getTime
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.daos.TDao
import com.example.fffaaaa.room.enitites.TaskEntity
import com.example.fffaaaa.updateSectorInfo
import com.example.fffaaaa.updatedPages
import kotlinx.coroutines.DelicateCoroutinesApi
import java.time.LocalDateTime

class SectorInfoPresenter(private var view: SSI.View, private var dao: TDao) : SSI.Presenter {

    companion object {
        lateinit var sector: SectorEntity
    }

    @DelicateCoroutinesApi
    override fun replaceInfo(
        layout: LinearLayout,
        sectorEntity: SectorEntity,
        position: Int,
        context: Context,
        fragmentPresenter: FragmentPresenter
    ) {
        Log.i("Function", "replaceInfo")
        val list = TaskEntity.getTaskListBySectorId(sectorEntity.id, dao)
        sector = sectorEntity
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

    @DelicateCoroutinesApi
    fun updateRecyclers(taskEntity: TaskEntity, recyclerView: RecyclerView) {
        val relative: RelativeLayout
        val parentAdapter = view.getParentRec().adapter as ParentAdapter
        if (LocalDateTime.now() > taskEntity.taskDate) {
            Log.i("SectorInfoPresenter", "updateRecyclers: 1")
            relative = recyclerView.findViewHolderForLayoutPosition(0)?.itemView as RelativeLayout

            val childRecycler =
                (relative.findViewById(R.id.child_recycler_view)) as RecyclerView
            if (childRecycler.adapter is EmptyAdapter) {
                Log.i("SectorInfoPresenter", "updateRecyclers: 1.1")
                parentAdapter.changeAdapterByAddingTask(taskEntity)
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

    @DelicateCoroutinesApi
    override fun modifyToolbar(context: Context, menuItem: MenuItem) {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle(context.getString(R.string.deleting_sector_title))
            dialog.setMessage(context.getString(R.string.deleting_sector_desc))
            dialog.setPositiveButton(android.R.string.yes) { _, _ ->
                TaskEntity.delete(dao, TaskEntity.getTaskListBySectorId(sector.id, dao))
                view.requestSectorDeleting(sector)
                if (view is SectorInfoFragment) {
                    (view as SectorInfoFragment).changeVisibilityOfFragment(true)
                }
            }
            dialog.setNegativeButton(android.R.string.no) { dial, _ ->
                run {
                    dial.cancel()
                }
            }
            dialog.show()
    }
}