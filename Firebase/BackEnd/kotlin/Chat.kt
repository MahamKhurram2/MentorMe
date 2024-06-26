package com.mahamkhurram.i210681

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
class Chat : AppCompatActivity() {
   /* var userList = ArrayList<user_CS>()
    private var currentid: Int = 1
    lateinit var userRecyclerView: RecyclerView
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val backArrowImageView: ImageView = findViewById(R.id.bakarrow)
        backArrowImageView.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        val homeIcon = findViewById<ImageView>(R.id.homeicon)
        val searchIcon = findViewById<ImageView>(R.id.searchicon)
        val chatIcon = findViewById<ImageView>(R.id.chaticon)
        val personIcon = findViewById<ImageView>(R.id.personicon)

        currentid = intent.getIntExtra("currentid", 1)

        homeIcon.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        searchIcon.setOnClickListener {
            val intent = Intent(this, search::class.java)
            startActivity(intent)
        }

        chatIcon.setOnClickListener {
            val intent = Intent(this, chatscreen::class.java)
            startActivity(intent)
        }

        personIcon.setOnClickListener {
            val intent = Intent(this, myprofile::class.java)
            startActivity(intent)
        }

        userRecyclerView = findViewById<RecyclerView>(R.id.userRecyclerView)
        userRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter = UserAdapter(this, userList)
        userRecyclerView.adapter = adapter

        getUsers()
    }

    private fun getUsers() {
        val url = "http://192.168.8.101/A3/showusers.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                userList.clear()

                for (i in 0 until response.length()) {
                    val userJson = response.getJSONObject(i)
                    //val uid = userJson.getString("uid")
                    val userId = userJson.getString("user_id")
                    Log.d("ChatActivity", "Retrieved user ID: $userId")
                    val name = userJson.getString("name")
                    val profilePicture = if (userJson.has("profile_picture")) {
                        userJson.getString("profile_picture")
                    } else {
                        ""
                    }


                    val user =user_CS(userId, name, profilePicture)
                    userList.add(user_CS())
                }

                adapter.notifyDataSetChanged()

                Log.d("ChatActivity", "User list size: ${userList.size}")
                Log.d("ChatActivity", "Users: $userList")
            },
            { error ->
                Toast.makeText(this@Chat, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("ChatActivity", "Error fetching users: ${error.message}")
            }
        )

        Volley.newRequestQueue(this@Chat).add(jsonArrayRequest)
    }
}


*/
   private lateinit var toprv: RecyclerView
    val userList = mutableListOf<User_chat>()
    private var currentid: Int = 1
    lateinit var adapter: UserAdapter

//    val adapter = user_adapter(userList, this,currentid)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        currentid = intent.getIntExtra("currentid", 1)

        // Initialize adapter with 'currentid'
        adapter = UserAdapter(userList, this, currentid)




//        val fip = findViewById<TextView>(R.id.johncooper)
//
//        fip.setOnClickListener {
//            val intent = Intent(this, privatemessage::class.java)
//            startActivity(intent)
//        }


        // Initialize RecyclerViews
        toprv = findViewById(R.id.userRecyclerView)
        toprv.setHasFixedSize(true)
        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Create and set adapter
        retrieveMentorsFromWebService()
        toprv.adapter = adapter

        // Rest of your code for handling button clicks
    }

    private fun retrieveMentorsFromWebService() {
        // Replace "YOUR_WEB_SERVICE_URL_HERE" with the actual URL of your web service
        val url = "http://172.20.10.9/A3/showusers.php"

        // Create a JSON array request to fetch data from the web service
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Clear the existing list of users
                userList.clear()

                // Parse the JSON response
                for (i in 0 until response.length()) {
                    val userJson = response.getJSONObject(i)

                    // Extract user data from JSON
                    val name = userJson.getString("name")
                    val uid = userJson.getString("uid")
//                    val photoLink = userJson.getInt("photolink")

                    // Create a User_rv object and add it to the list
                    val user = User_chat()
                    user.name = name
                    user.uid = uid
//                    user.photolink = photoLink
                    userList.add(user)
                }

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged()
            },
            { error ->
                // Handle error
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

}




/* fun getUsers() {
     val databaseReference = FirebaseDatabase.getInstance().getReference("Mentors")
     val usersRef = FirebaseDatabase.getInstance().getReference("users")

     val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

     // Create lists for users and mentors
     val userList = ArrayList<user_CS>()
     val mentorList = ArrayList<user_CS>()
     databaseReference.addValueEventListener(object : ValueEventListener {
         override fun onDataChange(snapshot: DataSnapshot) {
             mentorList.clear() // Clear the list before adding new data
             for (snap in snapshot.children) {
                 val mentorName: String? = snap.child("name").getValue(String::class.java)
                 val mentorImageURL: String? =
                     snap.child("imageURL").getValue(String::class.java)

                 // Check if mentor name and image URL are not null or empty before adding to the list
                 if (!mentorName.isNullOrEmpty() && !mentorImageURL.isNullOrEmpty()) {
                     mentorList.add(user_CS(userName = mentorName, userImage = mentorImageURL))
                     Log.d(
                         "FirebaseData",
                         "Mentor Name: $mentorName, Image URL: $mentorImageURL"
                     )
                 }
             }
             // After fetching mentors, update the adapter
             updateAdapter(userList, mentorList)
         }

         override fun onCancelled(error: DatabaseError) {
             Toast.makeText(this@Chat, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
         }
     })
     // Fetch users
     usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
         override fun onDataChange(dataSnapshot: DataSnapshot) {
             for (userSnapshot in dataSnapshot.children) {
                 val userID = userSnapshot.key
                 val userName: String? = userSnapshot.child("name").getValue(String::class.java)

                 // Check if user ID, user name, and current user ID are not null or empty,
                 // and exclude the current user
                 if (!userID.isNullOrEmpty() && !userName.isNullOrEmpty() && userID != currentUserID) {
                     // Add the user to the list without specifying the userImage parameter
                     userList.add(user_CS(userName = userName, userImage = null))
                     Log.d("FirebaseData", "User ID: $userID, User Name: $userName")
                 }
             }
             // After fetching users, update the adapter
             updateAdapter(userList, mentorList)
         }

         override fun onCancelled(databaseError: DatabaseError) {
             // Handle error
             Toast.makeText(this@Chat, "Error: ${databaseError.message}", Toast.LENGTH_SHORT)
                 .show()
         }
     })

     // Fetch mentors

 }

 fun updateAdapter(userList: List<user_CS>, mentorList: List<user_CS>) {
     // Combine user and mentor lists into a single list
     val mergedList = ArrayList<Any>().apply {

         addAll(mentorList)
         addAll(userList)
     }
     // Update RecyclerView adapter with the merged list
     val userAdapter = UserAdapter(this@Chat, mergedList as ArrayList<user_CS>)
     userRecyclerView.adapter = userAdapter
 }*/
