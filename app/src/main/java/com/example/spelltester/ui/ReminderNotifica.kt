package com.example.spelltester.ui

import android.app.*
import android.content.*
import android.os.*
import com.example.spelltester.*

class ReminderNotification(context: Context) {
    private val notification: Notification
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
         notification = Notification.Builder(context, id)
            .setContentTitle(context.getString(R.string.reminder))
            .setContentText(context.getString(R.string.it_is_studying_time))
            .setSmallIcon(R.drawable.spell_check_icon)
            .setAutoCancel(true)
            .setContentIntent(activityPendingIntent)
            .build()

    }
    fun showNotification() {
        notificationManager.notify(0, notification)
    }

    companion object {
        const val importance: Int = NotificationManager.IMPORTANCE_LOW
        const val id = "reminder"
        const val name = "Spell Tester Reminder"
        const val description = "Reminder to practice spelling"
        //edger
        private var instance: ReminderNotification? = null
        fun getInstance(context: Context?=null): ReminderNotification {
            return instance ?: synchronized(this) {
                instance ?: ReminderNotification(context!!).also { instance = it }
            }
        }

    }
}