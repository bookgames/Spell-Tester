package com.example.spelltester.data.storage

import android.content.*

class LocalStorage(var context: Context) {
    fun saveVersion(version: Int ){
        val sharedPref = context.getSharedPreferences(KEY_VERSION, Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt(KEY_VERSION, version)
            apply()
        }
    }
    fun getVersion(): Int {
        val sharedPref = context.getSharedPreferences(KEY_VERSION, Context.MODE_PRIVATE)
        return sharedPref.getInt(KEY_VERSION, 0)
    }

    companion object {
        const val KEY_VERSION = "version"
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