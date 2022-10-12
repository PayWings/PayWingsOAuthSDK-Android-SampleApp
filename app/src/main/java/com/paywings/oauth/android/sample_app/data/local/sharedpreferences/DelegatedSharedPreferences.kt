package com.paywings.oauth.android.sample_app.data.local.sharedpreferences

import android.content.SharedPreferences
import kotlin.reflect.KProperty

class DelegatedSharedPreferences<T>(private val sharedPreferences: SharedPreferences, private val key: String, private val defaultValue: T) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findSharedPreferences(key, defaultValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        saveSharedPreferences(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun findSharedPreferences(key: String, defaultValue: T): T {
        with(sharedPreferences)
        {
            val result: Any? = when (defaultValue) {
                is Boolean -> getBoolean(key, defaultValue)
                is Int -> getInt(key, defaultValue)
                is Long -> getLong(key, defaultValue)
                is Float -> getFloat(key, defaultValue)
                is String -> getString(key, defaultValue)
                else -> throw IllegalArgumentException()
            }
            return result as T
        }
    }

    private fun saveSharedPreferences(key: String, value: T) {
        with(sharedPreferences.edit())
        {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException()
            }.apply()
        }
    }
}