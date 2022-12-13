package fei.stu.mobv.fragments.friends

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import fei.stu.mobv.config.authentication.PreferenceData
import fei.stu.mobv.database.items.FriendItem
import fei.stu.mobv.databinding.FragmentFriendsBinding
import fei.stu.mobv.helper.Injection
import fei.stu.mobv.viewModels.FriendViewModel
import fei.stu.mobv.widgets.events.FriendsEvents

class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var viewModel: FriendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[FriendViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view)
                .navigate(FriendsFragmentDirections.actionFriendsFragmentToLoginFragment())
            return
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.logout.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                Navigation.findNavController(it)
                    .navigate(FriendsFragmentDirections.actionFriendsFragmentToLoginFragment())
            }

            bnd.button.setOnClickListener {
                if (checkPermissions()) {
                    viewModel.addFriend(binding.editTextTextPersonName.text.toString())
                }
            }
            bnd.swiperefresh.setOnRefreshListener {
                viewModel.refreshData()
            }
            bnd.friendsList.events = object : FriendsEvents {
                override fun deleteFriend(friend: FriendItem) {
                    viewModel.deleteFriend(friend.name)
                }
            }
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = it
        }

        viewModel.message.observe(viewLifecycleOwner) {
            if (PreferenceData.getInstance().getUserItem(requireContext()) == null) {
                Navigation.findNavController(requireView())
                    .navigate(FriendsFragmentDirections.actionFriendsFragmentToLoginFragment())
            }
        }

        viewModel.response.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.refreshData()
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