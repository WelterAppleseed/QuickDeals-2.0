package com.example.fffaaaa.notifications


import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.fffaaaa.R
import kotlin.properties.Delegates


class NotificationService : IntentService("NotificationService") {
    private lateinit var mNotification: Notification
    private var notificationId by Delegates.notNull<Int>()

    override fun onCreate() {
        super.onCreate()
        if (!AlarmManagerBroadcastReceiver.isWorking) {
            Log.i("Notification Service", "Foreground started")
            startForeground()
        }
    }
    @SuppressLint("NewApi")
    private fun createChannel() {


        val context = this.applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        notificationChannel.enableVibration(true)
        notificationChannel.setShowBadge(true)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.parseColor("#e8334a")
        notificationChannel.description = getString(R.string.category_tv_text)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(notificationChannel)

    }

    companion object {

        const val CHANNEL_ID = "QD2_CHANNEL_ID"
        const val CHANNEL_NAME = "Sample Notification"
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onHandleIntent(intent: Intent?) {

        //Create Channel
        createChannel()

        var timestamp: Long = 0
        if (intent != null && intent.extras != null) {
            timestamp = intent.extras!!.getLong("timestamp")
        }

        if (timestamp > 0) {


            val context = this.applicationContext
            val notifyIntent = Intent(this, AlarmManagerBroadcastReceiver::class.java)

            val title = context.getString(R.string.notification_title, context.getString(R.string.app_name), intent!!.getStringExtra("title"))
            val message = context.getString(R.string.notification_desc, if (intent.getStringExtra("description")!! == "") "" else context.getString(R.string.notification_desc_with, intent.getStringExtra("description")!!))

           /* notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("message", message)
            notifyIntent.putExtra("notification", true)

            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

*/
            val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            mNotification = Notification.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(ResourcesCompat.getDrawable(resources, intent.getIntExtra("icon", R.drawable.file_def), null)!!.toBitmap())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(Notification.BigTextStyle()
                    .bigText(message))
                .setContentText(message)
                .build()
            notificationId = intent.getIntExtra("id", 0)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, mNotification)
        }
    }
    private fun startForeground() {
        val channelId = "333"
        val notificationBuilder = NotificationCompat.Builder(
            applicationContext, channelId
        )
        notificationBuilder.setChannelId(channelId)
    }

}