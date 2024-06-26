package com.mahamkhurram.i210681

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
class communitychat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communitychat)
        val cameraicon = findViewById<ImageView>(R.id.cameraicon)

// Set a click listener on the chat icon
        cameraicon.setOnClickListener {
            // Create an Intent to start the camera activity
            val intent = Intent(this, camera::class.java)
            // Start the camera activity
            startActivity(intent)
        }

        val phoneIcon = findViewById<ImageView>(R.id.phoneicon)

        // Set a click listener on the phone icon
        phoneIcon.setOnClickListener {
            // Create an Intent to start the voice call activity
            val intent = Intent(this, voicecall::class.java)
            // Start the voice call activity
            startActivity(intent)
        }

        // Find the ImageView for the video call icon
        val videoCallIcon = findViewById<ImageView>(R.id.videocallicon)

        // Set a click listener on the video call icon
        videoCallIcon.setOnClickListener {
            // Create an Intent to start the video call activity
            val intent = Intent(this, videocall::class.java)
            // Start the video call activity
            startActivity(intent)
        }
        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }
        val homeIcon = findViewById<ImageView>(R.id.homeicon)
        val searchIcon = findViewById<ImageView>(R.id.searchicon)
        val chatIcon = findViewById<ImageView>(R.id.chaticon)
        val personIcon = findViewById<ImageView>(R.id.personicon)

        homeIcon.setOnClickListener {
            // Navigate to the home page
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        searchIcon.setOnClickListener {
            // Navigate to the search page
            val intent = Intent(this, search::class.java)
            startActivity(intent)
        }

        chatIcon.setOnClickListener {
            // Navigate to the chat page
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        personIcon.setOnClickListener {
            // Navigate to the profile page
            val intent = Intent(this, myprofile::class.java)
            startActivity(intent)
        }
    }
}