package com.projeto.praticaapi.util

import android.os.Handler
import android.os.Looper
import com.projeto.praticaapi.model.ItemDetailsModel
import com.projeto.praticaapi.model.RatingsModel
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DetailsTask(private val callback: Callback) {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    interface Callback{
        fun onLoading()
        fun onResult(details: ItemDetailsModel)
        fun onFailure(error: String)
    }

    fun execute(url: String){
        executor.execute {
            callback.onLoading()

            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            var buffer: BufferedInputStream? = null

            try{

              val responseURL = URL(url)
              urlConnection = responseURL.openConnection() as HttpsURLConnection
              urlConnection.readTimeout = 2000
              urlConnection.connectTimeout = 2000

              val statusCode = urlConnection.responseCode
              if(statusCode > 400){
                  throw IOException("Invalid ID or Value.")
              }

              stream = urlConnection.inputStream
              buffer = BufferedInputStream(stream)
              val json = jsonToString(buffer)
              val details = toDetails(json)

              handler.post {
                  callback.onResult(details)
              }

            }catch (e: IOException){
                val message = e.message ?: "Invalid ID or Value."

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

    private fun jsonToString(stream: InputStream): String{

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

    private fun toDetails(json: String): ItemDetailsModel{

        val root = JSONObject(json)

        val title = root.getString("Title")
        val year = root.getString("Year")
        val rated = root.getString("Rated")
        val released = root.getString("Released")
        val runtime = root.getString("Runtime")
        val genre = root.getString("Genre")
        val writer = root.getString("Writer")
        val actors = root.getString("Actors")
        val plot = root.getString("Plot")
        val awards = root.getString("Awards")
        val poster = root.getString("Poster")
        val ratings = root.getJSONArray("Ratings")

        val ratingsList = mutableListOf<RatingsModel>()

        for(i in 0 until ratings.length()){
            val rating = ratings.getJSONObject(i)

            val ratingTitle = rating.getString("Source")
            val ratingValue = rating.getString("Value")

            ratingsList.add(RatingsModel(ratingTitle, ratingValue))
        }

        val metascore = root.getString("Metascore")
        val imdbRating = root.getString("imdbRating")
        val votes = root.getString("imdbVotes")
        val type = root.getString("Type")

        val details = ItemDetailsModel(poster, title, released, type, year, rated, runtime, genre,
            plot, writer, actors, awards, metascore, imdbRating, votes, ratingsList)

        return details
    }
}