package com.example.fffaaaa.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.fffaaaa.R


class AlarmManagerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, NotificationService::class.java)
        service.putExtra("title", intent.getStringExtra("title"))
        service.putExtra("description", intent.getStringExtra("description"))
        service.putExtra("icon", intent.getIntExtra("icon", R.drawable.file_def))
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))
        if (!isWorking) {
            Log.i("AlarmManagerBroadcastReceiver", "Foreground Service")
            context.startForegroundService(service)
        } else {
            Log.i("AlarmManagerBroadcastReceiver", "Service")
            context.startService(Intent(service))
            val updatingIntent = Intent("UPDATE_PAGES")
            updatingIntent.putExtra("id", intent.getLongExtra("id", 0))
            updatingIntent.putExtra("sector_id", intent.getLongExtra("sector_id", 0))
            context.sendBroadcast(updatingIntent)
        }
    }

    companion object {
        var isWorking = false
    }
}
