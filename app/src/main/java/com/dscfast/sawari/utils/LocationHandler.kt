package com.dscfast.sawari.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import timber.log.Timber

object LocationHandler {

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentKnownLocation(
        context: Context,
        onSuccess: (lat: Double, lng: Double) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.apply {
                interval = 60_000
                fastestInterval = 5_000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        val firstLocation = locationResult.locations.firstOrNull()
                        fusedLocationClient.removeLocationUpdates(this)
                        if (firstLocation != null) {
                            Timber.d("Got Current Location as: ${firstLocation.latitude}, ${firstLocation.longitude}")
                            onSuccess.invoke(firstLocation.latitude, firstLocation.longitude)
                        } else {
                            Timber.d("Failed to get location update")
                            onFailure.invoke()
                        }
                    }
                }, Looper.getMainLooper()
            )

        } catch (ex: Exception) {
            Timber.d("Exception while getting location update: ${ex.localizedMessage}")
            onFailure.invoke()
        }
    }
}