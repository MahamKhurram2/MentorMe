package com.mahamkhurram.i210681

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class profile : AppCompatActivity() {
    private lateinit var nametext: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var topRecyclerView: RecyclerView
    private lateinit var educationalRecyclerView: RecyclerView
    private lateinit var recentRecyclerView: RecyclerView
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
// top recyclerview
        val sharedPref = applicationContext.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        this.userId = sharedPref.getString("userId", "") ?: ""
        retrieveUserName(this.userId)
        retrieveUserName(userId)
        topRecyclerView = findViewById(R.id.topRecyclerview)
        topRecyclerView.setHasFixedSize(true)
        topRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

//educational
        educationalRecyclerView = findViewById(R.id.educationRecyclerView)
        educationalRecyclerView.setHasFixedSize(true)
        educationalRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        nametext = findViewById(R.id.nametext)



        // Retrieve top mentors
      retrieveMentorsFromWebService()


        //recent recyclerview
        /*recentRecyclerView = findViewById(R.id.recentRecyclerView)
        recentRecyclerView.setHasFixedSize(true)
        recentRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //others*/
        val homeIcon = findViewById<ImageView>(R.id.homeicon)
        val searchIcon = findViewById<ImageView>(R.id.searchicon)
        val chatIcon = findViewById<ImageView>(R.id.chaticon)
        val personIcon = findViewById<ImageView>(R.id.personicon)
        val addmentor = findViewById<ImageView>(R.id.addmentor)
        addmentor.setOnClickListener {
            val intent = Intent(this, newmentor::class.java)
            startActivity(intent)
        }
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
        val notificatinIcon = findViewById<ImageView>(R.id.notificationicon)

        // Set a click listener on the search icon
        notificatinIcon.setOnClickListener {
            // Navigate to the search activity
            val intent = Intent(this, notifications::class.java)
            startActivity(intent)
        }
       // getFCMToken()
    }

    private fun retrieveUserName(userId: String) {
        Log.d("UserID", "Retrieving user name for ID: $userId")

        if (userId.isNotEmpty()) {
            val url = "http://172.20.10.9/A3/getusername.php?id=$userId"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    Log.d("UserNameResponse", "Response: $response") // Log the response
                    try {
                        val jsonResponse = JSONObject(response)
                        val status = jsonResponse.getInt("status")
                        if (status == 1) {
                            val name = jsonResponse.getString("name")
                            Log.d("UserName", "User name: $name")
                            // Set the retrieved name to the appropriate view
                            nametext.text = name
                        } else {
                            val message = jsonResponse.getString("message")
                            Log.d("Status", "Status: $status, Message: $message")
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("JSONException", "Error parsing JSON: ${e.message}")
                        Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Log.e("VolleyError", "Error: ${error.message}")
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                })

            Volley.newRequestQueue(this).add(stringRequest)
        } else {
            Log.e("UserID", "User ID is empty")
            Toast.makeText(this, "User ID is empty", Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun retrieveMentorsFromWebService() {
        val url = "http://192.168.8.102/A3/mentor_data.php"
        val isConnected = isNetworkAvailable()

        if (isConnected) {
            // If internet is available, retrieve data from web service
            val request = StringRequest(
                Request.Method.GET, url,
                { response ->
                    // Parse JSON response
                    val jsonObject = JSONObject(response)
                    val topMentorsJsonArray = jsonObject.getJSONArray("topMentors")
                    val educationalMentorsJsonArray = jsonObject.getJSONArray("educationalMentors")

                    // Populate top mentors list
                    val topMentorsList = parseMentorsJsonArray(topMentorsJsonArray)
                    val topMentorsAdapter = MAdapter(topMentorsList, this@profile)
                    topRecyclerView.adapter = topMentorsAdapter

                    // Populate educational mentors list
                    val educationalMentorsList = parseMentorsJsonArray(educationalMentorsJsonArray)
                    val educationalMentorsAdapter = MAdapter(educationalMentorsList, this@profile)
                    educationalRecyclerView.adapter = educationalMentorsAdapter

                    // Save data to SQLite for offline access
                    saveMentorsToDatabase(topMentorsList)
                    saveMentorsToDatabase(educationalMentorsList)
                },
                { error ->
                    // Handle error
                    Log.e("VolleyError", "Failed to retrieve mentors: ${error.message}", error)
                }
            )

            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(request)
        } else {
            // If internet is not available, retrieve data from SQLite
            val topMentorsList = retrieveMentorsFromDatabase()
            val educationalMentorsList = retrieveMentorsFromDatabase()

            // Populate RecyclerViews with data from SQLite
            val topMentorsAdapter = MAdapter(topMentorsList, this@profile)
            topRecyclerView.adapter = topMentorsAdapter

            val educationalMentorsAdapter = MAdapter(educationalMentorsList, this@profile)
            educationalRecyclerView.adapter = educationalMentorsAdapter
        }
    }



    private fun parseMentorsJsonArray(jsonArray: JSONArray): ArrayList<mentorinfo> {
        val mentorsList = ArrayList<mentorinfo>()
        for (i in 0 until jsonArray.length()) {
            val mentorObject = jsonArray.getJSONObject(i)
            val name = mentorObject.getString("name")
            val description = mentorObject.getString("description")
            val sessionPrice = mentorObject.getString("sessionPrice")
            val status = mentorObject.getString("status")
            mentorsList.add(mentorinfo(name, description, sessionPrice, status))
        }
        return mentorsList
    }



    private fun saveMentorsToDatabase(mentorsList: List<mentorinfo>) {
        val dbHelper = DatabaseHelper(this)
        for (mentor in mentorsList) {
            dbHelper.insertOrUpdateMentorInfo(mentor)
        }
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    private fun retrieveMentorsFromDatabase(): ArrayList<mentorinfo> {
        val dbHelper = DatabaseHelper(this)
        return dbHelper.getAllMentors()
    }*/
    private fun retrieveMentorsFromWebService() {
        val url = "http://172.20.10.9/A3/mentor_data.php"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Parse JSON response
                val jsonObject = JSONObject(response)
                val topMentorsJsonArray = jsonObject.getJSONArray("topMentors")
                val educationalMentorsJsonArray = jsonObject.getJSONArray("educationalMentors")

                // Populate top mentors list
                val topMentorsList = parseMentorsJsonArray(topMentorsJsonArray)
                val topMentorsAdapter = MAdapter(topMentorsList, this@profile)
                topRecyclerView.adapter = topMentorsAdapter

                // Populate educational mentors list
                val educationalMentorsList = parseMentorsJsonArray(educationalMentorsJsonArray)
                val educationalMentorsAdapter = MAdapter(educationalMentorsList, this@profile)
                educationalRecyclerView.adapter = educationalMentorsAdapter
            },
            { error ->
                // Handle error
                Log.e("VolleyError", "Failed to retrieve mentors: ${error.message}", error)
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }

    private fun parseMentorsJsonArray(jsonArray: JSONArray): ArrayList<mentorinfo> {
        val mentorsList = ArrayList<mentorinfo>()
        for (i in 0 until jsonArray.length()) {
            val mentorObject = jsonArray.getJSONObject(i)
            val name = mentorObject.getString("name")
            val description = mentorObject.getString("description")
            val sessionPrice = mentorObject.getString("sessionPrice")
            val status = mentorObject.getString("status")
            mentorsList.add(mentorinfo(name, description, sessionPrice, status))
        }
        return mentorsList
    }

    private  fun getFCMToken() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCMToken", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = "Token: $token"
                Log.d("FCMToken", msg)
                // Assuming you have access to Context (e.g., within an Activity or a Fragment)
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            })
        }

}
