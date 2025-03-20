package com.example.customgallerydemo

import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var thumbnailRecyclerView: RecyclerView
    private lateinit var adapter: FullScreenImageAdapter
    private lateinit var thumbnailAdapter: ThumbnailAdapter

    private var imageList = mutableListOf<String>()
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        viewPager = findViewById(R.id.viewPager)
        thumbnailRecyclerView = findViewById(R.id.thumbnailRecyclerView)

        imageList = intent.getStringArrayListExtra("imageList")?.toMutableList() ?: mutableListOf()
        currentPosition = intent.getIntExtra("position", 0)

        // Set up ViewPager Adapter
        adapter = FullScreenImageAdapter(imageList)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(currentPosition, false)

        // Set up Thumbnail RecyclerView
        thumbnailAdapter = ThumbnailAdapter(imageList, currentPosition) { position ->
            viewPager.setCurrentItem(position, true)
        }
        thumbnailRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        thumbnailRecyclerView.adapter = thumbnailAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                thumbnailAdapter.updateSelection(position)
                thumbnailRecyclerView.smoothScrollToPosition(position)
            }
        })

        // Delete button click
        findViewById<View>(R.id.btnDelete).setOnClickListener {
            deleteImage(imageList[currentPosition])
        }

        // Request permissions if needed
//        requestStoragePermissions()
    }

    private fun deleteImage(imagePath: String) {
        val contentResolver = contentResolver
        val uri = getImageContentUri(imagePath)

        if (uri != null) {
            try {
                contentResolver.delete(uri, null, null)
                imageList.removeAt(currentPosition)
                adapter.notifyItemRemoved(currentPosition)
                thumbnailAdapter.updateSelection(currentPosition)

                if (imageList.isEmpty()) {
                    finish()
                } else {
                    if (currentPosition >= imageList.size) {
                        currentPosition = imageList.size - 1
                    }
                    viewPager.setCurrentItem(currentPosition, false)
                    thumbnailAdapter.updateSelection(currentPosition)
                }

                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                requestDeletePermission(uri)
            }
        } else {
            Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getImageContentUri(imagePath: String): Uri? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DATA}=?"
        val selectionArgs = arrayOf(imagePath)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            cursor.close()
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        } else {
            cursor?.close()
            null
        }
    }
    private fun requestDeletePermission(uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intentSender = MediaStore.createDeleteRequest(contentResolver, listOf(uri)).intentSender
            deleteImageLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        }
    }

    private val deleteImageLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            imageList.removeAt(currentPosition)
            adapter.notifyItemRemoved(currentPosition)
            thumbnailAdapter.updateSelection(currentPosition)

            if (imageList.isEmpty()) {
                finish()
            } else {
                if (currentPosition >= imageList.size) {
                    currentPosition = imageList.size - 1
                }
                viewPager.setCurrentItem(currentPosition, false)
                thumbnailAdapter.updateSelection(currentPosition)
            }

            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Delete permission denied", Toast.LENGTH_SHORT).show()
        }
    }

}


