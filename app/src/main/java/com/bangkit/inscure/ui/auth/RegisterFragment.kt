package com.bangkit.inscure.ui.auth

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import com.bangkit.inscure.databinding.FragmentRegisterBinding
import com.bangkit.inscure.R
import com.bangkit.inscure.network.RegisterRequest
import com.bangkit.inscure.network.RegisterResponse
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.utils.Constanta
import com.bangkit.inscure.utils.Helper
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        zoomRotateAnimation(view)

        binding.btnLogin.setOnClickListener {
            switchLogin()
        }

        binding.btnAction.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val notelp = binding.edRegisterNotelp.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            when {
                email.isEmpty() || password.isEmpty() || name.isEmpty() || notelp.isEmpty() -> {
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
                    registerUser(name, email, notelp, password)
                }
            }
        }
    }

    private fun zoomRotateAnimation(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imageView)

        val scaleXOut = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0.5f).apply {
            duration = 500 // Duration for zoom out
            interpolator = AccelerateDecelerateInterpolator()
        }
        val scaleYOut = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.5f).apply {
            duration = 500 // Duration for zoom out
            interpolator = AccelerateDecelerateInterpolator()
        }

        val rotate = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f).apply {
            duration = 500 // Duration for fast rotation
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleXIn = ObjectAnimator.ofFloat(imageView, "scaleX", 0.5f, 1f).apply {
            duration = 500 // Duration for zoom in
            interpolator = AccelerateDecelerateInterpolator()
        }
        val scaleYIn = ObjectAnimator.ofFloat(imageView, "scaleY", 0.5f, 1f).apply {
            duration = 500 // Duration for zoom in
            interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorSet = AnimatorSet().apply {
            play(scaleXOut).with(scaleYOut) // Play zoom out animations together
            play(rotate).after(scaleXOut) // Play rotate animation after zoom out
            play(scaleXIn).with(scaleYIn).after(rotate) // Play zoom in animations after rotate
        }

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                animatorSet.start() // Restart animation to loop indefinitely
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSet.start()
    }

    private fun registerUser(name: String, email: String, notelp: String, password: String) {
        val request = RegisterRequest(name, email, notelp, password)
        RetrofitClient.instance.registerUser(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                    switchLogin()
                } else {
                    Snackbar.make(binding.root, getString(R.string.register_failed), Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Snackbar.make(binding.root, "Error: ${t.message}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun switchLogin() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
            addSharedElement(binding.labelAuth, "auth")
            addSharedElement(binding.edRegisterEmail, "email")
            addSharedElement(binding.edRegisterPassword, "password")
            addSharedElement(binding.containerMisc, "misc")
            commit()
        }
    }
}
