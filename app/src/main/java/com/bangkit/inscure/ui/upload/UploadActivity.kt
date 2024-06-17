package com.bangkit.inscure.ui.upload

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.databinding.ActivityUploadBinding
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.network.PredictionResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupWindow()

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val myFile = intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File
        val bitmap = BitmapFactory.decodeFile(myFile.path)
        binding.skinImage.setImageBitmap(bitmap)

        binding.btnAdd.setOnClickListener {
            uploadImage(myFile)
        }
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
                override fun onResponse(
                    call: Call<PredictionResponse>,
                    response: Response<PredictionResponse>
                ) {
                    // Hide loading animation
                    binding.lotiieLoading.visibility = View.INVISIBLE
                    binding.lotiieLoading.cancelAnimation()

                    if (response.isSuccessful) {
                        binding.tvPrediction.text = response.body()?.data?.hasil_prediksi
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
        if (Build.VERSION.SDK_INT >= 21) {
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= 30) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}
