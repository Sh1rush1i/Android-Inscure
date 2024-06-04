package com.bangkit.inscure.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.R
import com.bangkit.inscure.ui.auth.AuthActivity

class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenDuration: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        Handler().postDelayed({
            // Start AuthActivity
            val intent = Intent(this@SplashScreenActivity, AuthActivity::class.java)
            startActivity(intent)
            // Close SplashActivity
            finish()
        }, splashScreenDuration)
    }

}