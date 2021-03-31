package com.dscfast.sawari.ui.fragments.home

sealed class HomeActions {
    object OpenMusic: HomeActions()
    object OpenAbout: HomeActions()
    object OpenSettings: HomeActions()
    object StartRide: HomeActions()
    object EndRide: HomeActions()
    object ChangeTextToEnd: HomeActions()
    data class OpenMaps(val lat: Double, val lng: Double): HomeActions()
}