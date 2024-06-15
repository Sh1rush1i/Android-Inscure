package com.bangkit.inscure.ui.splashscreen

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.R
import com.bangkit.inscure.ui.auth.AuthActivity
import com.bangkit.inscure.ui.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Menunda eksekusi selama 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            // Memeriksa status login
            if (isLoggedIn) {
                // Jika sudah login, buka MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Jika belum login, buka AuthActivity
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 2000) // 2 detik
    }
}
