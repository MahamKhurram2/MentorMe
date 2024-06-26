package com.mahamkhurram.i210681

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class UserAdapter(private val user_list: MutableList<User_chat>, private val mContext: Context, private val currentid: Int) : RecyclerView.Adapter<UserAdapter.userViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return userViewHolder(view)
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val mentor = user_list[position]
        holder.bind(mentor)

        holder.itemView.setOnClickListener {
            fetchUserDetails(mentor.uid, currentid) // Pass the currentid here
        }
    }

    override fun getItemCount(): Int {
        return user_list.size
    }

    inner class userViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in mentors_item layout
        private val heart: ImageView = itemView.findViewById(R.id.userImage)
        private val name: TextView = itemView.findViewById(R.id.userName)

        fun bind(mentor: User_chat) {
            // Set values to views
            heart.setImageResource(mentor.photolink)
            name.text = mentor.name
        }
    }

    private fun fetchUserDetails(userId: String?, currentid: Int) {
        userId?.let {
            val url = "http://172.20.10.9/A3/sendfromchats.php?userId=$userId"

            val request = StringRequest(
                Request.Method.GET,
                url,
                { response ->
                    // Log the response for debugging
                    Log.d("NetworkResponse", "Response: $response")

                    val userDetails = JSONObject(response)
                    val userName = userDetails.getString("name")
                    val userIdInt = userId.toIntOrNull() ?: 2
                    val intent = Intent(mContext, chatscreen::class.java)
                    intent.putExtra("userid", userId)
                    intent.putExtra("username", userName)
                    intent.putExtra("currentid", userIdInt)

                    // Log the intent details before starting the activity
                    Log.d(
                        "IntentDetails",
                        "User ID: $userId, Username: $userName, Current ID: $currentid"
                    )

                    mContext.startActivity(intent)
                },
                { error ->
                    // Log the error if the request fails
                    Log.e("NetworkError", "Failed to fetch user details: ${error.message}")
                    Toast.makeText(mContext, "Failed to fetch user details", Toast.LENGTH_SHORT)
                        .show()
                }
            )

            // Add the request to the Volley queue
            Volley.newRequestQueue(mContext).add(request)
        }
    }
}

/*  (private val context: Context, private val userList: ArrayList<user_CS>) :
  RecyclerView.Adapter<UserAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
      return ViewHolder(view)
  }

  override fun getItemCount(): Int {
      return userList.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val user = userList[position]
      holder.txtUserName.text = user.name

      // Load user profile picture using Glide library
      if (!user.profilePicture.isNullOrEmpty()) {
          Glide.with(context)
              .load(user.profilePicture)
              .placeholder(R.mipmap.ic_launcher) // Placeholder image while loading
              .error(R.mipmap.ic_launcher) // Error image if loading fails
              .into(holder.imgUser)
      } else {
          // If userProfilePicture is null or empty, load a default image
          holder.imgUser.setImageResource(R.mipmap.ic_launcher)
      }

      // Set click listener to open chat screen with user details
      holder.itemView.setOnClickListener {
          val intent = Intent(context, chatscreen::class.java)
          intent.putExtra("userName", user.name)
          intent.putExtra("userId", user.userId)
          context.startActivity(intent)
      }
  }

  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
      val txtUserName: TextView = view.findViewById(R.id.userName)
      val imgUser: ImageView = view.findViewById(R.id.userImage)
  }
}
*/