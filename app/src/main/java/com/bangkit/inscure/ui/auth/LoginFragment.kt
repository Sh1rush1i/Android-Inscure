package com.bangkit.inscure.ui.auth

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
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
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        playAnimLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoomRotateAnimation(view)
        animStop()

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
                    animPlay()
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
                addSharedElement(binding.btnAction, "action")
                commit()
            }
        }
    }

    private fun playAnimLayout() {
        val context = requireContext()

        // Load animations
        val slideInFadeIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_right_fade_in)
        val slideInFadeInUp = AnimationUtils.loadAnimation(context, R.anim.slide_in_up_fade_in)
        val slideInFadeInBottom = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom_fade_in)
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        // Apply animations to views
        applyAnimation(binding.imageView, slideInFadeInUp)
        applyAnimation(binding.labelAuth, slideInFadeInBottom)
        applyAnimation(binding.labelAppname, fadeIn)
        applyAnimation(binding.edLoginEmail, slideInFadeIn)
        applyAnimation(binding.edLoginPassword, slideInFadeIn)
        applyAnimation(binding.containerMisc, slideInFadeInBottom)
        applyAnimation(binding.btnAction, slideInFadeIn)
    }

    private fun applyAnimation(view: View, animation: Animation) {
        view.startAnimation(animation)
    }

    private fun animPlay(){
        binding.lotiieLoading.visibility = View.VISIBLE
        binding.lotiieLoading.playAnimation()
    }

    private fun animStop(){
        binding.lotiieLoading.visibility = View.INVISIBLE
        binding.lotiieLoading.cancelAnimation()
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

                    Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    animStop()
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    animStop()
                    Snackbar.make(binding.root, getString(R.string.login_failed), Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                animStop()
                Snackbar.make(binding.root, "Error: ${t.message}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

