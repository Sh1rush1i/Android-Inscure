package com.bangkit.inscure.ui.disease

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.inscure.R
import com.bangkit.inscure.databinding.ActivityListDiseaseBinding
import com.bangkit.inscure.network.DiseaseListResponse
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.ui.adapter.ListAdapter
import com.bangkit.inscure.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListDiseaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListDiseaseBinding
    private lateinit var listAdapter: ListAdapter

    private val staticImages = listOf(
        "https://drive.google.com/uc?export=view&id=1w33SBrKnP0VhtGN4WBWKzqK4BbppO2uN",
        "https://drive.google.com/uc?export=view&id=126SMWAO500yQt3xGbCv2S5W3ht5j-VYZ",
        "https://drive.google.com/uc?export=view&id=1f-jbFWq87NoqUG5Nqe39H_OeEWcQoBIR",
        "https://drive.google.com/uc?export=view&id=1FyJvpW2R0i3L-VWrCDzzD_RkCGWw3iP3",
        "https://drive.google.com/uc?export=view&id=1kHe1t0LIUV5nHPCEFPgvlmwlvrAOEWM_",
        "https://drive.google.com/uc?export=view&id=1gHRNBSMyPxP71B0uOr-SwIfqcU1ynxhO",
        "https://drive.google.com/uc?export=view&id=1uatSO8mfqc5bCLFdmDwEDXH5T-V2hB-U",
        "https://drive.google.com/uc?export=view&id=1KHv81eaHnlwMHuwcqW3y9zPQTIjkLOwD",
        "https://drive.google.com/uc?export=view&id=1wQ7SaYm7krcCdpNF_U1Si33XumTlNYgS",
        "https://drive.google.com/uc?export=view&id=1yUd3RFTWJVkuW8cAAEYpt3vwLLNSqs29",
        "https://drive.google.com/uc?export=view&id=1Bnr-UbqEoRgeRF50m2S28Z-ZeXjLoH4n"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListDiseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        playAnimLayout()

        setSupportActionBar(binding.toolbarDisease)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize RecyclerView
        binding.recyclerDisease.layoutManager = LinearLayoutManager(this)

        // Fetch diseases from API
        fetchDiseases()

        // Handle back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateMain()
            }
        })
    }

    private fun playAnimLayout() {
        val context = this

        val slideInFadeInToolbar = AnimationUtils.loadAnimation(context, R.anim.slide_in_right_fade_in_toolbar)
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        // Apply animations to views
        applyAnimation(binding.toolbarDisease, slideInFadeInToolbar)
        applyAnimation(binding.recyclerDisease, fadeIn)
    }

    private fun applyAnimation(view: View, animation: Animation) {
        view.startAnimation(animation)
    }

    private fun fetchDiseases() {
        val apiService = RetrofitClient.instance
        apiService.getAllDiseases().enqueue(object : Callback<DiseaseListResponse> {
            override fun onResponse(
                call: Call<DiseaseListResponse>,
                response: Response<DiseaseListResponse>
            ) {
                if (response.isSuccessful) {
                    val diseaseListResponse = response.body()!!
                    val diseaseList = diseaseListResponse.data
                    listAdapter = ListAdapter(diseaseList, staticImages)
                    binding.recyclerDisease.adapter = listAdapter
                } else {
                    animPlay()
                    Toast.makeText(this@ListDiseaseActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DiseaseListResponse>, t: Throwable) {
                animPlay()
                Toast.makeText(this@ListDiseaseActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun animPlay(){
        binding.lotiieLoading.visibility = View.VISIBLE
        binding.lotiieLoading.playAnimation()
    }

    private fun animStop(){
        binding.lotiieLoading.visibility = View.INVISIBLE
        binding.lotiieLoading.cancelAnimation()
    }

    private fun navigateMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateMain()
        return true
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
}
