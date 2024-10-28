package com.projeto.praticaapi

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projeto.praticaapi.adapter.RatingsAdapter
import com.projeto.praticaapi.model.ItemDetailsModel
import com.projeto.praticaapi.model.RatingsModel
import com.projeto.praticaapi.util.DetailsTask
import com.projeto.praticaapi.util.DownloadImageTask

class ItemDetailsActivity : AppCompatActivity(), DetailsTask.Callback {
    private lateinit var img: ImageView
    private lateinit var title: TextView
    private lateinit var released: TextView
    private lateinit var categories: TextView
    private lateinit var genre: TextView
    private lateinit var desc: TextView
    private lateinit var creator: TextView
    private lateinit var cast: TextView
    private lateinit var awards: TextView
    private lateinit var metascore: TextView
    private lateinit var imdbRating: TextView
    private lateinit var votes: TextView

    private var ratingsList = mutableListOf<RatingsModel>()
    private lateinit var ratingsAdapter: RatingsAdapter

    private lateinit var progressDetails: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tb: Toolbar = findViewById(R.id.tb_details)
        setSupportActionBar(tb)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_btn_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        img = findViewById(R.id.image_details)
        title = findViewById(R.id.text_title_details)
        released = findViewById(R.id.text_released_details)
        categories = findViewById(R.id.text_categories_details)
        genre = findViewById(R.id.text_genre_details)
        desc = findViewById(R.id.text_desc_details)
        creator = findViewById(R.id.text_creator_details)
        cast = findViewById(R.id.text_cast_details)
        awards = findViewById(R.id.text_awards_details)
        metascore = findViewById(R.id.text_metascore_value_ratings)
        imdbRating = findViewById(R.id.text_imdb_value_ratings)
        votes = findViewById(R.id.text_imdb_votes_ratings)
        progressDetails = findViewById(R.id.progress_item_details)

        val itemID = intent?.getStringExtra("id") ?: throw IllegalArgumentException("Id not found!")
        val url = "https://www.omdbapi.com/?apikey=162ae16a&i=$itemID"
        DetailsTask(this).execute(url)

        ratingsAdapter = RatingsAdapter(ratingsList)
        val rvOthers: RecyclerView = findViewById(R.id.rv_others_ratings)
        rvOthers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvOthers.adapter = ratingsAdapter
    }

    override fun onLoading() {
        progressDetails.visibility = View.VISIBLE
    }

    override fun onResult(details: ItemDetailsModel) {

        DownloadImageTask(object : DownloadImageTask.Callback{
            override fun onSuccess(bitmap: Bitmap) {
                img.setImageBitmap(bitmap)
            }
        }).imageUrl(details.poster)

        title.text = details.title
        released.text = getString(R.string.string_released_details, details.released)
        categories.text = getString(R.string.string_categories_details, details.type, details.year,
            details.rated, details.runtime)
        genre.text = getString(R.string.string_genre_details, details.genre)
        desc.text = details.desc
        creator.text = getString(R.string.string_creator_details, details.creator)
        cast.text = getString(R.string.string_cast_details, details.cast)
        awards.text = getString(R.string.string_awards_details, details.awards)
        metascore.text = getString(R.string.string_metascore_ratings, details.metascore)
        imdbRating.text = getString(R.string.string_imdb_value, details.imdbRating)
        votes.text = details.votes

        ratingsList.clear()
        ratingsList.addAll(details.ratings)
        ratingsAdapter.notifyDataSetChanged()

        progressDetails.visibility = View.GONE
    }

    override fun onFailure(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        progressDetails.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}