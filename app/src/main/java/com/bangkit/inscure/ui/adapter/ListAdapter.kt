package com.bangkit.inscure.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.inscure.R
import com.bangkit.inscure.network.DiseaseResponse
import com.bangkit.inscure.ui.disease.DetailDiseaseActivity

class ListAdapter(private val diseaseList: List<DiseaseResponse>) :
    RecyclerView.Adapter<ListAdapter.DiseaseViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_disease, parent, false)
        return DiseaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        val disease = diseaseList[position]
        holder.bind(disease)

        // Set click listener
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailDiseaseActivity::class.java)
            intent.putExtra("disease_id", disease.id.toString()) // Convert ID to String
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = diseaseList.size

    inner class DiseaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val diseaseName: TextView = itemView.findViewById(R.id.tv_disease_name)

        fun bind(disease: DiseaseResponse) {
            diseaseName.text = disease.name
        }
    }
}
