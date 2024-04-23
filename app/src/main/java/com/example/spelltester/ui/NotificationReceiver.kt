package com.example.spelltester.ui

import android.content.*
import android.util.*

const val TYPE_REMINDER = 1
private const val TAG="KH_NOTI_REC"
class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ")
        ReminderNotification.getInstance().showNotification()
    }
}