//
//import android.media.MediaScannerConnection
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.widget.ViewPager2
//import com.bumptech.glide.Glide
//import java.io.File
//
//class FullScreenImageActivity : AppCompatActivity() {
//    private lateinit var viewPager: ViewPager2
//    private lateinit var thumbnailRecyclerView: RecyclerView
//    private lateinit var adapter: FullScreenImageAdapter
//    private lateinit var thumbnailAdapter: ThumbnailAdapter
//    private lateinit var btnDelete: Button
//
//    private var imageList = mutableListOf<String>()
//    private var currentPosition = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_full_screen_image)
//
//        viewPager = findViewById(R.id.viewPager)
//        thumbnailRecyclerView = findViewById(R.id.thumbnailRecyclerView)
//        btnDelete = findViewById(R.id.btnDelete)
//
//        imageList = intent.getStringArrayListExtra("imageList")?.toMutableList() ?: mutableListOf()
//        currentPosition = intent.getIntExtra("position", 0)
//
//        // Set up ViewPager Adapter
//        adapter = FullScreenImageAdapter(imageList)
//        viewPager.adapter = adapter
//        viewPager.setCurrentItem(currentPosition, false)
//
//        // Set up Thumbnail RecyclerView
//        thumbnailAdapter = ThumbnailAdapter(imageList, currentPosition) { position ->
//            viewPager.setCurrentItem(position, true)
//        }
//        thumbnailRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        thumbnailRecyclerView.adapter = thumbnailAdapter
//
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                currentPosition = position
//                thumbnailAdapter.updateSelection(position)
//                thumbnailRecyclerView.smoothScrollToPosition(position)
//            }
//        })
//
//        btnDelete.setOnClickListener {
//            if (imageList.isNotEmpty() && currentPosition < imageList.size) {
//                val imagePath = imageList[currentPosition]
//                deleteImage(imagePath)
//            }
//        }
//    }
//
//    private fun deleteImage(imagePath: String) {
//        val file = File(imagePath)
//        if (file.exists() && file.delete()) {
//            // Remove from lists and update UI
//            imageList.removeAt(currentPosition)
//            adapter.notifyItemRemoved(currentPosition)
//            thumbnailAdapter.notifyItemRemoved(currentPosition)
//
//            if (imageList.isEmpty()) {
//                finish() // Close activity if no images left
//            } else {
//                if (currentPosition >= imageList.size) {
//                    currentPosition = imageList.size - 1
//                }
//                viewPager.setCurrentItem(currentPosition, false)
//                thumbnailAdapter.updateSelection(currentPosition)
//            }
//
//            // Refresh Gallery
//            MediaScannerConnection.scanFile(this, arrayOf(imagePath), null, null)
//
//            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show()
//        }
//    }
//}




//
//class FullScreenImageActivity : AppCompatActivity() {
//    private lateinit var viewPager: ViewPager2
//    private lateinit var thumbnailRecyclerView: RecyclerView
//    private lateinit var adapter: FullScreenImageAdapter
//    private lateinit var thumbnailAdapter: ThumbnailAdapter
//
//    private var imageList = mutableListOf<String>()
//    private var currentPosition = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_full_screen_image)
//
//        viewPager = findViewById(R.id.viewPager)
//        thumbnailRecyclerView = findViewById(R.id.thumbnailRecyclerView)
//
//        imageList = intent.getStringArrayListExtra("imageList")?.toMutableList() ?: mutableListOf()
//        currentPosition = intent.getIntExtra("position", 0)
//
//        // Set up ViewPager Adapter
//        adapter = FullScreenImageAdapter(imageList)
//        viewPager.adapter = adapter
//        viewPager.setCurrentItem(currentPosition, false)
//
//        // Set up Thumbnail RecyclerView
//        thumbnailAdapter = ThumbnailAdapter(imageList, currentPosition) { position ->
//            viewPager.setCurrentItem(position, true)
//        }
//        thumbnailRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        thumbnailRecyclerView.adapter = thumbnailAdapter
//
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
////                thumbnailAdapter.updateSelection(position)
//                thumbnailRecyclerView.smoothScrollToPosition(position)
//            }
//        })
//
//    }
//}



//
//class FullScreenImageActivity : AppCompatActivity() {
//    private lateinit var viewPager: ViewPager2
//    private lateinit var deleteButton: Button
//    private lateinit var adapter: FullScreenImageAdapter
//    private var imageList = mutableListOf<String>()
//    private var currentPosition = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_full_screen_image)
//
//        viewPager = findViewById(R.id.viewPager)
//        deleteButton = findViewById(R.id.deleteButton)
//
//        imageList = intent.getStringArrayListExtra("imageList")?.toMutableList() ?: mutableListOf()
//        currentPosition = intent.getIntExtra("position", 0)
//
//        adapter = FullScreenImageAdapter(imageList)
//        viewPager.adapter = adapter
//        viewPager.setCurrentItem(currentPosition, false)
//
//        deleteButton.setOnClickListener {
//            deleteCurrentImage()
//        }
//    }
//
//    private fun deleteCurrentImage() {
//        if (imageList.isNotEmpty()) {
//            val file = File(imageList[currentPosition])
//            if (file.exists() && file.delete()) {
//                // Update media store to reflect deletion
//                MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), null, null)
//                Toast.makeText(this, "Image Deleted", Toast.LENGTH_SHORT).show()
//
//                imageList.removeAt(currentPosition)
//                adapter.notifyItemRemoved(currentPosition)
//
//                if (imageList.isEmpty()) {
//                    finish() // Close activity if no images left
//                } else {
//                    viewPager.setCurrentItem(currentPosition.coerceAtMost(imageList.size - 1), false)
//                }
//            } else {
//                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
