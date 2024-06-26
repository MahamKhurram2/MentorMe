package com.mahamkhurram.i210681

import android.content.Context
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

class mentorprofile : AppCompatActivity() {
    private lateinit var userIntent: Intent
    private lateinit var mentorName_: TextView
    private var mentorId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentorprofile)
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val reviewTag = findViewById<TextView>(R.id.bookedtag)
        mentorName_ = findViewById(R.id.name)
        userIntent = intent
        mentorId = userIntent.getIntExtra("MENTOR_ID", -1) // Get mentorId from intent

        if (mentorId != -1) { // Check if mentorId is valid
            getMentorById(mentorId)
        } else {
            Toast.makeText(this, "Mentor ID is invalid", Toast.LENGTH_SHORT).show()
        }
        // Set click listener on reviewtag TextView
        reviewTag.setOnClickListener {
            // Start the ReviewActivity
            val intent = Intent(this, review::class.java)
            intent.putExtra("MENTOR_ID", mentorId)
            startActivity(intent)
        }
        val CommunityTag = findViewById<TextView>(R.id.communitytag)

        // Set click listener on reviewtag TextView
        CommunityTag .setOnClickListener {
            // Start the ReviewActivity
            val intent = Intent(this, communitychat::class.java)
            startActivity(intent)
        }
        val bookbutton = findViewById<TextView>(R.id.updatebutton)

        // Set click listener on reviewtag TextView
        bookbutton.setOnClickListener {
            val intent = Intent(this, bookmentor::class.java)
            intent.putExtra("MENTOR_ID", mentorId)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, searchresults::class.java)
            startActivity(intent)
        }
    }
    private fun getMentorById(mentorId: Int) {
        val url = "http://172.20.10.9/A3/searchmentor.php?mentorId=$mentorId"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val mentorName = response.optString("name")
                val pic = response.getString("photoLink")

                // Set retrieved data to TextViews
                mentorName_.text = mentorName

                val profilepic = findViewById<ImageView>(R.id.profilepicture)
                // You can also use the other mentor data as needed

                if (!pic.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(pic)
                        .placeholder(R.drawable.smd10) // Optional placeholder image
                        .error(R.drawable.smd2) // Optional error image
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