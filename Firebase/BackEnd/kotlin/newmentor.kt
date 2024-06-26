package com.mahamkhurram.i210681


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class newmentor : AppCompatActivity() {

    private lateinit var cIcon: ImageView
    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var status: EditText
    private lateinit var sessionprice: EditText
    private lateinit var uploadbutton: TextView
    private var uri: Uri? = null
    private val networkChangeReceiver = NetworkChangeReciever()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newmentor)

        cIcon = findViewById(R.id.photoupload)
        name = findViewById(R.id.nameEditText)
        description = findViewById(R.id.descriptionEditText)
        status = findViewById(R.id.statusEditText)
        uploadbutton = findViewById(R.id.uploadbutton)
        sessionprice = findViewById(R.id.sessionpriceEditText)

        cIcon.setOnClickListener {
            openGalleryForImage()
        }

        askNotificationPermission()
        uploadbutton.setOnClickListener {
            saveMentorData()
            NotificationHelper(this,"New Mentor Added").Notification()
            val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
           /* sendPushNotification("cG7JZv3xQH6bkTXAhpalfC:APA91bEBoeUu1bt1iUGfHXKyLXnY--QCBzmAWV7P6KycMfAN1Gm-sli7v1khA1rWLBr16WV1KlD03qMPU7vVWXlX-j67dnYri_3wL1A6qvxhcayEnC7c-CNrc8aAx8TZplapLciLlEIT",
                "Title:  New Mentor Added ",
                "Subtitle: ",
                "BodyMsg: New Mentor Added sucessfully",
                mapOf("key1" to "value1", "key2" to "value2"))*/
        }

    }
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkChangeReceiver)
    }
    var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),)
    { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            askNotificationPermission()
        }
    }
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                this.uri = uri
                cIcon.setImageURI(uri) // Set the selected image to ImageView
            }
        }

    private fun openGalleryForImage() {
        galleryLauncher.launch("image/*")
    }

    private fun addMentor(
        mentorName: String,
        mentorDescription: String,
        mentorStatus: String,
        mentorSessionPrice: String,
        encodedImage: String
    ) {
        val request = object : StringRequest(
            Method.POST, "http://192.168.8.102/A3/mentoradd.php",
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
                    "Failed to add mentor: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("VolleyError", "Failed to add mentor", error)
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = mentorName
                params["description"] = mentorDescription
                params["status"] = mentorStatus
                params["sessionprice"] = mentorSessionPrice
                params["encodedimage"] = encodedImage // Add encoded image here
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    // Call this method when you want to add a mentor
    private fun saveMentorData() {
        val nameText = name.text.toString().trim()
        val descText = description.text.toString().trim()
        val statusText = status.text.toString().trim()
        val sessionPrice = sessionprice.text.toString().trim()

        if (nameText.isEmpty() || descText.isEmpty() || statusText.isEmpty() || sessionPrice.isEmpty() || uri == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val encodedImage = encodeImageToBase64(uri!!)
        val dbHelper = DatabaseHelper(this)

        // Check network connectivity
        val isConnected = isNetworkAvailable()

        if (isConnected) {
            // If internet is available, save to MySQL
            addMentor(nameText, descText, statusText, sessionPrice, encodedImage)
            Log.d("Mentor", "Mentor added to MySQL")

            // Synchronize locally saved mentors with the server
            dbHelper.synchronizeMentorData(this)
        } else {
            // If internet is not available, save only locally
            val mentor = Mentor(nameText, descText, statusText, sessionPrice, encodedImage)
            val localId = dbHelper.insertOrUpdateMentor(mentor)
            if (localId != null) {
                Toast.makeText(this, "Mentor saved locally", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save mentor locally", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Use this method to encode the image to base64
    private fun encodeImageToBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val imageBytes = inputStream?.readBytes()
        inputStream?.close()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


}
