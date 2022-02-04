package com.example.fffaaaa.notifications

import android.app.*
import android.content.Intent
import com.example.fffaaaa.room.enitites.TaskEntity
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


class NotificationUtils {

    fun setNotification(taskEntity: TaskEntity, icon: Int, activity: Activity) {

        if (taskEntity.taskDate.toEpochSecond(ZoneOffset.UTC) > LocalDateTime.now().toEpochSecond(
                ZoneOffset.UTC)) {

            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmManagerBroadcastReceiver::class.java) // AlarmReceiver1 = broadcast receiver

            alarmIntent.putExtra("id", taskEntity.id)
            alarmIntent.putExtra("sector_id", taskEntity.sectorId)
            alarmIntent.putExtra("title", taskEntity.taskTitle)
            alarmIntent.putExtra("description", taskEntity.taskDescription)
            alarmIntent.putExtra("icon", icon)
            alarmIntent.putExtra("timestamp", taskEntity.taskDate.toEpochSecond(ZoneOffset.UTC))
            val pendingIntent = PendingIntent.getBroadcast(activity, taskEntity.id.toInt(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            val calendar = Calendar.getInstance().time
            calendar.time = (taskEntity.taskDate.toEpochSecond(ZoneOffset.UTC)*1000 - 10800000)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.time, pendingIntent)

        }
    }
    fun dismissNotification(id: Long, activity: Activity) {
        val notificationIntent = Intent(activity.applicationContext, AlarmManagerBroadcastReceiver::class.java)
        val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(activity, id.toInt(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.cancel(pendingIntent)
    }
}