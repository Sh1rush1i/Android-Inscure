package com.bangkit.inscure.ui.upload

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.R
import com.bangkit.inscure.databinding.ActivityUploadBinding
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.network.PredictionResponse
import com.bangkit.inscure.ui.camera.CameraActivity
import com.bangkit.inscure.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        playAnimLayout()

        setSupportActionBar(binding.toolbarPrediction)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnHome.setOnClickListener {
            navigateMain()
        }

        // Handle back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateCam()
            }
        })

        @Suppress("DEPRECATION")
        val myFile = intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File
        val bitmap = BitmapFactory.decodeFile(myFile.path)
        binding.skinImage.setImageBitmap(bitmap)

        binding.btnAdd.setOnClickListener {
            binding.tvPrediction.text = " "
            uploadImage(myFile)
        }
    }

    private fun playAnimLayout() {
        val context = this

        val slideInFadeInTop = AnimationUtils.loadAnimation(context, R.anim.slide_in_up_fade_in)
        val slideInFadeInBottom = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom_fade_in)
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        applyAnimation(binding.toolbarPrediction, slideInFadeInTop)
        applyAnimation(binding.containerSkinImage, fadeIn)
        applyAnimation(binding.predictionTitle, slideInFadeInBottom)
        applyAnimation(binding.tvPrediction, slideInFadeInBottom)
    }

    private fun applyAnimation(view: View, animation: Animation) {
        view.startAnimation(animation)
    }

    private fun navigateMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateCam() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateCam()
        return true
    }

    private fun uploadImage(file: File) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("authToken", null)

        if (authToken.isNullOrEmpty()) {
            Toast.makeText(this, "Auth token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val authHeader = "Bearer $authToken"

        // Show loading animation
        binding.lotiieLoading.visibility = View.VISIBLE
        binding.lotiieLoading.playAnimation()

        RetrofitClient.instance.uploadImage(authHeader, body)
            .enqueue(object : Callback<PredictionResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<PredictionResponse>,
                    response: Response<PredictionResponse>
                ) {
                    // Hide loading animation
                    binding.lotiieLoading.visibility = View.INVISIBLE
                    binding.lotiieLoading.cancelAnimation()

                    if (response.isSuccessful) {
                        binding.tvPrediction.text = response.body()?.data?.hasil_prediksi
                        binding.tvConfidence.text = "${response.body()?.data?.confidence.toString()}%"
                    } else {
                        Toast.makeText(
                            this@UploadActivity,
                            "Failed to get prediction",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                    // Hide loading animation
                    binding.lotiieLoading.visibility = View.INVISIBLE
                    binding.lotiieLoading.cancelAnimation()

                    Log.e("Retrofit", "Error: ${t.message}", t)
                    Toast.makeText(this@UploadActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setupWindow() {
        // For SDK version 24 (Nougat) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }

        // For SDK version 30 (Android 11) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            // For SDK versions below 30
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}

