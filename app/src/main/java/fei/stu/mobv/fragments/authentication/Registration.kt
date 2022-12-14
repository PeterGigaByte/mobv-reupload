package fei.stu.mobv.fragments.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import fei.stu.mobv.config.authentication.PreferenceData
import fei.stu.mobv.databinding.FragmentRegistrationBinding
import fei.stu.mobv.helper.Injection
import fei.stu.mobv.viewModels.AuthViewModel


class Registration : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isNotBlank()) {
            Navigation.findNavController(view)
                .navigate(RegistrationDirections.actionRegistrationFragmentToHomeFragment())
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = authViewModel
        }

        binding.signup.setOnClickListener {
            if (binding.username.text.toString().isNotBlank() && binding.password.text.toString().isNotBlank()
                && binding.password.text.toString().compareTo(binding.repeatPassword.text.toString())==0) {
                authViewModel.signup(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                )
            } else if (binding.username.text.toString().isBlank() || binding.password.text.toString().isBlank()){
                authViewModel.show("Fill in name and password")
            } else {
                authViewModel.show("Passwords must be same")
            }
        }

        binding.login.setOnClickListener {
            it.findNavController()
                .navigate(RegistrationDirections.actionRegistrationFragmentToLoginFragment())
        }

        authViewModel.user.observe(viewLifecycleOwner){
            it?.let {
                PreferenceData.getInstance().putUserItem(requireContext(), it)
                Navigation.findNavController(requireView())
                    .navigate(RegistrationDirections.actionRegistrationFragmentToHomeFragment())
            }
        }

    }

}