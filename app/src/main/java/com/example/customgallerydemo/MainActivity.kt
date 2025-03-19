package com.example.customgallerydemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageFolderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            loadGallery()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 101)
        }
    }

    fun getImageFolders(context: Context): List<ImageFolder> {
        val folders = mutableMapOf<String, MutableList<String>>()
        val projection = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        )

        cursor?.use {
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            while (it.moveToNext()) {
                val imagePath = it.getString(dataColumn)
                val folderPath = File(imagePath).parentFile?.absolutePath ?: continue

                if (!folders.containsKey(folderPath)) {
                    folders[folderPath] = mutableListOf()
                }
                folders[folderPath]?.add(imagePath)
            }
        }

        cursor?.close()

        return folders.map { (folderPath, images) ->
            ImageFolder(
                folderName = File(folderPath).name,
                imageCount = images.size,
                lastModified = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(File(images[0]).lastModified()),
                firstImagePath = images[0]
            )
        }
    }
    private fun loadGallery() {
        val folders = getImageFolders(this)
        adapter = ImageFolderAdapter(folders) { folder ->
            val intent = Intent(this, ImageGridActivity::class.java)
            intent.putExtra("folderPath", folder.firstImagePath.substringBeforeLast("/"))
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadGallery()
        }
    }

}