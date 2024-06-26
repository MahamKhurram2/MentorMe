package com.mahamkhurram.i210681

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.auth.FirebaseAuth
import android.Manifest
import android.app.Activity
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class camera : AppCompatActivity() {
    companion object {
        const val CAMERA_PERM_CODE = 101
        const val CAMERA_REQUEST_CODE = 102
    }

    private var imageUri: Uri? = null
    private lateinit var selectedImage: ImageView
    private lateinit var Fuser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var currentPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val videoButton = findViewById<TextView>(R.id.videoButton)

        videoButton.setOnClickListener {
            val intent = Intent(this, video::class.java)
            startActivity(intent)
        }
        val backButton = findViewById<TextView>(R.id.cancel)
        backButton.setOnClickListener {
            val intent = Intent(this, chatscreen::class.java)
            startActivity(intent)
        }
        val userId = intent.getStringExtra("userId")!!
        Fuser = FirebaseAuth.getInstance().currentUser!!
        if (Fuser == null) {
            // Handle the case where user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if user is not authenticated
            return
        }

        reference = FirebaseDatabase.getInstance().getReference("mentors").child(userId)





        val signupText = findViewById<ImageView>(R.id.imageView27)
        selectedImage = findViewById<ImageView>(R.id.imageView30)

        storageReference = FirebaseStorage.getInstance().reference

        signupText.setOnClickListener {
            askCameraPermission()
        }
    }
    private fun askCameraPermission() {
        Log.d("CameraPermission", "Checking camera permission...")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("CameraPermission", "Camera permission not granted, requesting permission...")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERM_CODE)
        } else {
            Log.d("CameraPermission", "Camera permission granted, dispatching picture intent...")
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CameraPermission", "Camera permission granted, dispatching picture intent...")
                dispatchTakePictureIntent()
            } else {
                Log.d("CameraPermission", "Camera permission denied.")
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
            if (imageUri != null) {  // Check if imageUri is not null
                selectedImage.setImageURI(imageUri)  // Use the stored imageUri
                Log.d("tag", "Absolute URL of image is: $imageUri")

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = imageUri
                sendBroadcast(mediaScanIntent)

                uploadImage(imageUri!!)
            } else {
                Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage(imageUri: Uri) {
        val userId = intent.getStringExtra("userId")!!
        val imageName = "pictures/${imageUri.lastPathSegment}"
        val imageRef = storageReference.child(imageName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("TAG", "onSuccess: Uploaded Image URL is ${uri.toString()}")
                    val downloadUri = uri // Use uri instead of task.result
                    // Once image is uploaded, send message with image URL
                    sendMessageWithImage(Fuser.uid, userId, downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "onFailure:", e)
            }

    }
    private fun sendMessageWithImage(sender: String, receiver: String, imageUrl: String) {
        val reference = FirebaseDatabase.getInstance().reference

        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["imageUrl"] = imageUrl

        reference.child("Chats").push().setValue(hashMap)
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = packageManager
        val activities = packageManager.queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
        Log.d("TAG", "Number of activities that can handle the camera intent: ${activities.size}")

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.e("TAG", "Error occurred while creating the file", ex)
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.mahamkhurram.i210681.fileprovider",
                    it
                )
                imageUri = photoURI  // Store the URI for later use
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        } else {
            Log.e("TAG", "No camera app found")
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }




    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }





}