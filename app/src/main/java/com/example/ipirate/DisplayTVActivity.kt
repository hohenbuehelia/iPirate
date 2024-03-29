package com.example.ipirate

import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.AlarmClock
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_display_movies.*
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.FileNotFoundException
import java.io.OutputStreamWriter

data class TVTMDBData(val name: String, val id: Int, val first_air_date: String,
                    val vote_average: Float,val overview: String, val poster_path: String)
data class TVTMDBExternalIDs(val imdb_id: String, val tvdb_id: String)
data class TMDBDetailed(val number_of_seasons: Int, val number_of_episodes: Int)
data class SonarrData(val title: String, val titleSlug: String, val images: JsonArray, val seasons: JsonArray)
data class SonarrPost(val tvdbId: Int, val title: String, val qualityProfileId: Int, val titleSlug: String, val images: JsonArray,
                      val seasons: JsonArray, val rootFolderPath: String, val monitored: Boolean, val addOptions: JsonObject
)


class DisplayTVActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_tvshows)
        recyclerView.layoutManager = LinearLayoutManager(this)
        input = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)!!
        // do multi / single if statement here and just add backup - getTVShow() / getTVShowMultiPage()
        getTVShow()
        val dbHelper = CreateNewDatabase(this)
        val db = dbHelper.readableDatabase
        db.execSQL(createSonarr)
        val projection = arrayOf(OwnedSQLiteDB.Sonarr.tvdbId)
        val selection = "${OwnedSQLiteDB.Sonarr.tvdbId} = ?"
        val listofowned = mutableListOf<String>()
        val descriptions = mutableListOf<String>()
        val titles = mutableListOf<String>()
        val releases = mutableListOf<String>()
        val ratings = mutableListOf<String>()
        val tmdbIds = mutableListOf<String>()
        val imdbIds = mutableListOf<String>()
        val tvdbIds = mutableListOf<String>()
        val seasons = mutableListOf<String>()
        val episodes = mutableListOf<String>()
        val posters = mutableListOf<String>()
        var count = 0
        for (i in tvOutputMap) {
            count += 1
            var owned = false
            val selectionArgs = arrayOf(i["tvdbId"].toString())
            val cursor = db.query(
                OwnedSQLiteDB.Sonarr.TABLE_NAME,
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
            titles.add(i["name"].toString())
            releases.add(i["first_air_date"].toString())
            ratings.add(i["rating"].toString())
            tmdbIds.add(i["tmdbId"].toString())
            imdbIds.add(i["imdbId"].toString())
            tvdbIds.add(i["tvdbId"].toString())
            seasons.add(i["seasons"].toString())
            episodes.add(i["episodes"].toString())
            posters.add(i["poster"].toString())
        }
        val adapter = RecyclerAdapterTV()
        recyclerView.adapter = adapter
        adapter.posterUrls = posters.toTypedArray()
        adapter.titles = titles.toTypedArray()
        adapter.dates = releases.toTypedArray()
        adapter.tvdbIds = tvdbIds.toTypedArray()
        adapter.listofowned = listofowned.toTypedArray()
        adapter.notifyDataSetChanged()
    }


    private var input = ""
    private var tvOutputString = mutableListOf<String>()
    private var tvOutputMap = mutableListOf<Map<String, String>>()

    private fun getTVShow() = runBlocking {
        withContext(Dispatchers.IO) {
            val url =
                URL("https://api.themoviedb.org/3/search/tv?api_key=${Globals.tmdbAPI}&query=$input")
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
                        val url2 =
                            URL("https://api.themoviedb.org/3/search/tv?api_key=${Globals.tmdbAPI}&page=$pagecount&query=$input")
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
                        val url2 =
                            URL("https://api.themoviedb.org/3/search/tv?api_key=${Globals.tmdbAPI}&page=$pagecount&query=$input")
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
                        val tvData: TVTMDBData = gson.fromJson(i, TVTMDBData::class.java)
                        val tmdbIdStr = tvData.id.toString()
                        var imdbId = ""
                        var tvdbId = ""

                        try {
                            val newUrl =
                                URL("https://api.themoviedb.org/3/tv/$tmdbIdStr/external_ids?api_key=${Globals.tmdbAPI}")
                            with(newUrl.openConnection() as HttpURLConnection) {
                                requestMethod = "GET"
                                newDirtyJson = inputStream.bufferedReader().use {
                                    it.readText()
                                }
                            }
                            val externals: TVTMDBExternalIDs =
                                gson.fromJson(newDirtyJson, TVTMDBExternalIDs::class.java)
                            imdbId = externals.imdb_id
                            tvdbId = externals.tvdb_id
                        } catch (e: FileNotFoundException) {
                        }
                        var seasons = ""
                        var episodes = ""
                        try {
                            val newUrl2 =
                                URL("https://api.themoviedb.org/3/tv/$tmdbIdStr?api_key=${Globals.tmdbAPI}&language=en-US")
                            with(newUrl2.openConnection() as HttpURLConnection) {
                                requestMethod = "GET"
                                newDirtyJson = inputStream.bufferedReader().use {
                                    it.readText()
                                }
                            }
                            val detailed: TMDBDetailed =
                                gson.fromJson(newDirtyJson, TMDBDetailed::class.java)
                            seasons = detailed.number_of_seasons.toString()
                            episodes = detailed.number_of_episodes.toString()
                        } catch (e: FileNotFoundException) {
                        }

                        val title = tvData.name
                        val release = tvData.first_air_date
                        val rating = tvData.vote_average.toString()
                        val tmdbId = tvData.id.toString()
                        val description = tvData.overview
                        val poster = tvData.poster_path
                        tvOutputMap.add(
                            mapOf(
                                "name" to title, "first_air_date" to release, "rating" to rating,
                                "tmdbId" to tmdbId, "imdbId" to imdbId, "tvdbId" to tvdbId,
                                "episodes" to episodes, "seasons" to seasons,
                                "description" to description, "poster" to poster
                            )
                        )
                        tvOutputString.add(
                            "$title \n Released: $release \n Rating: $rating \n TMDB ID: $tmdbId " +
                                    "\n IMDB ID: $imdbId TVDB ID: $tvdbId \n Seasons: $seasons \n Episodes: $episodes " +
                                    "\n $description \n \n"
                        )
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun postShow(view: View) = runBlocking {
        withContext(Dispatchers.IO) {
            val textView = findViewById<TextView>(R.id.tvTvdbId)
            val searchNum = textView.text
            val gson = Gson()
            val url =
                URL("${Globals.homeIP}${Globals.sonarrPort}/api/series/lookup?term=tvdb:$searchNum&apikey=${Globals.sonarrAPI}")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                val dirtyJson = inputStream.bufferedReader().use {
                    it.readText()
                }
                if (dirtyJson == "[]") {
                    val tval = "Failed - TVDB ID Error"
                    val tdur = Toast.LENGTH_SHORT
                    runOnUiThread {
                        run {
                            Toast.makeText(this@DisplayTVActivity, tval, tdur).show()
                        }
                    }
                    return@withContext
                }
                val fixJson: JsonArray = gson.fromJson(dirtyJson, JsonArray::class.java)
                val cleanJson: SonarrData = gson.fromJson(fixJson[0], SonarrData::class.java)
                val addOptionsString =
                    "{ \"ignoreEpisodesWithFiles\": true, \"ignoreEpisodesWithoutFiles\": false, \"searchForMissingEpisodes\": true}"
                val addOptions = JsonParser().parse(addOptionsString).asJsonObject
                val postInfo = SonarrPost(searchNum.toString().toInt(), cleanJson.title, 6, cleanJson.titleSlug, cleanJson.images, cleanJson.seasons, "d:\\TV Shows\\", true, addOptions)
                val data = gson.toJson(postInfo)
                val posturl =
                    URL("${Globals.homeIP}${Globals.sonarrPort}/api/series?apikey=${Globals.sonarrAPI}")
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
                                Toast.makeText(this@DisplayTVActivity, tval, tdur).show()
                            }
                        }
                    }
                    else {
                        val tval = "Failed - $response"
                        val tdur = Toast.LENGTH_SHORT
                        runOnUiThread {
                            run {
                                Toast.makeText(this@DisplayTVActivity, tval, tdur).show()
                            }
                        }
                    }
                }
            }
        }
    }

    fun openIndividualTV(view: View) {
        val textView1 = findViewById<TextView>(R.id.tvTvdbId)
        val tvdbId = textView1.text.toString()
        val message = mutableListOf<Map<String, String>>()
        for (i in tvOutputMap) {
            if (i["tvdbId"] == tvdbId) {
                message.add(i)
            }
        }
        val len = message.size
        val messageList = arrayListOf(message[len - 1]["name"].toString(),
            message[len - 1]["tvdbId"].toString(), message[len - 1]["first_air_date"].toString(),
            message[len - 1]["rating"].toString(), message[len - 1]["description"].toString())
        val intent = Intent(this, IndividualTVDisplay::class.java).apply {
            putStringArrayListExtra(AlarmClock.EXTRA_MESSAGE, messageList)
        }
        startActivity(intent)
    }
}
