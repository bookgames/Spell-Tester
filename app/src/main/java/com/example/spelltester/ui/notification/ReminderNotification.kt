package com.example.spelltester.ui.notification

import android.Manifest.permission.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.os.*
import androidx.activity.*
import androidx.activity.result.contract.*
import com.example.spelltester.*
import com.example.spelltester.R
import com.example.spelltester.data.storage.*

class ReminderNotification(private val activity: ComponentActivity) {
    private val notification: Notification
    private val notificationManager: NotificationManager =
        activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmTime = 1000 * 60 * 60 * 24 // 24 hours

    init {
        val activityPendingIntent = PendingIntent.getActivity(
            activity,
            0,
            Intent(activity, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        notification = Notification.Builder(activity, id)
            .setContentTitle(activity.getString(R.string.reminder))
            .setContentText(activity.getString(R.string.it_is_studying_time))
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
        const val TAG = "KH_REMINDER_NOTIF"

        private var instance: ReminderNotification? = null
        fun getInstance(activity: ComponentActivity? = null): ReminderNotification {
            return instance ?: synchronized(this) {
                instance ?: ReminderNotification(activity!!).also { instance = it }
            }
        }

    }

    fun handleNotification() {
        if (isNotificationPermissionGranted()) {
            scheduleNotification()
        } else {
            requestNotificationPermission()
        }
    }

    private fun scheduleNotification() {
        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            0,
            Intent(activity, NotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC, System.currentTimeMillis() + alarmTime, pendingIntent
        )
    }

    private fun requestNotificationPermission() {
        val requestNotification = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                scheduleNotification()
            } else {
                LocalStorage.getInstance().logDebug(TAG, "Notification permission denied")
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requestNotification.launch(POST_NOTIFICATIONS)
            return
        }
        activity.apply {
            if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.notification_permission_needed))
                    .setMessage(getString(R.string.we_need_notification_permission_to_schedule_reminders))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        requestNotification.launch(POST_NOTIFICATIONS)
                    }.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            } else {
                requestNotification.launch(POST_NOTIFICATIONS)
            }
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.checkSelfPermission(POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permissions are not required for older versions
        }
    }

}