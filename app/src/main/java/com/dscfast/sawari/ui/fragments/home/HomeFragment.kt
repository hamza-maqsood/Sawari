package com.dscfast.sawari.ui.fragments.home

import android.Manifest
import android.content.*
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.*
import com.dscfast.sawari.databinding.FragmentHomeBinding
import com.dscfast.sawari.extensions.viewLifecycle
import com.dscfast.sawari.ui.fragments.base.BaseFragment
import com.dscfast.sawari.ui.fragments.base.viewmodel.kodeinViewModel
import timber.log.Timber
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dscfast.sawari.R
import com.dscfast.sawari.extensions.showLocationPrompt
import com.dscfast.sawari.extensions.toText
import com.dscfast.sawari.services.ForegroundOnlyLocationService
import com.dscfast.sawari.ui.activity.AppActivity
import com.dscfast.sawari.utils.LocationHandler
import com.dscfast.sawari.utils.toast
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions

class HomeFragment : BaseFragment(), AppActivity.OnLocationSettingsChangedListener {

    private val homeViewModel: HomeViewModel by kodeinViewModel()
    private var fragmentHomeBinding: FragmentHomeBinding by viewLifecycle()

    private var isTrackingLocation: Boolean = false

    private var foregroundOnlyLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    // Monitors connection to the while-in-use location service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppActivity.bindOnLocationSettingsChangedListener(listener = this)
    }

    override fun onStart() {
        super.onStart()
        if (isTrackingLocation) {
            val serviceIntent = Intent(requireContext(), ForegroundOnlyLocationService::class.java)
            hostActivity?.bindService(
                serviceIntent,
                foregroundOnlyServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTrackingLocation && ::foregroundOnlyBroadcastReceiver.isInitialized) {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                foregroundOnlyBroadcastReceiver,
                IntentFilter(
                    ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
                )
            )
        }
    }

    override fun onPause() {
        if (isTrackingLocation) {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
                foregroundOnlyBroadcastReceiver
            )
        }
        super.onPause()
    }

    override fun onStop() {
        if (isTrackingLocation) {
            if (foregroundOnlyLocationServiceBound) {
                hostActivity?.unbindService(foregroundOnlyServiceConnection)
                foregroundOnlyLocationServiceBound = false
            }
        }
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentHomeBinding.apply {
            lifecycleOwner = this@HomeFragment
            viewModel = homeViewModel
        }
        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeViewModel.checkForButtonText()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeHomeActions()
    }

    private fun observeHomeActions() {
        homeViewModel.homeActions.observe(viewLifecycleOwner) {
            when (it) {
                HomeActions.OpenAbout -> {
                    Timber.d("Opening About")
                    navigateTo(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
                }
                HomeActions.OpenMusic -> {
                    Timber.d("Opening Music")
                    val intent = Intent("android.intent.action.MUSIC_PLAYER")
                    startActivity(intent)
                }
                HomeActions.OpenSettings -> {
                    Timber.d("Opening App Settings")
                    showUpdateFareDialog()
                }
                HomeActions.StartRide -> {
                    Timber.d("Starting Ride")
                    // 1: check permissions
                    runWithPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) {
                        // 2: check if location enabled
                        if (LocationHandler.isLocationEnabled(requireContext())) {
                            // 3: start ride when current location is available
                            startTrackRide()
                        } else {
                            // 3: notify user to turn on location
                            hostActivity?.showLocationPrompt()
                        }
                    }
                }
                HomeActions.EndRide -> {
                    Timber.d("Ending ride")
                    endRide()
                }
                HomeActions.ChangeTextToEnd -> {
                    Timber.d("Changing Button Text to End")
                    fragmentHomeBinding.button.apply {
                        this.text = END_RIDE_BUTTON_TEXT
                    }
                }
                is HomeActions.OpenMaps -> {
                    Timber.d("Opening Maps")
                    val uri = "http://maps.google.com/maps?q=loc:${it.lat},${it.lng}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)
                }
            }
        }
    }

    private fun showUpdateFareDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext()).apply {
            this.setTitle("فی کلومیٹر کرایہ تبدیل کریں")
            val viewInflated = LayoutInflater.from(context)
                .inflate(R.layout.dialog_update_fare, view as ViewGroup?, false)
            this.setView(viewInflated)
            setPositiveButton(
                "تبدیل کرو"
            ) { _, _ ->
                val newFare = viewInflated.findViewById<EditText>(R.id.new_fare_input).text
                Timber.d("New Fare is: $newFare")
                requireContext().toast("کرایہ تبدیل کر دیا گیا ہے")
                if (newFare.isNotBlank()) {
                    homeViewModel.updateFare(newFare = newFare.toString().toInt())
                }
            }
            setNegativeButton(
                "واپس جاو"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
        builder.show()
    }

    private fun endRide() {
        isTrackingLocation = false
        foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
        requireContext().toast("کسفر ختم ہوچکا ہے")
        fragmentHomeBinding.button.apply {
            text = START_RIDE_BUTTON_TEXT
        }
    }

    private fun startTrackRide() = runWithPermissions(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) {
        isTrackingLocation = true
        // 1: wait to get current location
        LocationHandler.getCurrentKnownLocation(requireContext(), onSuccess = { lat, lng ->
            Timber.d("Started ride")
            requireContext().toast("سفر شروع کیا گیا ہے")
            // 2: start tracking
            homeViewModel.setRideStartLatestLocation(lat = lat, lng = lng)
            // 3: subscribe to location updates
            foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
            foregroundOnlyLocationService?.subscribeToLocationUpdates()

            // 4: update button text
            fragmentHomeBinding.button.apply {
                text = END_RIDE_BUTTON_TEXT
            }
        }, onFailure = {
            requireContext().toast("کسی پریشانی کی وجہ سے سفر شروع کرنے سے قاصر ہے")
        })

    }

    override fun onLocationSettingsChanged(turnedOn: Boolean) {
        if (turnedOn) {
            startTrackRide()
        } else {
            requireContext().toast("براہ کرم مقام کی ترتیبات کو آن کریں")
        }
    }

    /**
     * Receiver for location broadcasts from [ForegroundOnlyLocationService].
     */
    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                Timber.d("Foreground location: ${location.toText()}")
                homeViewModel.updateFareSinceLastTime(newLocation = location)
            } else {
                Timber.d("Received a null location!")
            }
        }
    }

    companion object {
        private const val START_RIDE_BUTTON_TEXT = "شروع"
        private const val END_RIDE_BUTTON_TEXT = "ختم"
    }
}