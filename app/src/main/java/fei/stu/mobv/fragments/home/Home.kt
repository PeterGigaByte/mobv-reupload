package fei.stu.mobv.fragments.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import fei.stu.mobv.config.authentication.PreferenceData
import fei.stu.mobv.databinding.FragmentHomeBinding

class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Snackbar.make(
                    binding.root.rootView,
                    "Only approximate location access granted.",
                    Snackbar.LENGTH_SHORT
                ).show()

                // Only approximate location access granted.
            }
            else -> {
                Snackbar.make(
                    binding.root.rootView,
                    "Location access denied.",
                    Snackbar.LENGTH_SHORT
                ).show()
                // No location access granted.
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view)
                .navigate(HomeDirections.actionHomeFragmentToLoginFragment())
            return
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        binding.also { bnd ->
            bnd.logout.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                Navigation.findNavController(it)
                    .navigate(HomeDirections.actionHomeFragmentToLoginFragment())
            }

            bnd.barsButton.setOnClickListener {
                if (checkPermissions()) {
                    Navigation.findNavController(it)
                        .navigate(HomeDirections.actionHomeFragmentToBarsFragment())
                }
            }

            bnd.friendsList.setOnClickListener {
                if (checkPermissions()) {
                    Navigation.findNavController(it)
                        .navigate(HomeDirections.actionHomeFragmentToFriendsFragment())
                }
            }

            bnd.friendsLocation.setOnClickListener {
                if (checkPermissions()) {
                    Navigation.findNavController(it)
                        .navigate(HomeDirections.actionHomeFragmentToFriendsLocationFragment())
                }
            }
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