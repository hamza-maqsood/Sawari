package com.dscfast.sawari.ui.fragments.home

import android.location.Location
import com.dscfast.sawari.extensions.ioQuery
import com.dscfast.sawari.repo.keyvaluepairs.KeyValuePairsServiceContract
import com.dscfast.sawari.ui.fragments.base.viewmodel.BaseViewModel
import com.dscfast.sawari.utils.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

class HomeViewModel(
    private val keyValuePairsService: KeyValuePairsServiceContract
): BaseViewModel() {

    val homeActions: SingleLiveEvent<HomeActions> = SingleLiveEvent()

    private val _fareUnit = MutableStateFlow("0")
    val fareUnit: StateFlow<String> = _fareUnit

    private val _fareTen = MutableStateFlow("0")
    val fareTen: StateFlow<String> = _fareTen

    private val _fareHundred = MutableStateFlow("0")
    val fareHundred: StateFlow<String> = _fareHundred

    private val _fareThousand = MutableStateFlow("0")
    val fareThousand: StateFlow<String> = _fareThousand

    private val _rateHundred = MutableStateFlow("0")
    val rateHundred: StateFlow<String> = _rateHundred

    private val _rateTen = MutableStateFlow("0")
    val rateTen: StateFlow<String> = _rateTen

    private val _rateUnit = MutableStateFlow("0")
    val rateUnit: StateFlow<String> = _rateUnit

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    init {
        getNewFareValue()
    }

    fun openAppSettings() {
        homeActions.postValue(HomeActions.OpenSettings)
    }

    fun openAbout() {
        homeActions.postValue(HomeActions.OpenAbout)
    }

    fun openMaps() {
        homeActions.postValue(HomeActions.OpenMaps(lat = 33.6844, lng = 73.0479)) // islamabad coordinates
    }

    fun openMusic() {
        homeActions.postValue(HomeActions.OpenMusic)
    }

    fun updateFare(newFare: Int) {
        ioQuery {
            keyValuePairsService.updateFarePerKiloMeter(newFare = newFare)
            getNewFareValue()
        }
    }

    fun checkForButtonText() {
        ioQuery {
            if(keyValuePairsService.getHasStarted())
                homeActions.postValue(HomeActions.ChangeTextToEnd)
        }
    }

    fun startRide() {
        ioQuery {
            if (keyValuePairsService.getHasStarted()) {
                keyValuePairsService.setHasStarted(started = false)
                homeActions.postValue(HomeActions.EndRide)
            } else {
                homeActions.postValue(HomeActions.StartRide)
                _isProcessing.value = true
            }
        }
    }

    private fun getNewFareValue() {
        ioQuery {
            val farePerKm = keyValuePairsService.getFarePerKiloMeter()
            setFarePerKmValue(fare = farePerKm)
        }
    }

    private fun setFarePerKmValue(fare: Int) {
        _rateUnit.value = (fare % 10).toString()
        _rateTen.value = ((fare/10) % 10).toString()
        _rateHundred.value = ((fare/100) % 10).toString()
    }

    private fun setCurrentFarePerKmValue(fare: Int) {
        _fareUnit.value = (fare % 10).toString()
        _fareTen.value = ((fare/10) % 10).toString()
        _fareHundred.value = ((fare/100) % 10).toString()
        _fareThousand.value = (fare/10000).toString()
    }

    fun setRideStartLatestLocation(lat: Double, lng: Double) {
        ioQuery {
            keyValuePairsService.setHasStarted(started = true)
            _isProcessing.value = false
            val location = Location("").apply {
                this.latitude = lat
                this.longitude = lng
            }
            keyValuePairsService.setRideStartLocation(location = location)
        }
    }

    fun updateFareSinceLastTime(newLocation: Location) {
        ioQuery {
            val startLocation = keyValuePairsService.getRideStartLocation()
            val distanceKm = startLocation.distanceTo(newLocation) / 1000
            val perKmFare = keyValuePairsService.getFarePerKiloMeter()
            val newFare = distanceKm * perKmFare
            setCurrentFarePerKmValue(newFare.roundToInt())
        }
    }
}