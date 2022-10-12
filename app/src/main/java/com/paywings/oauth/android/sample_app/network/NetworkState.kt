package com.paywings.oauth.android.sample_app.network

import android.content.Context
import android.net.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkState @Inject constructor(@ApplicationContext private val context: Context):
    NetworkStateHolder {

    private var isNetworkStateHListenerRegistered: Boolean = false
    private var networkStateHolder: NetworkStateHolderImp = NetworkStateHolderImp()
    private val networkCallback = NetworkCallbackImp(networkStateHolder)

    override val isConnected: Boolean
        get() = networkStateHolder.isConnected
    override val network: Network?
        get() = networkStateHolder.network
    override val networkCapabilities: NetworkCapabilities?
        get() = networkStateHolder.networkCapabilities
    override val linkProperties: LinkProperties?
        get() = networkStateHolder.linkProperties

    init {
        registerNetworkStateListener()
    }

    /**
     * Unregister from listening to network changes.
     */
    fun destroy() {
        isNetworkStateHListenerRegistered = false
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).unregisterNetworkCallback(networkCallback)
    }

    /**
     * Register from listening to network changes.
     */
    fun start() {
        registerNetworkStateListener()
    }

    private fun registerNetworkStateListener() {
        if (!isNetworkStateHListenerRegistered) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Getting current status because registerNetworkCallback takes some time to get back network status.
            connectivityManager.activeNetwork?.let { activeNetwork ->
                networkStateHolder.network = activeNetwork
                connectivityManager.getNetworkCapabilities(activeNetwork)?.let {
                    networkStateHolder.networkCapabilities = it
                    networkStateHolder.isConnected =
                        it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                }
            }

            // Register to retrieve network changes.
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                networkCallback
            )
            isNetworkStateHListenerRegistered = true
        }
    }
}