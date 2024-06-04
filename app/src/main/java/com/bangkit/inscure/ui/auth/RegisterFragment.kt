package com.bangkit.inscure.ui.auth

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.inscure.databinding.FragmentRegisterBinding
import com.bangkit.inscure.R
import com.bangkit.inscure.utils.Constanta
import com.bangkit.inscure.utils.Helper

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            switchLogin()
        }
        binding.btnAction.setOnClickListener {
            val nama = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                email.isEmpty() or password.isEmpty() or nama.isEmpty() -> {
                    Helper.showDialogInfo(
                        requireContext(),
                        getString(R.string.empty_email_password)
                    )
                }
                !email.matches(Constanta.emailPattern) -> {
                    Helper.showDialogInfo(
                        requireContext(),
                        getString(R.string.validation_invalid_email)
                    )
                }
                password.length <= 7 -> {
                    Helper.showDialogInfo(
                        requireContext(),
                        getString(R.string.validation_password_rules)
                    )
                }
                else -> {
                    switchLogin()
                }
            }
        }
    }

    private fun switchLogin() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
            /* Shared element transition to main activity */
            addSharedElement(binding.labelAuth, "auth")
            addSharedElement(binding.edRegisterEmail, "email")
            addSharedElement(binding.edRegisterPassword, "password")
            addSharedElement(binding.containerMisc, "misc")
            commit()
        }
    }

}