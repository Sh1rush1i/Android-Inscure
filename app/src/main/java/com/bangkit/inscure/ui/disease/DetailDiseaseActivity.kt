package com.bangkit.inscure.ui.disease

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.inscure.R
import com.bangkit.inscure.databinding.ActivityDetailDiseaseBinding
import com.bangkit.inscure.network.DiseaseResponse
import com.bangkit.inscure.network.DiseaseResponseWrapper
import com.bangkit.inscure.network.RetrofitClient
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailDiseaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDiseaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailDiseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()

        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle back press
        binding.toolbarDetail.setNavigationOnClickListener {
            navigateToList()
        }

        // Get disease ID and image URL from intent
        val diseaseId = intent.getStringExtra("disease_id")
        val imageUrl = intent.getStringExtra("image_url")
        Log.d(TAG, "Received disease ID: $diseaseId, image URL: $imageUrl")

        // Fetch disease detail from API
        diseaseId?.let { fetchDiseaseDetail(it) }

        // Display image
        imageUrl?.let { loadImage(it) }
    }

    private fun fetchDiseaseDetail(diseaseId: String) {
        val apiService = RetrofitClient.instance
        apiService.getDiseaseById(diseaseId).enqueue(object : Callback<DiseaseResponseWrapper> {
            override fun onResponse(call: Call<DiseaseResponseWrapper>, response: Response<DiseaseResponseWrapper>) {
                if (response.isSuccessful) {
                    val disease = response.body()?.data
                    Log.d(TAG, "Disease fetched: $disease")
                    disease?.let {
                        displayDiseaseDetail(it)
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e(TAG, "Failed to fetch disease detail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DiseaseResponseWrapper>, t: Throwable) {
                // Handle failure, show error message or retry logic
                Log.e(TAG, "Error fetching disease detail", t)
            }
        })
    }

    private fun displayDiseaseDetail(disease: DiseaseResponse) {
        Log.d(TAG, "Displaying disease details: $disease")
        runOnUiThread {
            binding.tvDetailDiseaseTitle.text = disease.name
            binding.tvDetailDisease.text = disease.description
        }
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.person_24px) // Placeholder image
            .error(R.drawable.person_24px) // Error image
            .into(binding.ivImageDisease)
    }

    private fun navigateToList() {
        finish()
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
