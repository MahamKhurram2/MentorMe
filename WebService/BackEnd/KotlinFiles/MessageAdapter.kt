package com.mahamkhurram.i210681

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.mahamkhurram.i210681.databinding.DeletemessageBinding
import com.mahamkhurram.i210681.databinding.RecievemessageBinding
import com.mahamkhurram.i210681.databinding.SendmessageBinding

class MessageAdapter(private val mContext: Context, private val mChat: ArrayList<chatt>,private val imageURL: String) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val fuser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == ITEM_SEND) R.layout.sendmessage else R.layout.recievemessage
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mChat.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]
        var isSent = false
        holder.textm.text = chat.message
        Log.d("MessageAdapter", "Message: ${chat.message}")
        if (chat.imageUrl.isNullOrEmpty()) {
            // If imageUrl is null or empty, display text message
            holder.textm.visibility = View.VISIBLE
            holder.chatImage.visibility = View.GONE
            holder.textm.text = chat.message
            isSent = true
        } else {
            // If imageUrl is not empty, display image
            holder.textm.visibility = View.GONE
            holder.chatImage.visibility = View.VISIBLE
            Glide.with(mContext).load(chat.imageUrl).into(holder.chatImage)
        }
        if (!chat.audioUrl.isNullOrEmpty()) {
            holder. Voicenoteurl.visibility = View.VISIBLE
            // Set click listener to play audio when the voice note icon is clicked
            holder. Voicenoteurl.setOnClickListener {
                val audioUrl = chat.audioUrl
                if (!audioUrl.isNullOrEmpty()) {
                    playAudio(audioUrl)
                }
            }
        } else {
            // Hide the voice note icon if voice note is not sent
            holder. Voicenoteurl.visibility = View.GONE
        }
    }
    private fun playAudio(audioUrl: String) {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                reset()
            }
            setDataSource(audioUrl)
            prepare()
            start()
        } ?: run {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepare()
                start()
            }
        }
    }


        override fun getItemViewType(position: Int): Int {
        return if (mChat[position].sender == fuser.uid) ITEM_SEND else ITEM_RECEIVE
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textm: TextView = view.findViewById(R.id.message)
        val chatImage: ImageView = itemView.findViewById(R.id.chatImage)
        var Voicenoteurl: ImageView = itemView.findViewById(R.id.voiceNoteIcon)
        //val txtUserName: TextView = view.findViewById(R.id.userName)
        //val txtTemp: TextView = view.findViewById(R.id.temp)
        //val imgUser: ImageView = view.findViewById(R.id.userImage)
    }

    companion object {
        const val ITEM_SEND = 1
        const val ITEM_RECEIVE = 0
    }
}

    /*  val ITEM_SEND = 1
        val ITEM_RECEIVE = 2
        val senderRoom: String
        var receiverRoom: String



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
           return if (viewType == ITEM_SEND) {
               val view = LayoutInflater.from(context).inflate(R.layout.sendmessage, parent, false)
               SenderViewHolder(view)
           } else {
               val view = LayoutInflater.from(context).inflate(R.layout.recievemessage, parent, false)
               ReceiverViewHolder(view)
           }
        }
    override fun getItemViewType(position: Int): Int {
        val message = messages!![position]
        return if (FirebaseAuth.getInstance().uid == message.senderId) {
            ITEM_SEND
        } else {
            ITEM_RECEIVE

        }
    }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = messages!![position]
            if (holder is SenderViewHolder) {
                val viewHolder = holder
                if (message.message == "photo") {
                    viewHolder.binding.image.visibility = View.VISIBLE
                    viewHolder.binding.message.visibility = View.GONE
                    viewHolder.binding.mlinear.visibility = View.GONE
                    Glide.with(context).load(message.imageUrl).into(viewHolder.binding.image)
                }
                viewHolder.binding.message.text = message.message
                viewHolder.itemView.setOnLongClickListener {
                    val view = LayoutInflater.from(context).inflate(R.layout.deletemessage, null)
                    val binding: DeletemessageBinding = DeletemessageBinding.bind(view)
                    val dialog = AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setView(binding.root)
                        .create()
                    binding.everyone.setOnClickListener {
                        message.message = "This message was removed"
                        message.messageId?.let { messageId ->
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                .child(receiverRoom).child("messages").child(messageId).setValue(message)
                            dialog.dismiss()
                        }
                    }

                    binding.delete.setOnClickListener {
                        message.messageId?.let { it1 ->
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                .child(senderRoom).child("messages").child(it1!!).setValue(null)
                        }
                        dialog.dismiss()
                    }
                    binding.cancel.setOnClickListener {

                        dialog.dismiss()
                    }
                    dialog.show()
                    false // indicate that the long click event was consumed
                }

            } else {
                val viewHolder = holder as ReceiverViewHolder
                if (message.message == "photo") {
                    viewHolder.binding.image.visibility = View.VISIBLE
                    viewHolder.binding.message.visibility = View.GONE
                    viewHolder.binding.mlinear.visibility = View.GONE
                    Glide.with(context).load(message.imageUrl).into(viewHolder.binding.image)
                }
                viewHolder.binding.message.text = message.message
                viewHolder.itemView.setOnLongClickListener {
                    val view = LayoutInflater.from(context).inflate(R.layout.deletemessage, null)
                    val binding: DeletemessageBinding = DeletemessageBinding.bind(view)
                    val dialog = AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setView(binding.root)
                        .create()
                    binding.everyone.setOnClickListener {
                        message.message = "This message was removed"
                        message.messageId?.let { it1 ->
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                .child(senderRoom).child("messages").child(it1).setValue(message)
                        }
                        message.messageId?.let { it1 ->
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                .child(receiverRoom).child("messages").child(it1!!).setValue(message)
                        }
                        dialog.dismiss()
                    }
                    binding.delete.setOnClickListener {
                        message.messageId?.let { it1 ->
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                .child(senderRoom).child("messages").child(it1!!).setValue(null)
                        }
                        dialog.dismiss()
                    }
                    binding.cancel.setOnClickListener {

                        dialog.dismiss()
                    }
                    dialog.show()
                    false // indicate that the long click event was consumed
                }
            }
        }

        override fun getItemCount(): Int = messages?.size ?: 0
        inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var binding: SendmessageBinding = SendmessageBinding.bind(itemView)
        }

        inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var binding: RecievemessageBinding = RecievemessageBinding.bind(itemView)
        }


        init {
            this.senderRoom = senderRoom
            this.receiverRoom = receiverRoom
        }*/

