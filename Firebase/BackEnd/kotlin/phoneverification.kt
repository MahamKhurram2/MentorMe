package com.mahamkhurram.i210681

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.TextView
import android.widget.ImageView
class phoneverification : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phoneverification)

        val signUpTextView: TextView = findViewById(R.id.resendbutton)
        signUpTextView.setOnClickListener {

            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }
        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)


        backArrowImageView.setOnClickListener {

            val intent = Intent(this, signup::class.java) // Replace SignUpActivity::class.java with your sign-up activity
            startActivity(intent)
        }
    }
}