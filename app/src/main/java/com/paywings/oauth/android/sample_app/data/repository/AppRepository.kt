package com.paywings.oauth.android.sample_app.data.repository

import com.paywings.oauth.android.sample_app.data.local.sharedpreferences.AppSharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val appSharedPreferences: AppSharedPreferences
) {

    fun setRefreshToken(refreshToken: String) {
        appSharedPreferences.refreshToken = refreshToken
    }

    val refreshToken: String
        get() {
            return appSharedPreferences.refreshToken
        }
}