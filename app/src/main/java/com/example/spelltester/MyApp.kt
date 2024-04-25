package com.example.spelltester

import android.app.*
import android.content.*
import com.example.spelltester.data.db.*
import com.example.spelltester.data.repositories.*
import com.example.spelltester.data.storage.*
import com.example.spelltester.ui.notification.*

class MyApp:Application() {
private val TAG = "KH_MY_APP"
    override fun onCreate() {
        super.onCreate()
        LocalStorage.invoke(this).logDebug("App started")
        AppDatabase.invoke(this)
        AppRepository.invoke(AppDatabase.getInstance())

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            ReminderNotification.id,
            ReminderNotification.name,
            ReminderNotification.importance).apply {
            description = ReminderNotification.description
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}