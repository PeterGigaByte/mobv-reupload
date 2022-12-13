package fei.stu.mobv.fragments.bars

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import fei.stu.mobv.R
import fei.stu.mobv.config.authentication.PreferenceData
import fei.stu.mobv.databinding.FragmentBarsBinding
import fei.stu.mobv.helper.Injection
import fei.stu.mobv.viewModels.BarsViewModel
import fei.stu.mobv.viewModels.items.MyLocation

class BarsFragment : Fragment() {
    private lateinit var binding: FragmentBarsBinding
    private lateinit var viewmodel: BarsViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Navigation.findNavController(requireView())
                    .navigate(BarsFragmentDirections.actionBarsFragmentToLocateFragment())
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewmodel.show("Only approximate location access granted.")
                // Only approximate location access granted.
            }
            else -> {
                viewmodel.show("Location access denied.")
                // No location access granted.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[BarsViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.sorting_descending_people -> viewmodel.getBarsByUsersCountDesc()
                    R.id.sorting_ascending_people -> viewmodel.getBarsByUsersCountAsc()
                    R.id.sorting_descending_name -> viewmodel.sortBarsByNameDesc()
                    R.id.sorting_ascending_name -> viewmodel.sortBarsByNameAsc()
                    R.id.sorting_descending_distance -> viewmodel.sortByDistanceDesc()
                    R.id.sorting_ascending_distance -> viewmodel.sortByDistanceAsc()
                }
                viewmodel.show(getString(R.string.selected_item) + " " + menuItem.title)
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view)
                .navigate(BarsFragmentDirections.actionBarsFragmentToLoginFragment())
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewmodel
        }.also { bnd ->
            bnd.logout.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                Navigation.findNavController(it)
                    .navigate(BarsFragmentDirections.actionBarsFragmentToLoginFragment())
            }

            bnd.swiperefresh.setOnRefreshListener {
                viewmodel.refreshData()
                loadData()
            }

            bnd.findBar.setOnClickListener {
                if (checkPermissions()) {
                    it.findNavController()
                        .navigate(BarsFragmentDirections.actionBarsFragmentToLocateFragment())
                } else {
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            bnd.actionToHome.setOnClickListener {
                if (checkPermissions()) {
                    it.findNavController()
                        .navigate(BarsFragmentDirections.actionBarsFragmentToHomeFragment())
                }
            }
            if (checkPermissions()) {
                loadData()
            } else {
                Navigation.findNavController(requireView())
                    .navigate(BarsFragmentDirections.actionBarsFragmentSelf())
            }
        }

        viewmodel.loading.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = it
        }

        viewmodel.message.observe(viewLifecycleOwner) {
            if (PreferenceData.getInstance().getUserItem(requireContext()) == null) {
                Navigation.findNavController(requireView())
                    .navigate(BarsFragmentDirections.actionBarsFragmentToLoginFragment())
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