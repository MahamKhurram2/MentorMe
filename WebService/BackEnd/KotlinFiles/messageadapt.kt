package com.mahamkhurram.i210681


import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject

class messageadapt(private val mContext: Context, private val chatList: List<ChatRV>, private val image: String,private val id:Int) : RecyclerView.Adapter<messageadapt.ChatViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(mContext).inflate(
            if (viewType == MSG_TYPE_RIGHT) R.layout.sendmessage else R.layout.recievemessage,
            parent, false
        )
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]


        Log.d("ChatMessage", "image: ${chat.image}, message: ${chat.message}")

        // Check if the message is from the current user
        val isCurrentUserMessage = chat.sender == id

        // Set visibility of message views based on message type
        if (chat.image=="null") {
            holder.showMessage.visibility = View.VISIBLE
            holder.chatImage.visibility = View.GONE
            holder.showMessage.text = chat.message
        } else if(chat.message=="null"){
            holder.showMessage.visibility = View.GONE
            holder.chatImage.visibility = View.VISIBLE
            // Load image using Glide only if it's not the default value
            if (chat.image != "default") {
                Glide.with(mContext).load(chat.image).into(holder.chatImage)
            }
        }

        // Set visibility and behavior of voice message button
        if (chat.voiceNoteUrl.isNullOrEmpty()) {
            holder.voiceMessageButton.visibility = View.GONE
        } else {
            holder.voiceMessageButton.visibility = View.VISIBLE
            holder.voiceMessageButton.setOnClickListener {
                // Handle voice message playback
                // playAudio(chat.voiceNoteUrl)
            }
        }

        // Load user image



        holder.itemView.setOnClickListener {
            showOptionsDialog(chat)
        }

    }

    private fun showOptionsDialog(chat: ChatRV) {
        val options = arrayOf("Edit", "Delete")

        AlertDialog.Builder(mContext)
            .setTitle("Select Action")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> editMessage(chat)
                    1 -> deleteMessage(chat)
                }
            }
            .show()
    }

    private fun deleteMessage(chat: ChatRV) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Edit Message")

        if (chat.message == "null") {
            if(chat.image=="null"){
                chat.voiceNoteUrl?.let { deleteMessageOnServer(it) }
            }
            else{
                chat.image?.let { deleteMessageOnServer(it) }
            }
        }
        else {
            chat.message?.let { deleteMessageOnServer(it) }
        }

    }

    private fun deleteMessageOnServer(messageId: String) {
        val url = "http://172.20.10.9/A3/delete_message.php"

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getInt("success")
                    val message = jsonResponse.getString("message")

                    if (success == 1) {
                        // Message deleted successfully
                        Toast.makeText(mContext, "Message deleted", Toast.LENGTH_SHORT).show()
                        // Optionally, remove the message from your local list and update the UI
                        // chatList.remove(chat)
                        // notifyDataSetChanged()
                    } else {
                        // Error deleting message
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(mContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["message_id"] = messageId
                return params
            }
        }

        Volley.newRequestQueue(mContext).add(request)
    }

    private fun editMessage(chat: ChatRV) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Edit Message")

        // Set up the input
        val input = EditText(mContext)
        input.setText(chat.message)
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { dialog, which ->
            val newMessage = input.text.toString().trim()
            if (newMessage.isNotEmpty()) {
                chat.message?.let { updateMessageOnServer(it, newMessage) }
            } else {
                Toast.makeText(mContext, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun updateMessageOnServer(messageId: String, newMessage: String) {
        val url = "http://172.20.10.9/A3/update_message.php"

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getInt("success")
                    val message = jsonResponse.getString("message")
                    if (success == 1) {
                        // Handle success
                        Toast.makeText(mContext, "Message updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle error
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(mContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error: ${error.message}", error)
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["message_id"] = messageId.toString()
                params["new_message"] = newMessage
                return params
            }
        }

        Volley.newRequestQueue(mContext).add(request)
    }


    private fun stopAudio() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showMessage: TextView = itemView.findViewById(R.id.message) // Corrected ID
        //val profileImage: ImageView = itemView.findViewById(R.id.profileimage)
        val chatImage: ImageView = itemView.findViewById(R.id.chatImage)// Corrected ID
        val voiceMessageButton: ImageView = itemView.findViewById(R.id.voiceNoteIcon)
    }


    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].sender == id) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        // Release MediaPlayer resources when adapter is detached from RecyclerView
        stopAudio()
    }

    private fun playAudio(audioUrl: String) {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                    reset()
                }
                setDataSource(audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    start()
                }
                setOnCompletionListener {
                    stopAudio()
                }
                setOnErrorListener { mp, what, extra ->
                    stopAudio()
                    Toast.makeText(mContext, "Error playing audio: $what", Toast.LENGTH_SHORT).show()
                    true
                }
            } ?: run {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioUrl)
                    prepareAsync()
                    setOnPreparedListener {
                        start()
                    }
                    setOnCompletionListener {
                        stopAudio()
                    }
                    setOnErrorListener { mp, what, extra ->
                        stopAudio()
                        Toast.makeText(mContext, "Error playing audio: $what", Toast.LENGTH_SHORT).show()
                        true
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    public interface onClickMessage {
        public fun onItemClick(key: String,isSent:Boolean,view: ImageView)
    }

}


















