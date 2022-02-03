package com.example.fffaaaa.contract

import android.content.Context
import android.widget.TextView
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.enitites.TaskEntity
import java.time.LocalDateTime

interface ReminderContract {

    interface View {

        fun updateView()

        fun over()

        fun updateCategory()

        fun onSectorSaved(sector: SectorEntity, position: Int, taskEntity: TaskEntity)

        fun transit(up: Boolean)

        fun onSortList(list: ArrayList<SectorEntity>)

        fun onNotificationCreating(taskEntity: TaskEntity, icon: Int)
    }
    interface Presenter {
        fun onAddReminderButton(id : Long, icon : Int, title: String, remCount: Int, taskTitle: String,  taskDescription: String, localDateTime: LocalDateTime)

        fun onDateTextViewClicked(context: Context, textView: TextView)

    }
}