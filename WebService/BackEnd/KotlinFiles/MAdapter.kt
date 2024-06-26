package com.mahamkhurram.i210681

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MAdapter(private val mentorList: List<mentorinfo>, private val context: Context) :
    RecyclerView.Adapter<MAdapter.MyViewHolder>() {
    // Rest of the class remains the same



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mentor = mentorList[position]
        holder.name.text = mentor.name
        holder.description.text = mentor.description
        holder.sessionPrice.text = mentor.sessionPrice
        holder.availability.text = mentor.availability
    }

    override fun getItemCount(): Int {
        return mentorList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nametext)
        val description: TextView = itemView.findViewById(R.id.description)
        val sessionPrice: TextView = itemView.findViewById(R.id.sessionprice)
        val availability: TextView = itemView.findViewById(R.id.availability)
    }
}