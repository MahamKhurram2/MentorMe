package com.mahamkhurram.i210681;
import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class PersistenceHandler : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }