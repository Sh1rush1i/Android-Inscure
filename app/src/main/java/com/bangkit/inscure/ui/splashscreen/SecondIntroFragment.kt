package com.bangkit.inscure.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.bangkit.inscure.R
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

        val btnGetStarted = binding.btnGetStarted
        playAnimLayout()

        // Set up the button click listener
        btnGetStarted.setOnClickListener {
            val intent = Intent(activity, AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun playAnimLayout() {
        val context = requireContext()

        val slideInFadeInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right_fade_in)
        val slideInFadeInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left_in)
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        // Apply animations to views
        applyAnimation(binding.firstTextView, slideInFadeInRight)
        applyAnimation(binding.secondTextView, slideInFadeInLeft)
        applyAnimation(binding.menuAnim, fadeIn)
    }

    private fun applyAnimation(view: View, animation: Animation) {
        view.startAnimation(animation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
