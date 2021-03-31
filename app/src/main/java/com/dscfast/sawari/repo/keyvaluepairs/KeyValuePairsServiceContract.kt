package com.dscfast.sawari.repo.keyvaluepairs

import android.location.Location

interface KeyValuePairsServiceContract {

    suspend fun updateFarePerKiloMeter(newFare: Int)
    suspend fun getFarePerKiloMeter(): Int

    suspend fun setRideStartLocation(location: Location)
    suspend fun getRideStartLocation(): Location

    suspend fun setHasStarted(started: Boolean)
    suspend fun getHasStarted(): Boolean
}