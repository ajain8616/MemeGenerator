package com.example.memegenerator

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {
    var presentImageUrl: String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val shareButton = findViewById<Button>(R.id.shareButton)
        val memeImageView=findViewById<ImageView>(R.id.memeImageView)
        shareButton.setOnClickListener {
            sharememe(memeImageView, this)
        }
        loadmeme()
    }
    private fun loadmeme() {
        val progressBar: ProgressBar =findViewById(R.id.progressBar)
        progressBar.visibility= View.VISIBLE
        val meme: ImageView =findViewById(R.id.memeImageView)
        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.com/gimme"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                presentImageUrl = response.getString("url")
                Glide.with(this).load(presentImageUrl).listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility= View.GONE
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility= View.GONE
                        return false
                    }
                }).into(meme)
            }
        ) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        }
        queue.add(jsonObjectRequest)
    }
    fun nextmeme(view: View) {
        loadmeme()
    }
    fun sharememe(view: View, context: Context) {
        val bitmap = getBitmapFromView(view)
        val imageUri = saveBitmapToGallery(bitmap, context)

        imageUri?.let { uri ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Meme"))
        }
    }
    fun getBitmapFromView(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }
    fun saveBitmapToGallery(bitmap: Bitmap, context: Context): Uri? {
        val filename = "${System.currentTimeMillis()}.jpg"
        val contentResolver = context.contentResolver

        // Save the image to the device's gallery
        val imageUri = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            filename,
            "Meme created by User"
        )

        return Uri.parse(imageUri)
    }

}
