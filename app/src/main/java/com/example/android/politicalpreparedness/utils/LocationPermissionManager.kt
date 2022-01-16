package com.example.android.politicalpreparedness.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber


class LocationPermissionManager(private val context: Context) {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun checkLocationPermissionIsGranted(): Boolean {
        val foregroundApproved = (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)

        val backgroundGranted = if (runningQOrLater) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        return foregroundApproved && backgroundGranted
    }

    fun requestLocationPermission(activity: Activity) {
        var permissionArray = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val resultCode = when {
            runningQOrLater -> {
                permissionArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }

    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context): Location? {
        var lastLocation : Location?
        if (GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        ) {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    lastLocation = it.result
                    Timber.d("latitude ${lastLocation?.latitude}, longitude= ${lastLocation?.longitude}")
                }
            }

        } else {
            Timber.i("Google Play Service unavailable")
        }
        return null
    }

    companion object {
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 333
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 334
    }

}