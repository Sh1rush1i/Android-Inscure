package com.bangkit.inscure.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.inscure.databinding.FragmentHomeBinding
import com.bangkit.inscure.ui.adapter.CarouselAdapter

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Use the binding to get the RecyclerView reference
        val recyclerView: RecyclerView = binding.recycler
        val arrayList = ArrayList<String>()

        // Add multiple images to the ArrayList
        arrayList.add("https://cdn.discordapp.com/attachments/1094703367995015189/1232664687313948752/sample_cancer_2.jpg?ex=66605573&is=665f03f3&hm=62d83e68413fe5667c0a5e98809329e265633bac991ef85d6bd78c57844a5030&")
        arrayList.add("https://cdn.discordapp.com/attachments/1094703367995015189/1232664687892496394/sample_cancer_1.jpg?ex=66605573&is=665f03f3&hm=2299764b2daaa0e6e4446919de7f9c339784b1e59ee4b4fd53a0263f03cf0c5c&")
        arrayList.add("https://cdn.discordapp.com/attachments/1094703367995015189/1232983999832653915/sample_non_cancer_1.png?ex=6660d615&is=665f8495&hm=618c37dcbb3e247177b1a34604758de06113c710d211195e22849ef86c3683a4&")
        arrayList.add("https://cdn.discordapp.com/attachments/1094703367995015189/1232664687892496394/sample_cancer_1.jpg?ex=66605573&is=665f03f3&hm=2299764b2daaa0e6e4446919de7f9c339784b1e59ee4b4fd53a0263f03cf0c5c&")
        arrayList.add("https://cdn.discordapp.com/attachments/1094703367995015189/1232983999522013184/sample_non_cancer_2.JPG?ex=6660d615&is=665f8495&hm=69995a35899f2da151ba5027bc0f7342b6bfc560ff52700d13985f0cae0b97b2&")

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
}
