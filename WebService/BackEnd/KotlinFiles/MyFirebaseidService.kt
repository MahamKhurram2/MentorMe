package com.mahamkhurram.i210681

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
class MyFirebaseidService  : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    private fun updateToken(refreshToken: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val reference = FirebaseDatabase.getInstance().getReference("Tokens")
            val token = Token(refreshToken)
            reference.child(firebaseUser.uid).setValue(token)
        }
    }
}