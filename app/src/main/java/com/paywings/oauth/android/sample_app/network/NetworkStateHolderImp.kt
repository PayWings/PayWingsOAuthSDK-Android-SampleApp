package com.paywings.oauth.android.sample_app.network

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities

class NetworkStateHolderImp : NetworkStateHolder {
    override var isConnected: Boolean = false
    override var network: Network? = null
    override var linkProperties: LinkProperties? = null
    override var networkCapabilities: NetworkCapabilities? = null
}