package com.example.fffaaaa.presenter

import android.content.Context
import android.os.Handler
import android.view.KeyEvent
import android.widget.ScrollView
import android.widget.TextView
import com.example.fffaaaa.contract.ReminderContract
import com.example.fffaaaa.getDatePickerDialog
import com.example.fffaaaa.room.SDao
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TDao
import com.example.fffaaaa.room.TaskEntity
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
    override fun onAddReminderButton(
        id: Long,
        icon: Int,
        title: String,
        remCount: Int,
        taskTitle: String,
        localDateTime: LocalDateTime
    )
    {
        val taskId = TaskEntity.insert(tDao, TaskEntity(0,id,taskTitle, localDateTime, false))
        var sector = SectorEntity(id, icon, title, remCount)
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
        TaskEntity.getTaskById(tDao, taskId)?.let {
            remView?.onSectorSaved(sector, position,
                it
            )}
    }

    override fun onDateTextViewClicked(context: Context, textView: TextView) {
        return getDatePickerDialog(context, textView).show()
    }

    fun updateSectorsStringList() {
        sectors = SectorEntity.getSectorsSortedByName(sDao, 1)
    }
}

