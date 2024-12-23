package com.example.idiotchefassistant.result

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.idiotchefassistant.R

class ImagePagerAdapter(private val imageUrls: ArrayList<String>): RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPagerItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_pager_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = "https://topic114.bntw.dev/${imageUrls[position]}"
        holder.imageView.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.downloading_24)
            error(R.drawable.broken_image_24)
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}