package com.bangkit.inscure.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangkit.inscure.ui.splashscreen.FirstIntroFragment
import com.bangkit.inscure.ui.splashscreen.SecondIntroFragment

class IntroPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2 // Number of pages
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstIntroFragment()
            1 -> SecondIntroFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
