package com.bangkit.inscure.ui.upload

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.databinding.ActivityUploadBinding
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.network.PredictionResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myFile = intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File
        val bitmap = BitmapFactory.decodeFile(myFile.path)
        binding.storyImage.setImageBitmap(bitmap)

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

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val authHeader = "Bearer $authToken"

        RetrofitClient.instance.uploadImage(authHeader, body).enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                if (response.isSuccessful) {
                    binding.tvPrediction.text = response.body()?.data?.hasil_prediksi
                } else {
                    Toast.makeText(this@UploadActivity, "Failed to get prediction", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                Log.e("Retrofit", "Error: ${t.message}", t)
                Toast.makeText(this@UploadActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}
