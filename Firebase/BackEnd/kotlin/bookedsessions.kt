package com.mahamkhurram.i210681

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
class bookedsessions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookedsessions)

        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, myprofile::class.java)
            startActivity(intent)
        }
    }
}