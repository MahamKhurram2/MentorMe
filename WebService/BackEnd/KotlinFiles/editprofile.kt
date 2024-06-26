package com.mahamkhurram.i210681
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class editprofile : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var contact: EditText
    private lateinit var country: EditText
    private lateinit var city: EditText
    private lateinit var picture: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var encodedImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
        Log.d("editprofile", "onCreate")

        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        val dp: ImageView = findViewById(R.id.picture)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, myprofile::class.java)
            startActivity(intent)
        }
        dp.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Browse Image"), 1)
        }
        val updatebutton = findViewById<TextView>(R.id.updatebutton)
        updatebutton.setOnClickListener {
            retrieveAndUploadUserData()
        }

        name = findViewById(R.id.nameText)
        email = findViewById(R.id.emailText)
        contact = findViewById(R.id.contactText)
        country = findViewById(R.id.countryText)
        city = findViewById(R.id.cityText)
        picture = findViewById(R.id.picture)

        // Retrieve user data when activity starts
        retrieveUserData()
    }

    private fun retrieveUserData() {
        Log.d("editprofile", "retrieveUserData")

        // Retrieve the user ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        // If user ID is empty, display an error message
        if (userId.isNullOrEmpty()) {
            Log.e("editprofile", "User ID is empty")
            Toast.makeText(this, "User ID is empty", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("editprofile", "Retrieved user ID: $userId")

        // URL of the PHP file
        val url = "http://172.20.10.9/A3/getuser.php?id=$userId"

        Log.d("editprofile", "URL: $url") // Log the URL

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d("editprofile", "Raw response: $response") // Log the raw response

                try {
                    // Check if the response contains HTML error messages
                    if (response.startsWith("<")) {
                        Log.e("editprofile", "HTML error message received: $response")
                        Toast.makeText(
                            this,
                            "Server error. Please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@StringRequest
                    }

                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getInt("status")
                    if (status == 1) {
                        val userData = jsonResponse.getJSONObject("user")
                        name.setText(userData.getString("name"))
                        email.setText(userData.getString("email"))
                        contact.setText(userData.getString("contact"))
                        country.setText(userData.getString("country"))
                        city.setText(userData.getString("city"))

                        // Load and display the profile picture
                        val imageUrl = userData.getString("profile_picture")
                        if (imageUrl.isNotEmpty()) {
                            // Load image using Picasso
                            loadProfilePicture(imageUrl)
                        } else {
                            Log.e("editprofile", "Profile picture URL is empty")
                        }
                    } else {
                        val message = jsonResponse.getString("message")
                        Log.d("editprofile", "Status: $status, Message: $message")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("editprofile", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("editprofile", "Error: ${error.message}")
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun loadProfilePicture(imageUrl: String) {
        Picasso.get()
            .load(imageUrl)
            .into(picture)
    }



    private fun retrieveAndUploadUserData() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        if (userId.isNullOrEmpty()) {
            Log.e("EditProfile", "User ID is empty")
            Toast.makeText(this, "User ID is empty", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("EditProfile", "Retrieved user ID: $userId")

        val updatedName = name.text.toString().trim()
        val updatedEmail = email.text.toString().trim()
        val updatedContact = contact.text.toString().trim()
        val updatedCountry = country.text.toString().trim()
        val updatedCity = city.text.toString().trim()

        // Call updateProfile function with userId and updated fields
        updateProfile(
            userId,
            updatedName,
            updatedEmail,
            updatedContact,
            updatedCountry,
            updatedCity
        )
    }

    private fun updateProfile(
        userId: String?,
        updatedName: String,
        updatedEmail: String,
        updatedContact: String,
        updatedCountry: String,
        updatedCity: String
    ) {
        val request = object : StringRequest(
            Method.POST, "http://172.20.10.9/A3/updateuser.php?id=$userId",
            Response.Listener { response ->
                // Handle response
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, myprofile::class.java)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(
                    this,
                    "Failed to update user profile: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("VolleyError", "Failed to update user profile", error)
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = userId ?: ""
                params["name"] = updatedName
                params["email"] = updatedEmail
                params["contact"] = updatedContact
                params["country"] = updatedCountry
                params["city"] = updatedCity
                params["upload"] = encodedImage // Add encoded image here
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val filepath: Uri? = data?.data
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(filepath!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                picture.setImageBitmap(bitmap)
                encodeBitmapImage(bitmap)
            } catch (ex: Exception) {
                // Handle exception
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun encodeBitmapImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesofimage: ByteArray = byteArrayOutputStream.toByteArray()
        encodedImage = Base64.encodeToString(bytesofimage, Base64.DEFAULT)
    }
}
