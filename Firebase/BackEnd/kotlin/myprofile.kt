package com.mahamkhurram.i210681

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class myprofile : AppCompatActivity() {
    private lateinit var nameetext: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private var imageURL: String? = null
    private var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)
        nameetext = findViewById(R.id.name_text)
        val dpicon = findViewById<ImageView>(R.id.dp)
        dpicon.setOnClickListener {
            openGalleryForImage()
        }
       // retrieveProfilePicture()
        val editprofileicon = findViewById<ImageView>(R.id.editprofileicon)

        // Set a click listener on the edit profile icon
        editprofileicon.setOnClickListener {
            // Navigate to the EditProfile activity
            val intent = Intent(this, editprofile::class.java)
            startActivity(intent)
        }

        val bookbutton = findViewById<TextView>(R.id.bookedtag)

        // Set a click listener on the edit profile icon
        bookbutton.setOnClickListener {
            // Navigate to the EditProfile activity
            val intent = Intent(this, bookedsessions::class.java)
            startActivity(intent)
        }
        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }
        val homeIcon = findViewById<ImageView>(R.id.homeicon)
        val searchIcon = findViewById<ImageView>(R.id.searchicon)
        val chatIcon = findViewById<ImageView>(R.id.chaticon)
        val personIcon = findViewById<ImageView>(R.id.personicon)

        homeIcon.setOnClickListener {
            // Navigate to the home page
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        searchIcon.setOnClickListener {
            // Navigate to the search page
            val intent = Intent(this, search::class.java)
            startActivity(intent)
        }

        chatIcon.setOnClickListener {
            // Navigate to the chat page
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        personIcon.setOnClickListener {
            // Navigate to the profile page
            val intent = Intent(this, myprofile::class.java)
            startActivity(intent)
        }
        // retrieveUserName()
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // Store the selected image URI
                this.uri = uri
                // Automatically save the profile after selecting the image
                saveProfileInformation()
            }
        }

    private fun openGalleryForImage() {
        // Launch the gallery to select an image
        galleryLauncher.launch("image/*")
    }

    private fun saveProfileInformation() {
        // Check if an image is selected
        if (uri != null) {
            // Upload the selected image to Firebase Storage
            uploadImageToFirebaseStorage()
        } else {
            // Show error message if no image is selected
            Toast.makeText(this@myprofile, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // Create a reference to the Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference
        // Create a reference to the "profile_pictures" folder and generate a unique file name
        val fileRef =
            storageReference.child("profile_pictures").child("${System.currentTimeMillis()}.jpg")

        // Upload the image file to Firebase Storage
        fileRef.putFile(uri!!)
            .addOnSuccessListener { taskSnapshot ->
                // If upload is successful, get the download URL of the image
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Store the download URL of the image
                    imageURL = downloadUri.toString()
                    // Save the image URL to the database
                    saveProfilePictureToDatabase(imageURL!!)
                }
            }
            .addOnFailureListener { e ->
                // Failed to upload image
                Toast.makeText(
                    this@myprofile,
                    "Failed to upload image: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("UPLOAD_ERROR", "Failed to upload image", e)
            }
    }

    private fun saveProfilePictureToDatabase(imageURL: String) {
        // Get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if the user is logged in
        currentUser?.uid?.let { uid ->
            // Create a reference to the "users" node in the Firebase Realtime Database
            val databaseReference = FirebaseDatabase.getInstance().reference.child("users")

            // Create a reference to the current user's node
            val userRef = databaseReference.child(uid)

            // Update the "profile_picture" field with the provided image URL
            userRef.child("profile_picture").setValue(imageURL)
                .addOnSuccessListener {
                    // Profile picture URL saved successfully
                    Log.d("PROFILE_PICTURE", "Profile picture URL saved successfully: $imageURL")
                    Toast.makeText(
                        this@myprofile,
                        "Profile picture uploaded successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Finish the activity after saving profile information
                    finish()
                    // Retrieve and display the updated profile picture
                    retrieveProfilePicture()
                }
                .addOnFailureListener { e ->
                    // Failed to save profile picture URL
                    Log.e("PROFILE_PICTURE", "Failed to save profile picture URL: ${e.message}", e)
                    Toast.makeText(
                        this@myprofile,
                        "Failed to save profile picture URL: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun retrieveProfilePicture() {
        // Get the current user's UID
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        // Create a reference to the Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().reference
        // Create a reference to the current user's node
        val userReference = databaseReference.child("users").child(currentUserUid)

        // Retrieve the profile picture URL from the user node
        userReference.child("profile_picture")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val profilePictureUrl = dataSnapshot.getValue(String::class.java)
                    profilePictureUrl?.let { url ->
                        // Load the profile picture into the ImageView using the retrieved URL
                        Glide.with(this@myprofile)
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.profilepicture)
                            .error(R.drawable.profilepicture)
                            .into(findViewById(R.id.dp))
                    } ?: run {
                        // If the profile picture URL is null, set a default picture
                        findViewById<ImageView>(R.id.dp).setImageResource(R.drawable.profilepicture)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during the retrieval process
                    Log.e(
                        "PROFILE_PICTURE",
                        "Failed to retrieve profile picture URL: ${databaseError.message}",
                        databaseError.toException()
                    )
                    Toast.makeText(
                        this@myprofile,
                        "Failed to retrieve profile picture",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}