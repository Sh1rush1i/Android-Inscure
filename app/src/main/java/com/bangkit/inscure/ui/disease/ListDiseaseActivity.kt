package com.bangkit.inscure.ui.disease

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListDiseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()

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
                    listAdapter = ListAdapter(diseaseList)
                    binding.recyclerDisease.adapter = listAdapter
                } else {
                    Toast.makeText(this@ListDiseaseActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DiseaseListResponse>, t: Throwable) {
                Toast.makeText(this@ListDiseaseActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateMain(){
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