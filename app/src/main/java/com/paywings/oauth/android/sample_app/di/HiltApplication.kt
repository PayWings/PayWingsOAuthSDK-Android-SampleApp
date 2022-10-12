package com.paywings.oauth.android.sample_app.di

import android.app.Application
import com.paywings.oauth.android.sdk.data.enums.EnvironmentType
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PayWingsOAuthClient.init(context = applicationContext, environmentType = EnvironmentType.TEST, "97429b63-0716-460f-9702-0772e04451f3", "paywings.io")
    }
}