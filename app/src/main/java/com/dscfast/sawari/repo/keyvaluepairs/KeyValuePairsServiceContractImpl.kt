package com.dscfast.sawari.repo.keyvaluepairs

import android.content.Context
import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dscfast.sawari.constants.KVPKeys
import com.dscfast.sawari.extensions.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

class KeyValuePairsServiceContractImpl(
    context: Context
) : KeyValuePairsServiceContract {

    private val userPreferencesName = "sawariUserPreferences"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = userPreferencesName)
    private val dataStore: DataStore<Preferences> by lazy {
        context.dataStore
    }

    override suspend fun updateFarePerKiloMeter(newFare: Int) {
        dataStore.setValue(KVPKeys.PER_KM_FARE.toIntPreferenceKey(), newFare)
    }

    override suspend fun getFarePerKiloMeter(): Int {
        return dataStore.getValueFlow(
            KVPKeys.PER_KM_FARE.toIntPreferenceKey(),
            65
        ).first()
    }

    override suspend fun setRideStartLocation(location: Location) {
        dataStore.setValue(KVPKeys.START_LOCATION.toStringPreferenceKey(), Gson().toJson(location))
    }

    override suspend fun getRideStartLocation(): Location {
        val json = dataStore.getValueFlow(
            KVPKeys.START_LOCATION.toStringPreferenceKey(),
            ""
        ).first()
        return Gson().fromJson(json, Location::class.java)
    }

    override suspend fun setHasStarted(started: Boolean) {
        dataStore.setValue(KVPKeys.HAS_STARTED.toBooleanPreferenceKey(), started)
    }

    override suspend fun getHasStarted(): Boolean {
        return dataStore.getValueFlow(
            KVPKeys.HAS_STARTED.toBooleanPreferenceKey(),
            false
        ).first()
    }
}