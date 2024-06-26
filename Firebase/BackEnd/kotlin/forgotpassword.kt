package com.mahamkhurram.i210681

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
class forgotpassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)


        val sendButton: TextView = findViewById(R.id.resendbutton)
        sendButton.setOnClickListener {
            val intent = Intent(this, resetpassword::class.java)
            startActivity(intent)
        }
        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, logina::class.java)
            startActivity(intent)
        }
        val loginText: TextView = findViewById(R.id.loginclick)
        loginText.setOnClickListener {
            val intent = Intent(this, logina::class.java)
            startActivity(intent)
        }
    }
}