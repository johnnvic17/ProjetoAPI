package com.projeto.praticaapi.util

import android.os.Handler
import android.os.Looper
import com.projeto.praticaapi.model.ItemModel
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class ItemTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onSuccess(items: List<ItemModel>)
        fun onFailure(error: String)
    }

    fun execute(url: String){
        executor.execute {

            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            var buffer: BufferedInputStream? = null

            try {
                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Item not found!")
                }

                stream = urlConnection.inputStream
                buffer = BufferedInputStream(stream)
                val json = jsonToString(buffer)
                val items = toItems(json)

                handler.post {
                    callback.onSuccess(items)
                }

            }catch (e: IOException){
                val message = e.message ?: "Item not found!"

                handler.post {
                    callback.onFailure(message)
                }

            } finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
        }
    }

    private fun jsonToString(stream: BufferedInputStream): String{

        val array = ByteArray(1024)
        var read: Int
        val baos = ByteArrayOutputStream()

        while (true){
            read = stream.read(array)

            if(read <= 0){
                break
            }
            baos.write(array, 0, read)
        }
        return String(baos.toByteArray())
    }

    private fun toItems(json: String): List<ItemModel>{

        if(!json.contains("Search")){
            throw IOException("Movie not found!")
        }

        val itemsList = mutableListOf<ItemModel>()

        val root = JSONObject(json)
        val movies = root.getJSONArray("Search")

        for(i in 0 until movies.length()){

            val item = movies.getJSONObject(i)
            val title = item.getString("Title")
            val year = item.getString("Year")
            val id = item.getString("imdbID")
            val poster = item.getString("Poster")

            itemsList.add(ItemModel(id, title, year, poster))
        }
        return itemsList
    }
}