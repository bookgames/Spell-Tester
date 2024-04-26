package com.example.spelltester.data.storage

import android.app.*
import android.content.*
import android.os.*
import android.provider.*
import android.util.*
import androidx.core.content.*
import com.example.spelltester.*
import java.io.*
import java.text.*
import java.util.*

class LocalStorage(var context: Context) {

    val deviceId: String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)


    init {
            val dateCreated = Date()
        if ( context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE) == null){
            context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE)?.let {
                with(it.edit()) {
                    putLong(KEY_CREATE_D, dateCreated.time)
                    putLong(KEY_LAST_UPLOAD, 0)
                    putInt(KEY_NOTIFY_PERMISSION, NotifyPermission.NOT_ASKED.code)
                    apply()
                }
            }
        }
        if (!context.fileList().contains(LOG_FILE_NAME)) {
            context.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE).use {
                val str = StringBuilder()

                    .append(
                        "Log file created in date ${SimpleDateFormat("yyyy/MM/dd HH:mm").format(dateCreated)}\n"
                    )
                    .append("SDK version : ${Build.VERSION.SDK_INT}\n")
                    .append("Build version : ${Build.VERSION.RELEASE}\n")
                    .append(
                        "Device ID : $deviceId\n"
                    )
                    .append("Device Model : ${Build.MODEL}\n")
                it.write((str.toString()).toByteArray())
            }
        }
    }

    fun updateLastUpload() {
        context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE)?.let {
            with(it.edit()) {
                putLong(KEY_LAST_UPLOAD, Date().time)
                apply()
            }
        }
    }

    fun getLastUpload(): Long {
        return context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE).getLong(
            KEY_LAST_UPLOAD, Date().time
        )
    }

    fun saveVersion(version: Int) {
        val sharedPref = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(KEY_VERSION, version)
            apply()
        }
    }

    fun getVersion(): Int {
        val sharedPref = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE)
        return sharedPref.getInt(KEY_VERSION, 0)
    }

    fun log(log: String, tag: String = "KH_INFO"): LocalStorage {
        context.openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND).use {
            it.write(("$tag :$log\n").toByteArray())
        }
        return this
    }

    fun logDebug(log: String, tag: String = "KH_INFO"): LocalStorage {
        Log.d(tag, log)
        return log(log, tag)
    }

    fun getLog(): String {
        return context.openFileInput(LOG_FILE_NAME).bufferedReader().readText()
    }

    fun exportLog(activity: Activity) {
        val file = File(context.filesDir, LOG_FILE_NAME)
        val uri = FileProvider.getUriForFile(context, "com.example.spelltester.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "file/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission to the receiving app
        }
        activity.startActivity(Intent.createChooser(intent, activity.getText(R.string.export_log)))
    }

    fun getNotifyPermission():NotifyPermission {
        val sharedPref = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE)
        val code = sharedPref.getInt(KEY_NOTIFY_PERMISSION, NotifyPermission.NOT_ASKED.code)
        return NotifyPermission.entries.first { it.code == code }
    }

    fun updateNotifyPermission(permission: NotifyPermission) {
        val sharedPref = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(KEY_NOTIFY_PERMISSION, permission.code)
            apply()
        }
    }


    companion object {
        private const val SHARED_FILE = "shared_file"
        private const val KEY_VERSION = "version"
        private const val KEY_CREATE_D = "create_date"
        private const val KEY_LAST_UPLOAD = "last_update"
        private const val KEY_NOTIFY_PERMISSION = "notify_permission"
        const val LOG_FILE_NAME = "log.txt"
        const val TAG = "KH_LOCAL_STORAGE"

        private var instance: LocalStorage? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: LocalStorage(context).also { instance = it }
            }

        fun getInstance(): LocalStorage {
            return instance!!
        }

        enum class NotifyPermission(val code: Int) {
            GRANTED(0), DENIED(1), NOT_ASKED(2);
        }
    }


}