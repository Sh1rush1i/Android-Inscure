package com.bangkit.inscure.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangkit.inscure.R
import com.bangkit.inscure.network.PredictionData
import com.bangkit.inscure.utils.Helper

class HistoryAdapter(
    private val context: Context,
    val predictions: List<PredictionData>,
    private val onDeleteClick: (PredictionData) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivDiseaseThumbnail: ImageView = view.findViewById(R.id.iv_disease_thumbnail)
        val tvDiseaseName: TextView = view.findViewById(R.id.tv_disease_name)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        val tvconfidence : TextView = view.findViewById(R.id.tv_confidence)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prediction = predictions[position]
        holder.tvDiseaseName.text = prediction.hasil_prediksi
        holder.tvDate.text = Helper.formatDate(prediction.tgl)
        holder.tvconfidence.text = "${prediction.confidence}%"

        // Load image with Glide
        Glide.with(context)
            .load(prediction.gambar)
            .into(holder.ivDiseaseThumbnail)

        // Handle delete button click
        holder.btnDelete.setOnClickListener {
            onDeleteClick(prediction)
        }
    }

    override fun getItemCount(): Int {
        return predictions.size
    }
}
