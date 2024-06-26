package com.mahamkhurram.i210681

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mahamkhurram.i210681.databinding.ActivityLoginaBinding
import org.json.JSONException
import org.json.JSONObject

class logina : AppCompatActivity() {
    private val binding: ActivityLoginaBinding by lazy {
        ActivityLoginaBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val signUpTextView: TextView = findViewById(R.id.signUpTextView)
        signUpTextView.setOnClickListener {
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }

        val forgotPasswordText: TextView = findViewById(R.id.forgotPasswordText)
        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, forgotpassword::class.java)
            startActivity(intent)
        }

        binding.loginbutton.setOnClickListener {
            val email: String = binding.emailEditText.text.toString()
            val password: String = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("Login", "Attempting login with email: $email")

                val queue = Volley.newRequestQueue(this)
                val url = "http://172.20.10.9/A3/login.php"

                Log.d("Login", "Connecting to URL: $url")
                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            val status = jsonResponse.getString("status")
                            if (status == "1") {
                                Log.d("LoginStatus", "Login successful.")
                                val userId = jsonResponse.getString("user_id")
                                Log.d("UserID", "Retrieved user ID: $userId")
                                saveUserId(userId)
                                moveToProfilePage(userId)
                            } else {
                                val message = jsonResponse.getString("message")
                                Log.d("LoginStatus", "Login failed: $message")
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            Log.e("LoginError", "Error parsing response: ${e.message}", e)
                            Toast.makeText(this, "Login failed: Error parsing response", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { error ->
                        if (error is TimeoutError) {
                            // Handle timeout error separately
                            Log.e("LoginError", "Login timed out", error)
                            Toast.makeText(this, "Login timed out. Please try again.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Handle other errors
                            Log.e("LoginError", "Login failed: ${error.message}", error)
                            Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["email"] = email
                        params["password"] = password
                        return params
                    }
                }

                queue.add(stringRequest)
            }
        }
    }

    private fun saveUserId(userId: String) {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun moveToProfilePage(userId: String? = null) {
        Log.d("UserID", "Move to profile page with user ID: $userId")
        val intent = Intent(this, profile::class.java)
        intent.putExtra("userId", userId) // Pass the userId to profile activity
        startActivity(intent)
        finish()
    }

}
