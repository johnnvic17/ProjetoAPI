package com.projeto.praticaapi.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.projeto.praticaapi.R
import com.projeto.praticaapi.model.ItemModel
import com.projeto.praticaapi.util.DownloadImageTask

class MainAdapter(private val items: List<ItemModel>,
                    private val listener: (String) -> Unit): RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.list_item_main, parent, false)
        return MainViewHolder(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(item: ItemModel){

            val title: TextView = itemView.findViewById(R.id.item_title)
            title.text = item.title

            val year: TextView = itemView.findViewById(R.id.item_year)
            year.text = item.year

            val poster: ImageView = itemView.findViewById(R.id.item_poster)
            poster.setOnClickListener {
                listener.invoke(item.id)
            }

            DownloadImageTask(object : DownloadImageTask.Callback{
                override fun onSuccess(bitmap: Bitmap) {
                    poster.setImageBitmap(bitmap)
                }
            }).imageUrl(item.poster)
        }
    }
}