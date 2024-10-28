package com.projeto.praticaapi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projeto.praticaapi.adapter.MainAdapter
import com.projeto.praticaapi.model.ItemModel
import com.projeto.praticaapi.util.ItemTask

class MainActivity : AppCompatActivity(), ItemTask.Callback {
    private lateinit var progressMain: ProgressBar
    private var items = mutableListOf<ItemModel>()
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        progressMain = findViewById(R.id.progress_main)
        progressMain.visibility = View.GONE

        val response: EditText = findViewById(R.id.edit_search)
        val btn: ImageView = findViewById(R.id.btn_search_movie)
        btn.setOnClickListener {

            if(response.text.toString().isEmpty()){
                Toast.makeText(this, R.string.validation_field_string, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            progressMain.visibility = View.VISIBLE

            val item = response.text.toString()
            val url = "https://www.omdbapi.com/?apikey=162ae16a&s=$item"
            ItemTask(this).execute(url)

            val service = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            service.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        adapter = MainAdapter(items) { id ->
            val i = Intent(this, ItemDetailsActivity::class.java)
            i.putExtra("id", id)
            startActivity(i)
        }
        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = GridLayoutManager(this, 2)
        rv.adapter = adapter
    }

    override fun onSuccess(items: List<ItemModel>) {
        this.items.clear()
        this.items.addAll(items)
        adapter.notifyDataSetChanged()

        progressMain.visibility = View.GONE
    }

    override fun onFailure(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        progressMain.visibility = View.GONE
    }
}