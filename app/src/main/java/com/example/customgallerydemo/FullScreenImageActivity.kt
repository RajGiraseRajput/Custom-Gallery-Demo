package com.example.customgallerydemo

import android.media.MediaScannerConnection
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import java.io.File

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var deleteButton: Button
    private lateinit var adapter: FullScreenImageAdapter
    private var imageList = mutableListOf<String>()
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        viewPager = findViewById(R.id.viewPager)
        deleteButton = findViewById(R.id.deleteButton)

        imageList = intent.getStringArrayListExtra("imageList")?.toMutableList() ?: mutableListOf()
        currentPosition = intent.getIntExtra("position", 0)

        adapter = FullScreenImageAdapter(imageList)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(currentPosition, false)

        deleteButton.setOnClickListener {
            deleteCurrentImage()
        }
    }

    private fun deleteCurrentImage() {
        if (imageList.isNotEmpty()) {
            val file = File(imageList[currentPosition])
            if (file.exists() && file.delete()) {
                // Update media store to reflect deletion
                MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), null, null)
                Toast.makeText(this, "Image Deleted", Toast.LENGTH_SHORT).show()

                imageList.removeAt(currentPosition)
                adapter.notifyItemRemoved(currentPosition)

                if (imageList.isEmpty()) {
                    finish() // Close activity if no images left
                } else {
                    viewPager.setCurrentItem(currentPosition.coerceAtMost(imageList.size - 1), false)
                }
            } else {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
