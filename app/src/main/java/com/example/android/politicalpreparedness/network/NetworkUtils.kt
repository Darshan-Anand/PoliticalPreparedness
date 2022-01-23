package com.example.android.politicalpreparedness.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber


class ElectionsNetworkManager(context: Context) {

    val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private var _connectedToNetork = MutableLiveData<Boolean>()
    val connectedToNetwork: LiveData<Boolean>
        get() = _connectedToNetork

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            Timber.d("onAvailable: ${network}, $hasInternetCapability")
            if (hasInternetCapability == true) {
                _connectedToNetork.postValue(true)
            }
        }

        override fun onLost(network: Network) {
            _connectedToNetork.postValue(false)
        }
    }

    init {
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
    }

    companion object : SingletonHolder<ElectionsNetworkManager, Context>(::ElectionsNetworkManager)

}

open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T =
        instance ?: synchronized(this) {
            instance ?: constructor(arg).also { instance = it }
        }
}
