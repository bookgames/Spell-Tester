package com.example.spelltester.data.storage

import android.app.*
import android.content.*
import android.os.*
import android.provider.*
import android.util.*
import androidx.core.content.*
import java.io.*

class LocalStorage(var context: Context) {
    init {

        if (context.fileList().contains(LOG_FILE_NAME).not()) {
            context.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE).use {
                val str= StringBuilder()
                    .append("Log file created\n")
                    .append("SDK version : ${Build.VERSION.SDK_INT}\n")
                    .append("Build version : ${Build.VERSION.RELEASE}\n")
                    .append(
                        "Device ID : ${
                            Settings.Secure.getString(context.contentResolver
                                ,Settings.Secure.ANDROID_ID)}\n"
                    )
                it.write((str.toString()).toByteArray())
            }
        }
    }

    fun saveVersion(version: Int) {
        val sharedPref = context.getSharedPreferences(KEY_VERSION, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(KEY_VERSION, version)
            apply()
        }
    }

    fun getVersion(): Int {
        val sharedPref = context.getSharedPreferences(KEY_VERSION, Context.MODE_PRIVATE)
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

    fun exportLog(activity: Activity) {
        val file = File(context.filesDir, LOG_FILE_NAME)
        val uri = FileProvider.getUriForFile(context, "com.example.spelltester.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        activity.startActivity(Intent.createChooser(intent, "Share log file"))
    }


    fun printLog() {
        context.openFileInput(LOG_FILE_NAME).bufferedReader().useLines { lines ->
            lines.forEach {
                Log.d(TAG, it)
            }
        }
    }

    companion object {
        const val KEY_VERSION = "version"
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
    }
}