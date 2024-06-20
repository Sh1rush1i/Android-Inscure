package com.bangkit.inscure.ui.splashscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.R
import com.bangkit.inscure.ui.auth.AuthActivity
import com.bangkit.inscure.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenDuration: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        setupWindow()
        logoAnimation()

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
            val authToken: String? = sharedPreferences.getString("authToken", null)

            if (isFirstTime) {
                // First time opening the app, navigate to IntroActivity
                startActivity(Intent(this@SplashScreenActivity, IntroActivity::class.java))
                sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
            } else {
                if (!authToken.isNullOrEmpty()) {
                    // Token exists, navigate to MainActivity
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                } else {
                    // No token, navigate to AuthActivity
                    startActivity(Intent(this@SplashScreenActivity, AuthActivity::class.java))
                }
            }

            finish()
        }, splashScreenDuration)
    }

    private fun logoAnimation() {
        val animationView: ImageView = findViewById(R.id.animationView)

        val rotateAnimation1 = ObjectAnimator.ofFloat(animationView, "rotation", 0f, 360f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
        }

        val rotateAnimation2 = ObjectAnimator.ofFloat(animationView, "rotation", 0f, 360f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 500
        }

        val animatorSet = AnimatorSet().apply {
            playSequentially(rotateAnimation1, rotateAnimation2)
        }

        animatorSet.start()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setupWindow() {
        if (Build.VERSION.SDK_INT >= 21) {
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= 30) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}
