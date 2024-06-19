package com.bangkit.inscure.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bangkit.inscure.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.bumptech.glide.Glide

class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val infoView = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)
        val title = infoView.findViewById<TextView>(R.id.title)
        val snippet = infoView.findViewById<TextView>(R.id.snippet)
        val imageView = infoView.findViewById<ImageView>(R.id.imageView)

        title.text = marker.title
        snippet.text = marker.snippet

        val imageUrl = marker.tag as String
        Glide.with(context).load(imageUrl).into(imageView)

        return infoView
    }
}
