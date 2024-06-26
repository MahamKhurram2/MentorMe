package com.mahamkhurram.i210681

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide

class review : AppCompatActivity() {
    private lateinit var userIntent: Intent
    private lateinit var mentorName_: TextView
    private var mentorId: Int = -1 // Initialize mentorId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        mentorName_ = findViewById(R.id.name)
        userIntent = intent
        mentorId = userIntent.getIntExtra("MENTOR_ID", -1) // Get mentorId from intent

        if (mentorId != -1) { // Check if mentorId is valid
            getMentorById(mentorId)
        } else {
            Toast.makeText(this, "Mentor ID is invalid", Toast.LENGTH_SHORT).show()
        }
        // Find the feedbackbutton by its ID
        val feedbackButton = findViewById<TextView>(R.id.updatebutton)

        // Set OnClickListener for the feedbackbutton
        feedbackButton.setOnClickListener {
            // Create an Intent to navigate to the Add Mentor activity
            addReview(mentorId, findViewById<TextView>(R.id.reviewbox).text.toString())
            NotificationHelper(this,"Mentor Reviewed").Notification()
            val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, mentorprofile::class.java)
            startActivity(intent)
        }
    }
    private fun addReview(mentorId: Int, review: String) {
        val request = object : StringRequest(
            Method.POST, "http://172.20.10.9/A3/addreview.php",
            Response.Listener { response ->
                // Handle response
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                // Optionally, you can navigate to another activity after adding the review
                finish() // Go back to previous activity
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(this, "Error adding review: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["mentorId"] = mentorId.toString() // Use "mentorId" instead of "id"
                params["review"] = review
                return params
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
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
