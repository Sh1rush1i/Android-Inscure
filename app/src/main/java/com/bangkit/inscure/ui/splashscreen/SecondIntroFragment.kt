package com.bangkit.inscure.ui.splashscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.bangkit.inscure.databinding.FragmentSecondIntroBinding
import com.bangkit.inscure.ui.auth.AuthActivity

class SecondIntroFragment : Fragment() {

    private var _binding: FragmentSecondIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstTextView = binding.firstTextView
        val secondTextView = binding.secondTextView
        val btnGetStarted = binding.btnGetStarted
        val lottieAnimationView = binding.menuAnim

        // Slide in from left and fade in for the first text view
        val firstSlideIn = ObjectAnimator.ofFloat(firstTextView, "translationX", -500f, 0f)
        val firstFadeIn = ObjectAnimator.ofFloat(firstTextView, "alpha", 0f, 1f)

        // Slide in from right and fade in for the second text view
        val secondSlideIn = ObjectAnimator.ofFloat(secondTextView, "translationX", 500f, 0f)
        val secondFadeIn = ObjectAnimator.ofFloat(secondTextView, "alpha", 0f, 1f)

        // Fade in and slide up for the Lottie animation view
        val lottieSlideIn = ObjectAnimator.ofFloat(lottieAnimationView, "translationY", 500f, 0f)
        val lottieFadeIn = ObjectAnimator.ofFloat(lottieAnimationView, "alpha", 0f, 1f)

        // Combine all animations into one AnimatorSet
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(firstSlideIn, firstFadeIn, secondSlideIn, secondFadeIn, lottieSlideIn, lottieFadeIn)
        animatorSet.duration = 1000
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        // Start the animations together
        animatorSet.start()

        // Set up the button click listener
        btnGetStarted.setOnClickListener {
            val intent = Intent(activity, AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
