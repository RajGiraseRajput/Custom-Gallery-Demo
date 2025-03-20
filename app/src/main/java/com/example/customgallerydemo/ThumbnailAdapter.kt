package com.example.customgallerydemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ThumbnailAdapter(
    private val images: List<String>,
    private var selectedPosition: Int,
    private val onThumbnailClick: (Int) -> Unit
) : RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.thumbnailImage)
        val border: View = view.findViewById(R.id.selectedBorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imageView.context).load(images[position]).into(holder.imageView)

        // Show border for selected item, hide for others
        holder.border.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

        // Handle click event
        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition) // Update old selected item
            notifyItemChanged(selectedPosition) // Update new selected item
            onThumbnailClick(position) // Change full-screen image

        }
    }

    fun updateSelection(newPosition: Int) {
        val oldPosition = selectedPosition
        selectedPosition = newPosition
        notifyItemChanged(oldPosition)
        notifyItemChanged(newPosition)
    }

    override fun getItemCount(): Int = images.size
}


//class ThumbnailAdapter(
//    private val images: List<String>,
//    private var selectedPosition: Int,
//    private val onThumbnailClick: (Int) -> Unit
//) : RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val imageView: ImageView = view.findViewById(R.id.thumbnailImage)
//        val border: View = view.findViewById(R.id.selectedBorder)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Glide.with(holder.imageView.context).load(images[position]).into(holder.imageView)
//
//        // Show border only for selected image
//        if (position == selectedPosition) {
//            holder.border.visibility = View.VISIBLE
//        } else {
//            holder.border.visibility = View.GONE
//        }
//
//        // Handle click event
//        holder.itemView.setOnClickListener {
//            onThumbnailClick(position)
//            updateSelection(position)
//        }
//    }
//
//    override fun getItemCount(): Int = images.size
//
//    fun updateSelection(newPosition: Int) {
//        val oldPosition = selectedPosition
//        selectedPosition = newPosition
//        notifyItemChanged(oldPosition)
//        notifyItemChanged(newPosition)
//    }
//}
