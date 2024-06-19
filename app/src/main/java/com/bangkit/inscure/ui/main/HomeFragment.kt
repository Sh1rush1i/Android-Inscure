package com.bangkit.inscure.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.inscure.databinding.FragmentHomeBinding
import com.bangkit.inscure.ui.adapter.CarouselAdapter
import com.bangkit.inscure.ui.camera.CameraActivity
import com.bangkit.inscure.ui.disease.ListDiseaseActivity
import com.bangkit.inscure.ui.disease.ListHistoryActivity
import com.bangkit.inscure.ui.maps.MapActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)

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
}
