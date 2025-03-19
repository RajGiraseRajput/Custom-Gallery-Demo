package com.example.customgallerydemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImageGridActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageGridAdapter
    private var imageList: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_grid)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)


        val folderPath = intent.getStringExtra("folderPath")
        if (folderPath != null) {
            imageList = getImagesFromFolder(folderPath)
            adapter = ImageGridAdapter(imageList) { imagePath, position ->
                val intent = Intent(this, FullScreenImageActivity::class.java)
                intent.putStringArrayListExtra("imageList", ArrayList(imageList)) // Convert List to ArrayList
                intent.putExtra("position", position) // Ensure position is passed as Int
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }
    }

    private fun getImagesFromFolder(folderPath: String): List<String> {
        val images = mutableListOf<String>()
        val folder = File(folderPath)
        folder.listFiles()?.forEach {
            if (it.isFile && it.extension.lowercase() in listOf("jpg", "png", "jpeg")) {
                images.add(it.absolutePath)
            }
        }
        return images.sortedByDescending { File(it).lastModified() }
    }
}
