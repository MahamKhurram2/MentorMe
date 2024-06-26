package com.mahamkhurram.i210681
//import VolleyMultipartRequest
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.SurfaceView
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mahamkhurram.i210681.databinding.ActivityChatscreenBinding
import io.agora.rtc2.RtcEngine
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class chatscreen : AppCompatActivity() {
    /*private var binding: ActivityChatscreenBinding? = null
    private var adapter: MessageAdapter? = null
    private var messages: ArrayList<Message> = ArrayList()
    private var senderRoom: String? = null
    private var receiverRoom: String? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var dialog: ProgressDialog? = null
    private var senderUid: String? = null
    private var receiverUid: String? = null
    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )
*/
    private var encodedImage: String = ""
    private val appId = "004832ecc0de4f95b8d9dfd42d040b3d"
    private val channelName = "mentorme"
    private val token =
        "007eJxTYODf7d2tsv7/xgw7tnXSAn7BL4U1O1wfb/prYf9VOF1w0nkFBgMDEwtjo9TkZIOUVJM0S9MkixTLlLQUE6MUAxODJOMUJo1/qQ2BjAyHGlVZGRkgEMTnYMhNzSvJL8pNZWAAAEm8H5A="
    private val uid = 0
    private var isJoined = false
    private var imageURL: String? = null
    private var agoraEngine: RtcEngine? = null
    private lateinit var text_send: TextView
    private var localSurfaceView: SurfaceView? = null
    private var isRecording = false
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var recorder: MediaRecorder
    private var mchat = mutableListOf<ChatRV>()
    private var outputFile: File? = null
    private var remoteSurfaceView: SurfaceView? = null
    private var binding: ActivityChatscreenBinding? = null
    private lateinit var username: TextView
    private lateinit var fuser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var madapter: messageadapt
    private lateinit var mChat: ArrayList<chatt>
    private lateinit var recyclerView: RecyclerView
    private val GALLERY_REQUEST_CODE = 1001
    private val PERMISSION_REQ_ID = 22
    private lateinit var bitmap: Bitmap
    lateinit var chooseimg: ImageView
    var fileuri: Uri? = null
    private var imageurl: String = ""
    private val REQUESTED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }

    fun showMessage(message: String?) {
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Log.d("chatscreen", "onCreate called")
        //binding = ActivityChatscreenBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_chatscreen)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        text_send = findViewById(R.id.messagebox)
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        val currentidString = intent.getStringExtra("currentid")
        //val currentid = currentidString?.toIntOrNull() ?: 1 // Default value is 1 if "currentid" is not found or conversion fails
        //Toast.makeText(this, "Current ID: $currentid", Toast.LENGTH_SHORT).show()
        val currentid = intent.getIntExtra("currentid", 1)
        val userId = intent.getIntExtra("userid", 2)
        val userIdString = intent.getStringExtra("userid")
       // val userId = userIdString?.toIntOrNull() ?: 2 // Default value is 2 if "userid" is not found or conversion fails
        Log.d("chatscreen", "Received user ID: $userId")
        //Toast.makeText(this, "user id: $userId", Toast.LENGTH_SHORT).show()



        val usernameText = findViewById<TextView>(R.id.usernametext)

        val userName = intent.getStringExtra("username")
        if (userName != null) {
            usernameText.text = userName
        } else {
            // Handle the case where userName is null
            Log.e("chatscreen", "Username is null")
        }
        //setupVideoSDKEngine();
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        chooseimg = findViewById(R.id.uploadphoto)
        chooseimg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                111)
        }

        val sendbutton = findViewById<ImageView>(R.id.sendbutton)
        //val text_send = findViewById<TextView>(R.id.messagebox)
        sendbutton.setOnClickListener {
            val msg = text_send.text.toString()
            if (msg.isNotBlank()) {
                if (userId != null) {
                    if (isNetworkAvailable(this)) {
                        // Your code to execute when network is available
                        sendmessage(currentid, userId, msg)
                        sendMessageToSQLite(this, currentid, userId, msg)
                        NotificationHelper(this, msg).Notification()
                        val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        kh.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    } else {
                        sendMessageToSQLite(this, currentid, userId, msg)
                        Toast.makeText(this@chatscreen, "Message saved offline", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //sendMessageToSQLite(this, currentid, userId, msg)
                    Toast.makeText(this@chatscreen, "Receiver ID is null", Toast.LENGTH_SHORT).show()
                }
            } else if (fileuri != null) {
                sendMessageWithImage(currentid, userId, encodedImage!!)
                NotificationHelper(this,"Image sent successfully!").Notification()
                val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
            } else {
                Toast.makeText(
                    this@chatscreen,
                    "You can't send an empty message",
                    Toast.LENGTH_SHORT
                ).show()
            }
            text_send.text = ""
        }


        readmessage(currentid, userId, imageurl)


        val attachFileButton = findViewById<ImageView>(R.id.attachfile)
        attachFileButton.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
        val cameraicon = findViewById<ImageView>(R.id.cameraicon)

// Set a click listener on the chat icon
        cameraicon.setOnClickListener {
            // Create an Intent to start the camera activity

                Dexter.withContext(applicationContext)
                    .withPermissions(
                        android.Manifest.permission.CAMERA
                    ).withListener(object :
                        MultiplePermissionsListener { // Explicitly define the listener type
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            // Check if all permissions were granted
                            if (report != null && report.areAllPermissionsGranted()) {
                                // Start the camera activity
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, 101)

                            } else {
                                Toast.makeText(
                                    this@chatscreen,
                                    "Camera permission is required",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                        ) {
                            // Handle rationale for permissions
                            token?.continuePermissionRequest()
                        }
                    }).check()
            NotificationHelper(this,"image captured Sucessfully!").Notification()
            val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
            }

            val phoneIcon = findViewById<ImageView>(R.id.phoneicon)

            // Set a click listener on the phone icon
            phoneIcon.setOnClickListener {
                // Create an Intent to start the voice call activity
               // val userIdString = intent.getStringExtra("userid")
                //val userIdd = userIdString?.toIntOrNull() ?: 2
                val intent = Intent(this, voicecall::class.java)
                // Start the voice call activity
                //intent.putExtra("userId", userIdd)
                startActivity(intent)
            }


            // Find the ImageView for the video call icon
            val videoCallIcon = findViewById<ImageView>(R.id.videocallicon)

            // Set a click listener on the video call icon
            videoCallIcon.setOnClickListener {

                val intent = Intent(this, videocall::class.java)
                // Pass any necessary data to the video call screen here
                startActivity(intent)
            }
            val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
            backArrowImageView.setOnClickListener {
                val intent = Intent(this, Chat::class.java)
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
            val voicenote = findViewById<ImageView>(R.id.mic)
            mediaPlayer = MediaPlayer()
            recorder = MediaRecorder()

            voicenote.setOnClickListener {
                if (isRecording) {
                    stopRecording()
                } else {
                    startRecording()
                }
            }
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


        }


        private fun sendmessage(sender: Int, receiver: Int, message: String) {
            val url = "http://172.20.10.9/A3/send_message.php"

            val request = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // Handle response
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                },

                Response.ErrorListener { error ->
                    // Handle error
                    Toast.makeText(
                        this,
                        "Failed to send message: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["sender"] = sender.toString()
                    params["receiver"] = receiver.toString()
                    params["message"] = message
                    return params
                }

            }

            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(request)
        }

    private fun readmessage(myid: Int, userid: Int, imageurl: String) {
        Log.d("chatscreen", "Fetching messages for user ID: $userid")
        val url = "http://172.20.10.9A3/readmessage.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response
                try {
                    Log.d("chatscreen", "Received response: $response")
                    val jsonArray = JSONArray(response)
                    mchat.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val sender = jsonObject.getInt("sender")
                        val receiver = jsonObject.getInt("receiver")
                        val message = jsonObject.getString("message")
                        val image = jsonObject.getString("image")

                        // Log the contents of the chat message
                        Log.d(
                            "ChatMessage",
                            "Sender: $sender, Receiver: $receiver, Message: $message, Image: $image"
                        )

                        val chat = ChatRV(sender, receiver, message, image)
                        mchat.add(chat)
                    }
                    // Log the size of mchat after adding messages
                    Log.d("chatscreen", "mchat size after adding messages: ${mchat.size}")

                    // Pass mchat to the adapter after setting voiceNoteUrl for each ChatRV object
                    madapter = messageadapt(this@chatscreen, mchat, imageurl, myid)
                    recyclerView.adapter = madapter
                    // Notify the adapter that the data set has changed
                    madapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@chatscreen, "Failed to parse JSON", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("chatscreen", "Failed to parse JSON: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(
                    this@chatscreen,
                    "Failed to retrieve chat data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("chatscreen", "Failed to retrieve chat data: ${error.message}")
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["myid"] = myid.toString()
                params["userid"] = userid.toString()
                return params
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }




    private fun getFileExtension(uri: Uri): String? {
            val contentResolver = contentResolver
            val mimeTypeMap = MimeTypeMap.getSingleton()
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
        }




        private fun startRecording() {
            try {
                // Create a temporary file to store the recorded voice note
                outputFile = File.createTempFile("voice_note", ".3gp", cacheDir)
                // Set up the MediaRecorder
                recorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    outputFile?.let { setOutputFile(it.absolutePath) }
                    prepare()
                    start()
                }
                isRecording = true
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
                // Handle IOException
            } catch (e: IllegalStateException) {
                Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
                // Handle IllegalStateException
            } catch (e: Exception) {
                Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
                // Handle other exceptions
            }
        }


        private fun stopRecording() {
            try {
                recorder.stop()
                recorder.release()
                isRecording = false
                outputFile?.let { outputFile ->
                    // Upload the recorded audio file to Firebase Storage
                    uploadAudio(outputFile)
                }
            } catch (e: IllegalStateException) {
                Log.e("MainActivity15", "Error stopping recording: ${e.message}", e)
                // Handle IllegalStateException properly
            } catch (e: Exception) {
                Log.e("MainActivity15", "Error stopping recording: ${e.message}", e)
                // Handle other exceptions
            }
        }

        private fun sendMessageWithImage(sender: Int, receiver: Int, image: String) {
            val url = "http://172.20.10.9/A3/sendmessagewithimage.php"
            val request = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // Handle response
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    // Handle error
                    Toast.makeText(
                        this,
                        "Failed to send message: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["sender"] = sender.toString()
                    params["receiver"] = receiver.toString()
                    params["image"] = image // Send the Base64 encoded image data
                    return params
                }
            }
            Volley.newRequestQueue(this).add(request)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                fileuri = data.data // Get the URI of the selected image
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(fileuri!!)
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    chooseimg.setImageBitmap(bitmap)
                    encodeBitmapImage(bitmap)

                    // Send the image to be stored on the web
                    val currentid = intent.getIntExtra("currentid", 1)
                    val userId = intent.getIntExtra("userid", 2)
                    sendMessageWithImage(currentid, userId, encodedImage)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
                // Get the captured image
                val imageBitmap = data.extras?.get("data") as Bitmap

                // Convert the captured image to Base64
                val outputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val byteArray = outputStream.toByteArray()
                val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

                // Send the image to be stored on the web
                val currentid = intent.getIntExtra("currentid", 1)
                val userId = intent.getIntExtra("userid", 2)
                sendMessageWithImage(currentid, userId, encodedImage)
            }
        }


        private fun encodeBitmapImage(bitmap: Bitmap) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val bytesofimage: ByteArray = byteArrayOutputStream.toByteArray()
            encodedImage = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT)
        }


        private fun uploadAudio(audioFile: File) {
            val userId = intent.getStringExtra("userId")!!
            val storageReference = FirebaseStorage.getInstance().reference.child("audio")
            val audioFileName = "${System.currentTimeMillis()}.3gp"
            val audioRef = storageReference.child(audioFileName)

            val uploadTask = audioRef.putFile(Uri.fromFile(audioFile))
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                audioRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    // Once audio is uploaded, send message with audio URL
                    sendMessageWithAudio(fuser.uid, userId, downloadUri.toString())
                } else {
                    // Handle failures
                }
            }
        }

        private fun sendMessageWithAudio(sender: String, receiver: String, audioUrl: String) {
            val reference = FirebaseDatabase.getInstance().reference

            val hashMap = HashMap<String, Any>()
            hashMap["sender"] = sender
            hashMap["receiver"] = receiver
            hashMap["audioUrl"] = audioUrl

            reference.child("Chats").push().setValue(hashMap)
        }


        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer.release()
            recorder.release()
        }

    }
