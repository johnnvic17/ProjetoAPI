package com.projeto.praticaapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projeto.praticaapi.R
import com.projeto.praticaapi.model.RatingsModel

class RatingsAdapter(private val ratingsList: List<RatingsModel>):
    RecyclerView.Adapter<RatingsAdapter.RatingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingsViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_ratings, parent, false)
        return RatingsViewHolder(item)
    }

    override fun getItemCount(): Int {
        return ratingsList.size
    }

    override fun onBindViewHolder(holder: RatingsViewHolder, position: Int) {
        holder.bind(ratingsList[position])
    }

    inner class RatingsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(ratings: RatingsModel){

            val title: TextView = itemView.findViewById(R.id.item_title_ratings)
            title.text = ratings.title

            val ratingsValue: TextView = itemView.findViewById(R.id.item_value_ratings)
            ratingsValue.text = ratings.ratingValue
        }
    }
}