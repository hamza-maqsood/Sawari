package com.dscfast.sawari.extensions

import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import timber.log.Timber

fun AppCompatActivity.showLocationPrompt() {

    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

    val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

    result.addOnCompleteListener { task ->
        try {
            val response = task.getResult(ApiException::class.java)
            // All location settings are satisfied. The client can initialize location
            // requests here.
        } catch (exception: ApiException) {
            when (exception.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        // Cast to a resolvable exception.
                        val resolvable: ResolvableApiException = exception as ResolvableApiException
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        resolvable.startResolutionForResult(
                            this, LocationRequest.PRIORITY_HIGH_ACCURACY
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    } catch (e: ClassCastException) {
                        // Ignore, should be an impossible error.
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    // Location settings are not satisfied. But could be fixed by showing the
                    // user a dialog.
                    // Location settings are not satisfied. However, there's no way to fix the
                    // settings so we won't show the dialog.
                    Timber.d("Unable to change location settings")
                }
            }
        }
    }
}