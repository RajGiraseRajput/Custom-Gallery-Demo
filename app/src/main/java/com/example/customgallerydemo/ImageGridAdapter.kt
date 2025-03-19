package com.example.customgallerydemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageGridAdapter(private val images: List<String>, private val onClick: (String, Int) -> Unit) :
    RecyclerView.Adapter<ImageGridAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imageView.context).load(images[position]).into(holder.imageView)
        holder.itemView.setOnClickListener { onClick(images[position], position) } // Pass position correctly
    }

    override fun getItemCount(): Int = images.size
}
