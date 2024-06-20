package com.bangkit.inscure.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.inscure.R
import com.bangkit.inscure.databinding.FragmentHomeBinding
import com.bangkit.inscure.network.RetrofitClient
import com.bangkit.inscure.network.UserResponse
import com.bangkit.inscure.ui.adapter.CarouselAdapter
import com.bangkit.inscure.ui.camera.CameraActivity
import com.bangkit.inscure.ui.disease.ListDiseaseActivity
import com.bangkit.inscure.ui.disease.ListHistoryActivity
import com.bangkit.inscure.ui.maps.MapActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        playAnimText()

        binding.actionDesease.setOnClickListener {
            navigateList()
        }

        binding.btnHistory.setOnClickListener {
            navigatetoHistory()
        }

        binding.btnScan.setOnClickListener{
            openCam()
        }

        binding.btnActionMaps.setOnClickListener {
            navigatetoMap()
        }

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("authToken", null)
        authToken?.let {
            fetchUserName(it)
        }

        // Use the binding to get the RecyclerView reference
        val recyclerView: RecyclerView = binding.recycler
        val arrayList = ArrayList<String>()

        // Add multiple images to the ArrayList
        arrayList.add("https://storage.googleapis.com/example-bucket-test-cc-trw/static-imgs/healthy-skin-1.webp")
        arrayList.add("https://storage.googleapis.com/example-bucket-test-cc-trw/static-imgs/Healthy-skin-2.webp")
        arrayList.add("https://storage.googleapis.com/example-bucket-test-cc-trw/static-imgs/Healthy-skin-3.jpg")
        arrayList.add("https://storage.googleapis.com/example-bucket-test-cc-trw/static-imgs/Healthy-skin-4.jpg")
        arrayList.add("https://storage.googleapis.com/example-bucket-test-cc-trw/static-imgs/Healthy-skin-5.jpeg")
        arrayList.add("https://storage.googleapis.com/example-bucket-test-cc-trw/static-imgs/Healthy-skin-6.webp")

        // Initialize the adapter with the current context and ArrayList
        val adapter = CarouselAdapter(requireContext(), arrayList)
        recyclerView.adapter = adapter

        // Set up the item click listener for the adapter
        adapter.setItemClickListener(object : CarouselAdapter.OnItemClickListener {
            override fun onClick(imageView: ImageView?, path: String?) {
                // Handle the click event, e.g., open the image in a new activity or show it in full screen
            }
        })

        // Return the root view from the binding
        return binding.root
    }

    private fun playAnimText() {
        val context = requireContext()

        val slideInFadeIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_right_fade_in)
        val slideInFadeInBottom = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom_fade_in)
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        applyAnimation(binding.tvCover, slideInFadeIn)
        applyAnimation(binding.tvCoverSub, slideInFadeIn)
        applyAnimation(binding.tvCoverSecond, slideInFadeIn)
        applyAnimation(binding.tvCoverSubSecond, slideInFadeIn)
        applyAnimation(binding.welcome, slideInFadeInBottom)
        applyAnimation(binding.tvUsername, slideInFadeInBottom)
        applyFadeInAnimation(fadeIn)
    }

    private fun applyAnimation(view: View, animation: Animation) {
        view.startAnimation(animation)
    }

    private fun applyFadeInAnimation(animation: Animation) {
        with(binding) {
            menuAnimScan.startAnimation(animation)
            btnScan.startAnimation(animation)
            menuAnim.startAnimation(animation)
            actionDesease.startAnimation(animation)
            recycler.startAnimation(animation)
            btnActionMaps.startAnimation(animation)
        }
    }

    private fun fetchUserName(token: String) {
        val apiService = RetrofitClient.instance
        apiService.getUserByToken("Bearer $token").enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    user?.let {
                        binding.tvUsername.text = it.name
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

    private fun openCam() {
        startActivity(Intent(requireContext(), CameraActivity::class.java))
    }

    private fun navigateList(){
        // Navigate to AuthActivity
        val intent = Intent(requireContext(), ListDiseaseActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigatetoHistory(){
        // Navigate to AuthActivity
        val intent = Intent(requireContext(), ListHistoryActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigatetoMap(){
        // Navigate to AuthActivity
        val intent = Intent(requireContext(), MapActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
