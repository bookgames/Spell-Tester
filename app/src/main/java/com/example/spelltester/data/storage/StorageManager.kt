package com.example.spelltester.data.storage

import android.content.*

class StorageManager(var context: Context) {
    fun saveVersion(version: Int ){
        val sharedPref = context.getSharedPreferences("version", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt("version", version)
            apply()
        }
    }
    fun getVersion(): Int {
        val sharedPref = context.getSharedPreferences("version", Context.MODE_PRIVATE)
        return sharedPref.getInt("version", 0)
    }
    companion object{
        private var instance: StorageManager? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: StorageManager(context).also { instance = it }
            }
    }
}