package com.example.fffaaaa.contract

import android.content.Context
import android.widget.ScrollView
import android.widget.TextView
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TaskEntity
import java.time.LocalDateTime

interface ReminderContract {

    interface View {

        fun updateView()

        fun over()

        fun updateCategory()

        fun onSectorSaved(sector: SectorEntity, position: Int, taskEntity: TaskEntity)

        fun openSectorScreen(sectorId: Long)

        fun transit(up: Boolean)

        fun onSortList(list: ArrayList<SectorEntity>)
    }
    interface Presenter {
        fun onAddReminderButton(id : Long, icon : Int, title: String, remCount: Int, taskTitle: String, localDateTime: LocalDateTime)

        fun onDateTextViewClicked(context: Context, textView: TextView)

    }
}