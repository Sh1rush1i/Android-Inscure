package com.bangkit.inscure.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bangkit.inscure.R
import com.bangkit.inscure.databinding.FragmentLoginBinding
import com.bangkit.inscure.network.LoginRequest
import com.bangkit.inscure.network.LoginResponse
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.ui.main.MainActivity
import com.bangkit.inscure.utils.Constanta
import com.bangkit.inscure.utils.Helper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAction.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() || password.isEmpty() -> {
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
                    loginUser(email, password)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                addSharedElement(binding.labelAuth, "auth")
                addSharedElement(binding.edLoginEmail, "email")
                addSharedElement(binding.edLoginPassword, "password")
                addSharedElement(binding.containerMisc, "misc")
                commit()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)
        RetrofitClient.instance.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.data

                    // Store the auth token in SharedPreferences
                    val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("authToken", token)
                    editor.apply()

                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Failed to login", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
