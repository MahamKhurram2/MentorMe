package com.mahamkhurram.i210681

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
class video : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val cameraButton = findViewById<TextView>(R.id.photobutton)

        cameraButton.setOnClickListener {
            val intent = Intent(this, camera::class.java)
            startActivity(intent)
        }
        val backButton = findViewById<TextView>(R.id.cancel)

        backButton.setOnClickListener {
            val intent = Intent(this, chatscreen::class.java)
            startActivity(intent)
        }
    }
}