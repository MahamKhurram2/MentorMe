package com.mahamkhurram.i210681

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
class search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val searchIcon = findViewById<ImageView>(R.id.searchicon2)

        // Set a click listener on the search icon
        val searchButton: ImageView = findViewById(R.id.searchicon2)
        val searchBar: EditText = findViewById(R.id.searchbar)

        searchButton.setOnClickListener {
            val mentorIdText = searchBar.text.toString().trim()
            if (mentorIdText.isNotEmpty()) {
                // Convert the mentor ID string to an integer
                val mentorId = mentorIdText.toInt()
                // Start the search results activity with the mentor ID
                val intent = Intent(this, searchresults::class.java)
                intent.putExtra("MENTOR_ID", mentorId)
                startActivity(intent)
            } else {
                // Display a message indicating that the search query is empty
                Toast.makeText(this, "Please enter a mentor ID to search", Toast.LENGTH_SHORT).show()
            }
        }

        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }
        val homeIcon = findViewById<ImageView>(R.id.homeicon)
        val searchIcon22 = findViewById<ImageView>(R.id.searchicon)
        val chatIcon = findViewById<ImageView>(R.id.chaticon)
        val personIcon = findViewById<ImageView>(R.id.personicon)

        homeIcon.setOnClickListener {
            // Navigate to the home page
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        searchIcon22.setOnClickListener {
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