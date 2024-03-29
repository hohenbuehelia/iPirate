package com.example.ipirate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_display_movies.*
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.FileNotFoundException
import java.io.OutputStreamWriter

data class TMDBmultipage(val total_pages: Int)
data class TMDBClean(val results: JsonArray)
data class TMDBData(val title: String, val id: Int, val release_date: String,
                    val vote_average: Float,val overview: String, val poster_path: String)
data class TMDBExternalIDs(val imdb_id: String)
data class RadarrData(val title: String, val titleSlug: String, val images: JsonArray, val year: Int)
data class RadarrPost(val title: String, val qualityProfileId: Int, val titleSlug: String, val images: JsonArray,
                      val tmdbId: Int, val year: Int, val rootFolderPath: String, val monitored: Boolean, val addOptions: JsonObject)

class DisplayMoviesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_movies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // do if statement here if not using drop down
        input = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)!!
        // do if statement here if you can figure it out :-)
        getMovie()
        val dbHelper = CreateNewDatabase(this)
        val db = dbHelper.readableDatabase
        db.execSQL(createRadarr)
        val projection = arrayOf(OwnedSQLiteDB.Radarr.tmdbId)
        val selection = "${OwnedSQLiteDB.Radarr.tmdbId} = ?"
        val listofowned = mutableListOf<String>()
        val descriptions = mutableListOf<String>()
        val titles = mutableListOf<String>()
        val releases = mutableListOf<String>()
        val ratings = mutableListOf<String>()
        val tmdbIds = mutableListOf<String>()
        val imdbIds = mutableListOf<String>()
        val posters = mutableListOf<String>()
        var count = 0
        for (i in movieOutputMap) {
            count += 1
            var owned = false
            val selectionArgs = arrayOf(i["tmdbId"].toString())
            val cursor = db.query(
                OwnedSQLiteDB.Radarr.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            with(cursor) {
                if (moveToNext()) {
                    owned = true
                }
                cursor.close()
            }
            listofowned.add(owned.toString())
            descriptions.add(i["description"].toString())
            titles.add(i["title"].toString())
            releases.add(i["release"].toString())
            ratings.add(i["rating"].toString())
            tmdbIds.add(i["tmdbId"].toString())
            imdbIds.add(i["imdbId"].toString())
            posters.add(i["poster"].toString())
        }
        val adapter = RecyclerAdapter()
        recyclerView.adapter = adapter
        adapter.posterUrls = posters.toTypedArray()
        adapter.titles = titles.toTypedArray()
        adapter.dates = releases.toTypedArray()
        adapter.tmdbIds = tmdbIds.toTypedArray()
        adapter.listofowned = listofowned.toTypedArray()
        db.close()
        adapter.notifyDataSetChanged()
    }

    private var input = ""
    private var movieOutputString = mutableListOf<String>()
    private var movieOutputMap = mutableListOf<Map<String, String>>()

    private fun getMovie() = runBlocking {
        withContext(Dispatchers.IO) {
            val url =
                URL("https://api.themoviedb.org/3/search/movie?api_key=${Globals.tmdbAPI}&query=$input")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                val dirtyJson = inputStream.bufferedReader().use {
                    it.readText()
                }
                val gson = Gson()
                val dirtypages: TMDBmultipage = gson.fromJson(dirtyJson, TMDBmultipage::class.java)
                var pages = dirtypages.total_pages
                val dirtymultipages = mutableListOf<String>()
                val multipages = mutableListOf<JsonArray>()
                if (pages >= 4) {
                    var pagecount = 1
                    while (pages > 3) {
                        if (pagecount == 2) {
                            break
                        }
                        val url2 = URL("https://api.themoviedb.org/3/search/movie?api_key=${Globals.tmdbAPI}&page=$pagecount&query=$input")
                        with(url2.openConnection() as HttpURLConnection) {
                            requestMethod = "GET"
                            val dirtyJsonString = inputStream.bufferedReader().use {
                                it.readText()
                            }
                            dirtymultipages.add(dirtyJsonString)
                            pages -= 1
                            pagecount += 1
                        }
                    }
                    for (i in dirtymultipages) {
                        val cleaningresult: TMDBClean = gson.fromJson(i, TMDBClean::class.java)
                        val cleanlist = cleaningresult.results
                        multipages.add(cleanlist)
                    }
                }
                else {
                    var pagecount = 1
                    while (pages != 0) {
                        val url2 = URL("https://api.themoviedb.org/3/search/movie?api_key=${Globals.tmdbAPI}&language=en-US&page=$pagecount&query=$input")
                        with(url2.openConnection() as HttpURLConnection) {
                            requestMethod = "GET"
                            val dirtyJsonString = inputStream.bufferedReader().use {
                                it.readText()
                            }
                            dirtymultipages.add(dirtyJsonString)
                            pages -= 1
                            pagecount += 1
                        }
                        for (i in dirtymultipages) {
                            val cleaningresult: TMDBClean = gson.fromJson(i, TMDBClean::class.java)
                            val cleanlist = cleaningresult.results
                            multipages.add(cleanlist)
                        }
                    }
                }
                for (n in multipages) {
                    for (i in n) {
                        var newDirtyJson = ""
                        val movieData: TMDBData = gson.fromJson(i, TMDBData::class.java)
                        val tmdbIdStr = movieData.id.toString()
                        var imdbId = ""
                        try {
                            val newUrl =
                                URL("https://api.themoviedb.org/3/movie/$tmdbIdStr/external_ids?api_key=${Globals.tmdbAPI}&language=en-US")
                            with(newUrl.openConnection() as HttpURLConnection) {
                                requestMethod = "GET"
                                newDirtyJson = inputStream.bufferedReader().use {
                                    it.readText()
                                }
                            }
                            val externals: TMDBExternalIDs =
                                gson.fromJson(newDirtyJson, TMDBExternalIDs::class.java)
                            imdbId = externals.imdb_id
                        } catch (e: FileNotFoundException) {}
                        val tmdbId = movieData.id.toString()
                        val title = movieData.title
                        val release = movieData.release_date
                        val rating = movieData.vote_average.toString()
                        val description = movieData.overview
                        val poster =
                            movieData.poster_path
                        movieOutputMap.add(
                            mapOf(
                                "title" to title,
                                "release" to release,
                                "rating" to rating,
                                "tmdbId" to tmdbId,
                                "imdbId" to imdbId,
                                "description" to description,
                                "poster" to poster
                            )
                        )
                        movieOutputString.add("$title \n Released: $release \n Rating: $rating \n TMDB ID: $tmdbId \n IMDB ID: $imdbId \n $description \n \n")
                    }
                }
            }
        }
    }
    fun postMovie(view: View) = runBlocking {
        withContext(Dispatchers.IO) {
            val textView = findViewById<TextView>(R.id.movieTmdbId)
            val searchNum = textView.text
            val gson = Gson()
            val url =
                URL("${Globals.homeIP}${Globals.radarrPort}/api/movie/lookup/tmdb?tmdbId=$searchNum&apikey=${Globals.radarrAPI}")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                val dirtyJson = inputStream.bufferedReader().use {
                    it.readText()
                }
                val cleanjson: RadarrData = gson.fromJson(dirtyJson, RadarrData::class.java)
                val addOptionsString = "{ \"searchForMovie\": true}"
                val addOptions = JsonParser().parse(addOptionsString).asJsonObject
                val postInfo = RadarrPost(cleanjson.title, 4, cleanjson.titleSlug, cleanjson.images, searchNum.toString().toInt(), cleanjson.year, "d:\\Movies\\", true, addOptions)
                val data = gson.toJson(postInfo)
                val posturl =
                    URL("${Globals.homeIP}${Globals.radarrPort}/api/movie?apikey=${Globals.radarrAPI}")
                with(posturl.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    val outputWriter = OutputStreamWriter(outputStream)
                    outputWriter.write(data)
                    outputWriter.flush()
                    val response = responseCode
                    if (response == 201) {
                        val tval = "Success!"
                        val tdur = Toast.LENGTH_SHORT
                        runOnUiThread {
                            run {
                                Toast.makeText(this@DisplayMoviesActivity, tval, tdur).show()
                            }
                        }
                    }
                    else {
                        val tval = "Failed - $response"
                        val tdur = Toast.LENGTH_SHORT
                        runOnUiThread {
                            run {
                                Toast.makeText(this@DisplayMoviesActivity, tval, tdur).show()
                            }
                        }
                    }
                }
            }
        }
    }

    fun openIndividualMovie(view: View) {
        val textView1 = findViewById<TextView>(R.id.movieTmdbId)
        val tmdbId = textView1.text.toString()
        val message = mutableListOf<Map<String, String>>()
        for (i in movieOutputMap) {
            if (i["tmdbId"] == tmdbId) {
                message.add(i)
            }
        }
        val len = message.size
        val messageList = arrayListOf(message[len - 1]["title"].toString(),
            message[len - 1]["tmdbId"].toString(), message[len - 1]["release"].toString(),
            message[len - 1]["rating"].toString(), message[len - 1]["description"].toString())
        val intent = Intent(this, IndividualMovieDisplay::class.java).apply {
            putStringArrayListExtra(AlarmClock.EXTRA_MESSAGE, messageList)
        }
        startActivity(intent)
    }
}

