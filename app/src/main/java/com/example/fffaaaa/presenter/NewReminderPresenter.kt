package com.example.fffaaaa.presenter

import android.content.Context
import android.widget.TextView
import com.example.fffaaaa.contract.ReminderContract
import com.example.fffaaaa.getDatePickerDialog
import com.example.fffaaaa.room.daos.SDao
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.daos.TDao
import com.example.fffaaaa.room.enitites.TaskEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import java.time.LocalDateTime
import kotlin.collections.ArrayList

class NewReminderPresenter(
    private var remView: ReminderContract.View?,
    private var sDao: SDao,
    private var tDao: TDao
) : ReminderContract.Presenter {
    companion object {
        lateinit var sectors: ArrayList<String>
    }
    var set = false
    @DelicateCoroutinesApi
    override fun onAddReminderButton(
        id: Long,
        icon: Int,
        title: String,
        remCount: Int,
        taskTitle: String,
        taskDescription: String,
        localDateTime: LocalDateTime
    )
    {
        val taskId = TaskEntity.insert(tDao, TaskEntity(0,id,taskTitle, taskDescription, localDateTime, false))
        val sector = SectorEntity(id, icon, title, remCount)
        var position = -1
        if (sectors.indexOf(sector.title) == -1) {
            sectors.add(sector.title)
            SectorEntity.insert(sDao, sector)
        } else {
            position = sectors.indexOf(sector.title)
            sector.remCount = SectorEntity.getSectorTaskCount(sDao, title)+1
            sectors[position] = sector.title
            SectorEntity.update(sDao, sector)
        }
             val task = TaskEntity.getTaskById(tDao, taskId)!!
             remView?.onSectorSaved(sector, position, task)
             remView?.onNotificationCreating(task, sector.icon)
    }

    override fun onDateTextViewClicked(context: Context, textView: TextView) {
        return getDatePickerDialog(context, textView).show()
    }

    fun updateSectorsStringList(list: ArrayList<SectorEntity>) {
        sectors.clear()
        list.forEach { sectors.add(it.title) }
    }
}

