package com.bangkit.inscure.ui.disease

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.inscure.databinding.ActivityListHistoryBinding
import com.bangkit.inscure.network.PredictionData
import com.bangkit.inscure.network.PredictionHistoryResponse
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.ui.adapter.HistoryAdapter
import com.bangkit.inscure.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()

        setSupportActionBar(binding.toolbarHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateMain()
            }
        })

        fetchPredictionHistory()
    }

    private fun fetchPredictionHistory() {
        val apiService = RetrofitClient.instance
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("authToken", null)

        if (authToken != null) {
            apiService.getPredictionHistory("Bearer $authToken").enqueue(object : Callback<PredictionHistoryResponse> {
                override fun onResponse(
                    call: Call<PredictionHistoryResponse>,
                    response: Response<PredictionHistoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val predictions = response.body()?.data ?: listOf()
                        setupRecyclerView(predictions)
                    } else {
                        // Handle case when response is not successful (e.g., token expired)
                        Snackbar.make(findViewById(android.R.id.content), "Failed to fetch predictions: ${response.message()}", Snackbar.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PredictionHistoryResponse>, t: Throwable) {
                    // Handle failure
                    Snackbar.make(findViewById(android.R.id.content), "Error: ${t.message}", Snackbar.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle the case where the token is null (e.g., prompt the user to log in)
            Snackbar.make(findViewById(android.R.id.content), "Authentication token is missing. Please log in.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView(predictions: List<PredictionData>) {
        val adapter = HistoryAdapter(this, predictions)
        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistory.adapter = adapter
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
