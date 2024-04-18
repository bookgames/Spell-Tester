package com.example.spelltester.ui

import android.app.*
import android.content.*
import android.os.*
import com.example.spelltester.*

class ReminderNotification(private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun showNotification() {
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = Notification.Builder(context, id)
            .setContentTitle("Reminder")
            .setContentText("Don't forget to practice spelling")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(activityPendingIntent)
            .build()
        notificationManager.notify(0, notification)

    }

    companion object {
        const val importance: Int = NotificationManager.IMPORTANCE_LOW
        const val id = "reminder"
        const val name = "Spell Tester Reminder"
        const val description = "Reminder to practice spelling"
    }
}