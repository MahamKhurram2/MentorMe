package com.mahamkhurram.i210681

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import org.json.JSONException

class bookmentor : AppCompatActivity() {
    private lateinit var userIntent: Intent
    private lateinit var mentorName_: TextView
    private lateinit var sessionprice: TextView
    private var mentorId: Int = -1
    private lateinit var userId: String
    private var selectedTimeSlot: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmentor)
        mentorName_ = findViewById(R.id.name)
        sessionprice=findViewById(R.id.sessionprice)
        userIntent = intent
        mentorId = userIntent.getIntExtra("MENTOR_ID", -1) // Get mentorId from intent

        if (mentorId != -1) { // Check if mentorId is valid
            getMentorById(mentorId)
        } else {
            Toast.makeText(this, "Mentor ID is invalid", Toast.LENGTH_SHORT).show()
        }
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "") ?: ""
        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, mentorprofile::class.java)
            startActivity(intent)
        }

        val bookButton = findViewById<TextView>(R.id.bookbutton)
        bookButton.setOnClickListener {
            if (selectedTimeSlot.isNotEmpty()) {
                bookAppointment(mentorId, selectedTimeSlot)
            } else {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
            }
            NotificationHelper(this,"Mentor Booked Sucessfully!").Notification()
            val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }

        val timeSlot1 = findViewById<TextView>(R.id.timeslot1)
        val timeSlot2 = findViewById<TextView>(R.id.timeslot2)
        val timeSlot3 = findViewById<TextView>(R.id.timeslot3)

        timeSlot1.setOnClickListener {
            selectTimeSlot("10.00 AM")
        }

        timeSlot2.setOnClickListener {
            selectTimeSlot("11.00 AM")
        }

        timeSlot3.setOnClickListener {
            selectTimeSlot("12.00 PM")
        }
    }

    private fun selectTimeSlot(timeSlot: String) {
        // Store the selected time slot
        selectedTimeSlot = timeSlot

        // Reset other time slots
        findViewById<TextView>(R.id.timeslot1).setBackgroundResource(if (timeSlot == "10.00 AM") R.drawable.ts2 else 0)
        findViewById<TextView>(R.id.timeslot2).setBackgroundResource(if (timeSlot == "11.00 AM") R.drawable.ts2 else 0)
        findViewById<TextView>(R.id.timeslot3).setBackgroundResource(if (timeSlot == "12.00 PM") R.drawable.ts2 else 0)
    }

    private fun bookAppointment(mentorId: Int, timeSlot: String) {
        val url = "http://172.20.10.9/A3/book_appointment.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val message = response
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (message == "Appointment booked successfully") {
                        sendPushNotification("Booking Confirmation", "Your mentor has been successfully booked.")
                    }
                } catch (e: JSONException) {
                    Log.e("Booking", "Error parsing response: ${e.message}")
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("Booking", "Error: ${error.message}")
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["mentorId"] = mentorId.toString()
                params["userId"] = userId
                params["timeSlot"] = timeSlot
                return params
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }




    private fun getMentorById(mentorId: Int) {
        val url = "http://192.168.8.101/A3/searchmentor.php?mentorId=$mentorId"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val mentorName = response.optString("name")
                val sessionprice_ = response.optString("chargesPerSession")
                val pic = response.getString("photoLink")

                // Set retrieved data to TextViews
                mentorName_.text = mentorName
                sessionprice.text = sessionprice_
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

    private fun sendPushNotification(title: String, body: String) {
        // Get an instance of the FCMNotificationService
        val notificationService = FCMNotificationService()

        // Call the method to send the push notification
        notificationService.sendPushNotification(title, body)
    }
}
