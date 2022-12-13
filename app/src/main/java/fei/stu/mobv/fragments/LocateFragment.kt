package fei.stu.mobv.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import fei.stu.mobv.GeofenceBroadcastReceiver
import fei.stu.mobv.config.authentication.PreferenceData
import fei.stu.mobv.databinding.FragmentLocateBinding
import fei.stu.mobv.helper.Injection
import fei.stu.mobv.viewModels.LocateViewModel
import fei.stu.mobv.viewModels.items.MyLocation
import fei.stu.mobv.viewModels.items.NearbyBar
import fei.stu.mobv.widgets.events.NearbyBarsEvents

class LocateFragment : Fragment() {
    private lateinit var binding: FragmentLocateBinding
    private lateinit var viewmodel: LocateViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                // Precise location access granted.
            }
            else -> {
                viewmodel.show("Background location access denied.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[LocateViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocateBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view)
                .navigate(LocateFragmentDirections.actionLocateFragmentToLoginFragment())
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewmodel
        }.also { bnd ->
            bnd.back.setOnClickListener {
                it.findNavController().popBackStack()
            }
            bnd.swiperefresh.setOnRefreshListener {
                loadData()
            }

            bnd.checkme.setOnClickListener {
                if (checkBackgroundPermissions()) {
                    viewmodel.checkMe()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissionDialog()
                    }
                }
            }
            bnd.nearbyBars.events = object : NearbyBarsEvents {
                override fun onBarClick(nearbyBar: NearbyBar) {
                    viewmodel.myBar.postValue(nearbyBar)
                }
            }
        }
        viewmodel.loading.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = it
        }
        viewmodel.checkedIn.observe(viewLifecycleOwner) { it ->
            it?.getContentIfNotHandled()?.let { it ->
                if (it) {
                    viewmodel.show("Successfully checked in.")
                    viewmodel.myLocation.value?.let {
                        createFence(it.lat, it.lon)
                    }
                }
            }
        }

        if (checkPermissions()) {
            loadData()
        } else {
            Navigation.findNavController(requireView())
                .navigate(LocateFragmentDirections.actionLocateFragmentToBarsFragment())
        }

        viewmodel.message.observe(viewLifecycleOwner) {
            if (PreferenceData.getInstance().getUserItem(requireContext()) == null) {
                Navigation.findNavController(requireView())
                    .navigate(LocateFragmentDirections.actionLocateFragmentToLoginFragment())
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun loadData() {
        if (checkPermissions()) {
            viewmodel.loading.postValue(true)
            fusedLocationClient.getCurrentLocation(
                CurrentLocationRequest.Builder().setDurationMillis(30000)
                    .setMaxUpdateAgeMillis(60000).build(), null
            ).addOnSuccessListener {
                it?.let {
                    viewmodel.myLocation.postValue(MyLocation(it.latitude, it.longitude))
                } ?: viewmodel.loading.postValue(false)
            }
        }
    }

    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    private fun createFence(lat: Double, lon: Double) {
        if (!checkPermissions()) {
            viewmodel.show("Geofence failed, permissions not granted.")
        }
        val geofenceIntent = PendingIntent.getBroadcast(
            requireContext(), 0,
            Intent(requireContext(), GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val request = GeofencingRequest.Builder().apply {
            addGeofence(
                Geofence.Builder()
                    .setRequestId("mygeofence")
                    .setCircularRegion(lat, lon, 300F)
                    .setExpirationDuration(1000L * 60 * 60 * 24)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            )
        }.build()

        geofencingClient.addGeofences(request, geofenceIntent).run {
            addOnSuccessListener {
                Navigation.findNavController(requireView())
                    .navigate(LocateFragmentDirections.actionLocateFragmentToBarsFragment())
            }
            addOnFailureListener {
                viewmodel.show("Geofence failed to create.") //permission is not granted for All times.
                it.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun permissionDialog() {
        val alertDialog: AlertDialog = requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Background location needed")
                setMessage("Allow background location (All times) for detecting when you leave bar.")
                setPositiveButton(
                    "OK"
                ) { _, _ ->
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                    )
                }
                setNegativeButton(
                    "Cancel"
                ) { _, _ ->

                }
            }
            // Create the AlertDialog
            builder.create()
        }
        alertDialog.show()
    }

    private fun checkBackgroundPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}