private fun sendMessageToSQLite(context: Context, sender: Int, receiver: Int, message: String) {
    val db = ChatDatabase(context).writableDatabase
    val values = ContentValues().apply {
        put(ChatDatabase.Companion.KEY_SENDER, sender)
        put(ChatDatabase.Companion.KEY_RECEIVER, receiver)
        put(ChatDatabase.Companion.KEY_MESSAGE, message)
    }
    val newRowId = db.insert(ChatDatabase.Companion.TABLE_MESSAGES, null, values)
    db.close()
    Log.d("ChatDatabase", "Message inserted into SQLite database with ID: $newRowId")
}

private fun sendImageToSQLite(context: Context, sender: Int, receiver: Int, image: String) {
    val db = ChatDatabase(context).writableDatabase
    val values = ContentValues().apply {
        put(ChatDatabase.Companion.KEY_SENDER, sender)
        put(ChatDatabase.Companion.KEY_RECEIVER, receiver)
        put(ChatDatabase.Companion.KEY_IMAGE, image)
    }
    val newRowId = db.insert(ChatDatabase.Companion.TABLE_MESSAGES, null, values)
    db.close()
    Log.d("ChatDatabase", "Image message inserted into SQLite database with ID: $newRowId")
}
private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

/* setSupportActionBar(binding!!.toolbar)

 database = FirebaseDatabase.getInstance()
 storage = FirebaseStorage.getInstance()
 dialog = ProgressDialog(this@chatscreen)
 dialog!!.setMessage("Uploading Image...")
 dialog!!.setCancelable(false)

 val name = intent.getStringExtra("userName")
 val profile = intent.getStringExtra("userImage")
 binding!!.usernametext.text = name
 receiverUid = intent.getStringExtra("userId")
 senderUid = FirebaseAuth.getInstance().uid

 database!!.reference.child("Presence").child(receiverUid!!).addValueEventListener(
     object : ValueEventListener {
         override fun onDataChange(snapshot: DataSnapshot) {
             if (!snapshot.exists()) {
                 val status = snapshot.getValue(String::class.java)
                 if (status == "offline") {
                     binding!!.status.visibility = View.GONE
                 } else {
                     binding!!.status.text = status // Use setText method to set text
                     binding!!.status.visibility = View.VISIBLE // Set visibility to VISIBLE
                 }
             } else {
                 // Handle the case when the snapshot exists
             }

             // Initialize senderRoom and receiverRoom here
             senderRoom = senderUid + receiverUid
             receiverRoom = receiverUid + senderUid

             adapter = MessageAdapter(this@chatscreen, messages, senderRoom!!, receiverRoom!!)
             binding!!.recyclerview.layoutManager = LinearLayoutManager(this@chatscreen)
             binding!!.recyclerview.adapter = adapter
             database!!.reference.child("chat")
                 .child(senderRoom!!)
                 .child("messages")
                 .addValueEventListener(object : ValueEventListener {
                     override fun onDataChange(snapshot: DataSnapshot) {
                         messages.clear()
                         for (snapshot1 in snapshot.children) {
                             val message = snapshot1.getValue(Message::class.java)
                             message!!.messageId = snapshot1.key
                             messages.add(message)
                         }
                         adapter!!.notifyDataSetChanged()
                     }

                     override fun onCancelled(error: DatabaseError) {}
                 })
         }

         override fun onCancelled(error: DatabaseError) {
             // Handle onCancelled event
         }
     })

 binding!!.sendbutton.setOnClickListener {
     val messageText: String = binding!!.messagebox.text.toString()
     val data = Date()
     val message = Message(messageText, senderUid, data.time)
     binding!!.messagebox.setText("")
     val randomKey = database!!.reference.push().key

     // Update the receiver room to be specific to the user clicked on
     val specificReceiverRoom = senderUid + receiverUid

     val lastMsgObj = HashMap<String, Any>()
     lastMsgObj["lastMsg"] = message.message!!
     lastMsgObj["lastMsgTime"] = data.time
     database!!.reference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
     database!!.reference.child("chats").child(specificReceiverRoom).updateChildren(lastMsgObj) // Update here
     val messageRef = database!!.reference.child("chat").child(specificReceiverRoom).child("messages").child(randomKey!!)
     messageRef.setValue(message).addOnSuccessListener {
         // Handle success if needed
     }.addOnFailureListener { e ->
         // Handle failure if needed
     }
 }


 binding!!.attachfile.setOnClickListener {
     val intent = Intent()
     intent.action = Intent.ACTION_GET_CONTENT
     intent.type = "image/*"
     startActivityForResult(intent, 25)
 }
 val handler=Handler()
 binding!!.messagebox.addTextChangedListener(object : TextWatcher {
     override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
         // Do nothing
     }

     override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

     }

     override fun afterTextChanged(s: Editable?) {
         database!!.reference.child("Presence").child(senderUid!!).setValue("typing...")
         handler.removeCallbacksAndMessages(null)
         handler.postDelayed(userStoppedTyping, 1000)
     }
     var userStoppedTyping=Runnable {
         database!!.reference.child("Presence").child(senderUid!!).setValue("online")
     }
 })

}
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
 super.onActivityResult(requestCode, resultCode, data)
 if (requestCode == 25 && resultCode == RESULT_OK) {
     if (data != null && data.data != null) {
         val selectedImage = data.data
         val calendar = Calendar.getInstance()
         val ref = storage!!.reference.child("chats").child(calendar.timeInMillis.toString())
         dialog!!.show()
         val uploadTask = ref.putFile(selectedImage!!)
         uploadTask.continueWithTask { task ->
             if (!task.isSuccessful) {
                 task.exception?.let {
                     throw it
                 }
             }
             ref.downloadUrl
         }.addOnCompleteListener { task ->
             if (task.isSuccessful) {
                 val downloadUri = task.result
                 val filepath = downloadUri.toString()
                 val messagetxt: String = binding!!.messagebox.text.toString()
                 val data = Date()
                 val message = Message(messagetxt, senderUid, data.time)
                 message.message = "photo"
                 message.imageUrl = filepath
                 binding!!.messagebox.setText("")
                 val randomKey = database!!.reference.push().key
                 val lastMsgObj: MutableMap<String, Any> = hashMapOf(
                     "lastMsg" to "photo",
                     "lastMsgTime" to data.time
                 )
                 database!!.reference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
                 database!!.reference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)
                 database!!.reference.child("chat").child(senderRoom!!).child("messages").child(randomKey!!)
                     .setValue(message).addOnSuccessListener {
                         database!!.reference.child("chat").child(receiverRoom!!)
                             .child("messages").child(randomKey).setValue(message).addOnSuccessListener {
                                 dialog!!.dismiss()
                             }
                     }
             }
         }
     }
 }
}

override fun onResume() {
 super.onResume()
 val currentId = FirebaseAuth.getInstance().uid
 database!!.reference.child("Presence").child(currentId!!).setValue("online")
}
override fun onPause() {
 super.onPause()
 val currentId = FirebaseAuth.getInstance().uid
 database!!.reference.child("Presence").child(currentId!!).setValue("offline")
}
}


 */