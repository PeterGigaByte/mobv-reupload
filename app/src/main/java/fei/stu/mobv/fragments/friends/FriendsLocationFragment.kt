package fei.stu.mobv.fragments.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import fei.stu.mobv.config.authentication.PreferenceData
import fei.stu.mobv.databinding.FragmentFriendsLocationBinding
import fei.stu.mobv.helper.Injection
import fei.stu.mobv.viewModels.FriendLocationViewModel

class FriendsLocationFragment : Fragment() {
    private lateinit var binding: FragmentFriendsLocationBinding
    private lateinit var viewModel: FriendLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[FriendLocationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendsLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view)
                .navigate(FriendsLocationFragmentDirections.actionFriendsLocationFragmentToLoginFragment())
            return
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.logout.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                Navigation.findNavController(it)
                    .navigate(FriendsLocationFragmentDirections.actionFriendsLocationFragmentToLoginFragment())
            }

            bnd.swiperefresh.setOnRefreshListener {
                viewModel.refreshData()
            }
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = it
        }

        viewModel.message.observe(viewLifecycleOwner) {
            if (PreferenceData.getInstance().getUserItem(requireContext()) == null) {
                Navigation.findNavController(requireView())
                    .navigate(FriendsLocationFragmentDirections.actionFriendsLocationFragmentToLoginFragment())
            }
        }

        viewModel.response.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.refreshData()
            }
        }
    }

}