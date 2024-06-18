package com.bangkit.inscure.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.inscure.R
import com.bangkit.inscure.network.DiseaseResponse
import com.bangkit.inscure.ui.disease.DetailDiseaseActivity
import com.bumptech.glide.Glide

class ListAdapter(
    private val diseaseList: List<DiseaseResponse>,
    private val staticImages: List<String>
) : RecyclerView.Adapter<ListAdapter.DiseaseViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_disease, parent, false)
        return DiseaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        val disease = diseaseList[position]
        val imageUrl = staticImages.getOrNull(position) // Get image URL or null if out of bounds
        holder.bind(disease, imageUrl)

        // Set click listener
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailDiseaseActivity::class.java)
            intent.putExtra("disease_id", disease.id.toString()) // Convert ID to String
            intent.putExtra("image_url", imageUrl) // Add image URL to intent
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = diseaseList.size

    inner class DiseaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val diseaseName: TextView = itemView.findViewById(R.id.tv_disease_name)
        private val diseaseDesc: TextView = itemView.findViewById(R.id.tv_disease_desc)
        private val diseaseImage: ImageView = itemView.findViewById(R.id.iv_disease_thumbnail)

        fun bind(disease: DiseaseResponse, imageUrl: String?) {
            diseaseName.text = disease.name
            diseaseDesc.text = disease.headline
            if (imageUrl != null) {
                // Load the image using your preferred image loading library, e.g., Glide or Picasso
                Glide.with(itemView.context).load(imageUrl).into(diseaseImage)
            } else {
                // Handle case when imageUrl is null (optional)
                diseaseImage.setImageResource(R.drawable.person_24px)
            }
        }
    }
}
