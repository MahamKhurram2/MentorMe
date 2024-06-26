package com.mahamkhurram.i210681


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide

class searchresults : AppCompatActivity() {
    private lateinit var userIntent: Intent
    private lateinit var mentorName_: TextView
    private lateinit var mentorDescription_: TextView
    private lateinit var mentorStatus_: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchresults)

        // Initialize TextViews
        mentorName_ = findViewById(R.id.name)
        mentorDescription_ = findViewById(R.id.description)
        mentorStatus_ = findViewById(R.id.status)

        userIntent = intent
        val mentorId = userIntent.getIntExtra("MENTOR_ID", 1)
        if (mentorId != null) {
            Toast.makeText(this, "Mentor ID: $mentorId", Toast.LENGTH_SHORT).show()
            getMentorById(mentorId.toInt())
        } else {
            Toast.makeText(this, "Mentor ID is null", Toast.LENGTH_SHORT).show()
        }

        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, search::class.java)
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
        mentorName_.setOnClickListener {
            // Move to the review activity and pass mentor ID
            val intent = Intent(this, mentorprofile::class.java)
            intent.putExtra("MENTOR_ID", mentorId)
            startActivity(intent)
        }
    }

    private fun getMentorById(mentorId: Int) {
        val url = "http://172.20.10.9/A3/searchmentor.php?mentorId=$mentorId"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val mentorName = response.optString("name")
                val mentorDescription = response.optString("description")
                val mentorStatus = response.optString("status")
                val mentorChargesPerSession = response.optDouble("chargesPerSession")
                val pic = response.getString("photoLink")

                // Set retrieved data to TextViews
                mentorName_.text = mentorName
                mentorDescription_.text = mentorDescription
                mentorStatus_.text = mentorStatus


                val profilepic = findViewById<ImageView>(R.id.profilepic)
                // You can also use the other mentor data as needed

                if (!pic.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(pic)
                        .placeholder(R.drawable.greybox) // Optional placeholder image
                        .error(R.drawable.greybox) // Optional error image
                        .into(profilepic)
                }

            },
            { error ->
                Toast.makeText(
                    this,
                    "Error retrieving mentor information: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }
}
