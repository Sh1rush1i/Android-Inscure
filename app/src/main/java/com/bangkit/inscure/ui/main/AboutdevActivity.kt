package com.bangkit.inscure.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.databinding.ActivityAboutdevBinding

class AboutdevActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutdevBinding

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutdevBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()

        setSupportActionBar(binding.toolbarAboutdev)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize the WebView using ViewBinding
        val webView: WebView = binding.webView
        webView.settings.javaScriptEnabled = true

        // Load the URL
        webView.loadUrl("https://github.com/Sh1rush1i")

        // Handle back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    navigateToSettings()
                }
            }
        })
    }

    // Set up the window to handle status bar transparency and fullscreen layout
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

    // Intent to navigate to SettingsActivity
    private fun navigateToSettings() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateToSettings()
        return true
    }
}
