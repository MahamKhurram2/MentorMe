package com.mahamkhurram.i210681


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

class NetworkChangeReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val isConnected = isNetworkAvailable(context)
            Log.d("NetworkChangeReceiver", "Network status changed: isConnected = $isConnected")
            val dbHelper = DatabaseHelper(context)

            if (isConnected) {
                // If online, synchronize mentor data
                dbHelper.synchronizeMentorData(context) // Pass the context to synchronizeMentorData
            }
        }
    }


    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}