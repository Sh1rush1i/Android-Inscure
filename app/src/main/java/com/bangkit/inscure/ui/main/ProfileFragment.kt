package com.bangkit.inscure.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bangkit.inscure.databinding.FragmentProfileBinding
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.network.UserResponse
import com.bangkit.inscure.ui.auth.AuthActivity
import com.bangkit.inscure.ui.disease.ListHistoryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.btnLogout.setOnClickListener {
            logOut()
        }

        binding.btnCameraPermis.setOnClickListener {
            requestCameraPermission()
        }

        binding.btnAboutDev.setOnClickListener{
            navigateToWeb()
        }

        binding.btnHistory.setOnClickListener {
            navigateToHistory()
        }

        // Fetch user profile
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("authToken", null)
        authToken?.let {
            fetchUserProfile(it)
        }

        return binding.root
    }

    private fun fetchUserProfile(token: String) {
        val apiService = RetrofitClient.instance
        apiService.getUserByToken("Bearer $token").enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    user?.let {
                        binding.tvUsername.text = it.name
                        binding.tvNumber.text = it.notelp
                        binding.tvEmail.text = it.email
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch user profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToWeb(){
        val intent = Intent(requireContext(), AboutdevActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHistory(){
        val intent = Intent(requireContext(), ListHistoryActivity::class.java)
        startActivity(intent)
    }

    private fun logOut(){
        // Clear the auth token from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("authToken")
        editor.apply()

        // Navigate to AuthActivity
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            Toast.makeText(requireContext(), "Camera permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(requireContext(), "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

}
