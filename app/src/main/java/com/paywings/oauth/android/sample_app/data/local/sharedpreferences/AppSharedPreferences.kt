package com.paywings.oauth.android.sample_app.data.local.sharedpreferences

import android.content.SharedPreferences
import com.paywings.oauth.android.sample_app.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSharedPreferences @Inject constructor(sharedPreferences: SharedPreferences) {

    internal var refreshToken: String by DelegatedSharedPreferences(
        sharedPreferences,
        Constants.SHARED_PREFERENCES_KEY_REFRESH_TOKEN,
        ""
    )
}