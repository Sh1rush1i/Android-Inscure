package com.bangkit.inscure.ui.splashscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.bangkit.inscure.R
import com.bangkit.inscure.databinding.FragmentFirstIntroBinding

class FirstIntroFragment : Fragment() {

    private var _binding: FragmentFirstIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentFirstIntroBinding.inflate(inflater, container, false)
        playAnimLayout()
        return binding.root
    }

    private fun playAnimLayout() {
        val context = requireContext()
        val slideInFadeInUp = AnimationUtils.loadAnimation(context, R.anim.slide_in_up_fade_in)
        applyAnimation(binding.containerFirst, slideInFadeInUp)
    }

    private fun applyAnimation(view: View, animation: Animation) {
        view.startAnimation(animation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
