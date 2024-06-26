package com.mahamkhurram.i210681

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.mahamkhurram.i210681.databinding.ActivitySignupBinding
class signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use binding.root instead of R.layout.activity_signup
        setContentView(binding.root)
        //auth = FirebaseAuth.getInstance()

        val loginTextView: TextView = findViewById(R.id.loginbutton)
        loginTextView.setOnClickListener {
            // Start the login activity
            val intent = Intent(this, logina::class.java)
            startActivity(intent)
        }
        binding.signupbutton.setOnClickListener {
            val name: String = binding.nameEditText.text.toString()
            val email: String = binding.emailEditText.text.toString()
            val contact: String = binding.contactEditText.text.toString()
            val country: String = binding.countryEditText.text.toString()
            val city: String = binding.cityEditText.text.toString()
            val password: String = binding.passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || country.isEmpty() || city.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val queue = Volley.newRequestQueue(this)
                val url = "http://172.20.10.9/A3/signup.php"

                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Response: $response")
                        val intent = Intent(this, myprofile::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, "Registration failed: ${error.message}", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error: ${error.message}", error)
                    }) {
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = name
                        params["email"] = email
                        params["contact"] = contact
                        params["country"] = country
                        params["city"] = city
                        params["password"] = password
                        return params
                    }
                }
                queue.add(stringRequest)
            }
        }
    }
}