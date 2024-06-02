package com.bangkit.inscure.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.R

class SplashscreenActivity : AppCompatActivity() {

    private val splashScreenDuration: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        Handler().postDelayed({
            // Start MainActivity
            val intent = Intent(this@SplashscreenActivity, MainActivity::class.java)
            startActivity(intent)
            // Close SplashActivity
            finish()
        }, splashScreenDuration)
    }

}