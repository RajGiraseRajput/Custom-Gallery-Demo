package com.example.customgallerydemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageFolderAdapter(private val folders: List<ImageFolder>, private val onClick: (ImageFolder) -> Unit) :
    RecyclerView.Adapter<ImageFolderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val folderName: TextView = view.findViewById(R.id.folderName)
        val imageCount: TextView = view.findViewById(R.id.imageCount)
        val lastModified: TextView = view.findViewById(R.id.lastModified)
        val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        holder.folderName.text = folder.folderName
        holder.imageCount.text = "${folder.imageCount} images"
        holder.lastModified.text = folder.lastModified
        Glide.with(holder.thumbnail.context).load(folder.firstImagePath).into(holder.thumbnail)

        holder.itemView.setOnClickListener { onClick(folder) }
    }

    override fun getItemCount(): Int = folders.size
}
