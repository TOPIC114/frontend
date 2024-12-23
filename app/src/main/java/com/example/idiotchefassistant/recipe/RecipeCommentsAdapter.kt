package com.example.idiotchefassistant.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.idiotchefassistant.R

class RecipeCommentsAdapter(private var items: List<Comment>) :
    RecyclerView.Adapter<RecipeCommentsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.user_name)
        val rate: RatingBar = view.findViewById(R.id.user_rating)
        val content: TextView = view.findViewById(R.id.user_comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = items[position].username
        holder.rate.rating = items[position].score.toFloat()
        holder.content.text = items[position].content
    }
}