//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.google.firebase.auth.FirebaseAuth
//import com.laraib.i210865.R
//import com.laraib.i210865.ChatRV
//
//class messageadapter(private val mContext: Context, private val chatList: List<ChatRV>, private val imageurl: String) : RecyclerView.Adapter<messageadapter.ChatViewHolder>() {
//
//    private val fuser = FirebaseAuth.getInstance().currentUser
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
//        val view = LayoutInflater.from(mContext).inflate(
//            if (viewType == MSG_TYPE_RIGHT) R.layout.chat_item_right else R.layout.chat_item_left,
//            parent, false
//        )
//        return ChatViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
//        val chat = chatList[position]
//
//        // Check if it's a text message or a voice chat message
//        if (chat.message?.isNotEmpty() == true) {
//            // Text message
//            holder.showMessage.visibility = View.VISIBLE
//            holder.voiceMessageButton.visibility = View.GONE
//            holder.showMessage.text = chat.message
//        }
//        if (chat.voiceNoteUrl?.isNotEmpty() == true) {
//            // Voice chat message
//
//            holder.showMessage.visibility = View.GONE
//            holder.voiceMessageButton.visibility = View.VISIBLE
//
//            // Add click listener to play voice message
//            holder.voiceMessageButton.setOnClickListener {
//                // Implement voice message playback logic here
//            }
//        }
//
//        // Load user image using Glide
//        if (imageurl == "default") {
//            holder.profileImage.setImageResource(R.drawable.ic_launcher_background)
//        } else {
//            Glide.with(mContext).load(imageurl).into(holder.profileImage)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return chatList.size
//    }
//
//    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val showMessage: TextView = itemView.findViewById(R.id.showmessage)
//        val profileImage: ImageView = itemView.findViewById(R.id.profileimage)
//        val voiceMessageButton: ImageView = itemView.findViewById(R.id.voice_message_button)
//    }
//
//    companion object {
//        const val MSG_TYPE_LEFT = 0
//        const val MSG_TYPE_RIGHT = 1
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (chatList[position].sender == fuser?.uid) {
//            MSG_TYPE_RIGHT
//        } else {
//            MSG_TYPE_LEFT
//        }
//    }
//}

//
//import android.content.Context
//import android.media.MediaPlayer
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.google.firebase.auth.FirebaseAuth
//import com.laraib.i210865.R
//import com.laraib.i210865.ChatRV
//
//class messageadapter(private val mContext: Context, private val chatList: List<ChatRV>, private val image: String,private val id:Int) : RecyclerView.Adapter<messageadapter.ChatViewHolder>() {
//
//    private val fuser = FirebaseAuth.getInstance().currentUser
//    private var mediaPlayer: MediaPlayer? = null
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
//        val view = LayoutInflater.from(mContext).inflate(
//            if (viewType == MSG_TYPE_RIGHT) R.layout.chat_item_right else R.layout.chat_item_left,
//            parent, false
//        )
//        return ChatViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
//        val chat = chatList[position]
//        var isSent = false
//        holder.showMessage.text = chat.message
//        if (chat.image.isNullOrEmpty()) {
//            // If imageUrl is null or empty, display text message
//            holder.showMessage.visibility = View.VISIBLE
//            holder.chatImage.visibility = View.GONE
//            holder.showMessage.text = chat.message
//            isSent = true
//        } else {
//            // If imageUrl is not empty, display image
//            holder.showMessage.visibility = View.GONE
//            holder.chatImage.visibility = View.VISIBLE
//            Glide.with(mContext).load(chat.image).into(holder.chatImage)
//        }
//
//        // Check if it's a text message or a voice chat message
//        if (chat.message?.isNotEmpty() == true) {
//            // Text message
//            holder.showMessage.visibility = View.VISIBLE
//            holder.voiceMessageButton.visibility = View.GONE
//            holder.showMessage.text = chat.message
//            Toast.makeText(mContext, "bye", Toast.LENGTH_SHORT).show()
//        }
//
//        Toast.makeText(mContext, "Audio URL: ${chat.voiceNoteUrl}", Toast.LENGTH_SHORT).show()
//        if (chat.voiceNoteUrl?.isNotEmpty() == true) {
//            // Voice chat message
//            holder.showMessage.visibility = View.GONE
//            holder.voiceMessageButton.visibility = View.VISIBLE
//
//
//            Toast.makeText(mContext, "hii", Toast.LENGTH_SHORT).show()
//
//            // Add click listener to play voice message
//            holder.voiceMessageButton.setOnClickListener {
//                // Stop any previously playing audio
//
//                Toast.makeText(mContext, "audio is playing", Toast.LENGTH_SHORT).show()
//
//                stopAudio()
//
//                val voiceNoteUrl = chat.voiceNoteUrl
//                if (voiceNoteUrl != null) {
//                    playAudio(voiceNoteUrl)
//                }// Initialize MediaPlayer
////                mediaPlayer = MediaPlayer().apply {
////                    setDataSource(chat.voiceNoteUrl)
////                    prepareAsync()
////                    setOnPreparedListener { start() }
////                    setOnCompletionListener { stopAudio() }
////                }
//            }
//
//        }
//
//        // Load user image using Glide
//        if (image == "default") {
//            holder.profileImage.setImageResource(R.drawable.ic_launcher_background)
//        } else {
//            Glide.with(mContext).load(image).into(holder.profileImage)
//        }
//    }
//
//    private fun stopAudio() {
//        mediaPlayer?.apply {
//            stop()
//            release()
//        }
//        mediaPlayer = null
//    }
//
//    override fun getItemCount(): Int {
//        return chatList.size
//    }
//
//    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val showMessage: TextView = itemView.findViewById(R.id.showmessage) // Corrected ID
//        val profileImage: ImageView = itemView.findViewById(R.id.profileimage)
//        val chatImage: ImageView = itemView.findViewById(R.id.chatImage)// Corrected ID
//        val voiceMessageButton: ImageView = itemView.findViewById(R.id.voice_message_button)
//    }
//
//
//    companion object {
//        const val MSG_TYPE_LEFT = 0
//        const val MSG_TYPE_RIGHT = 1
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (chatList[position].sender == id) {
//            MSG_TYPE_RIGHT
//        } else {
//            MSG_TYPE_LEFT
//        }
//    }
//
//    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView)
//        // Release MediaPlayer resources when adapter is detached from RecyclerView
//        stopAudio()
//    }
//
//    private fun playAudio(audioUrl: String) {
//        try {
//            mediaPlayer?.apply {
//                if (isPlaying) {
//                    stop()
//                    reset()
//                }
//                setDataSource(audioUrl)
//                prepareAsync()
//                setOnPreparedListener {
//                    start()
//                }
//                setOnCompletionListener {
//                    stopAudio()
//                }
//                setOnErrorListener { mp, what, extra ->
//                    stopAudio()
//                    Toast.makeText(mContext, "Error playing audio: $what", Toast.LENGTH_SHORT).show()
//                    true
//                }
//            } ?: run {
//                mediaPlayer = MediaPlayer().apply {
//                    setDataSource(audioUrl)
//                    prepareAsync()
//                    setOnPreparedListener {
//                        start()
//                    }
//                    setOnCompletionListener {
//                        stopAudio()
//                    }
//                    setOnErrorListener { mp, what, extra ->
//                        stopAudio()
//                        Toast.makeText(mContext, "Error playing audio: $what", Toast.LENGTH_SHORT).show()
//                        true
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Toast.makeText(mContext, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    public interface onClickMessage {
//        public fun onItemClick(key: String,isSent:Boolean,view: ImageView)
//    }
//
//
//}
