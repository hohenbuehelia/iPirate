package com.example.ipirate
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.provider.AlarmClock
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_display_radarr.*
import kotlinx.android.synthetic.main.activity_display_sonarr.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.sql.DriverManager
import java.sql.SQLException

object OwnedSQLiteDB {
    object Radarr {
        const val TABLE_NAME = "Radarr"
        const val tmdbId = "TMDB_ID"
        const val title = "TITLE"
        const val overview = "OVERVIEW"
        const val posterUrl = "POSTER_URL"
        const val website = "WEBSITE"
        const val downloaded = "DOWNLOADED"
        const val year = "YEAR"
        const val runTime = "RUN_TIME"
        const val titleSlug = "TITLE_SLUG"
        const val rating = "RATING"
    }
    object Sonarr {
        const val TABLE_NAME = "Sonarr"
        const val tvdbId = "TVDB_ID"
        const val title = "TITLE"
        const val overview = "OVERVIEW"
        const val posterUrl = "POSTER_URL"
        const val seasonCount = "SEASON_COUNT"
        const val monitored = "MONITORED"
        const val year = "YEAR"
        const val runTime = "RUN_TIME"
        const val titleSlug = "TITLE_SLUG"
        const val rating = "RATING"
        const val status = "STATUS"
    }
}

const val createSonarr =
    "CREATE TABLE IF NOT EXISTS ${OwnedSQLiteDB.Sonarr.TABLE_NAME} (" +
            "${OwnedSQLiteDB.Sonarr.tvdbId} INTEGER PRIMARY KEY," +
            "${OwnedSQLiteDB.Sonarr.title} VARCHAR," +
            "${OwnedSQLiteDB.Sonarr.overview} VARCHAR," +
            "${OwnedSQLiteDB.Sonarr.posterUrl} VARCHAR," +
            "${OwnedSQLiteDB.Sonarr.seasonCount} INTEGER," +
            "${OwnedSQLiteDB.Sonarr.monitored} VARCHAR," +
            "${OwnedSQLiteDB.Sonarr.year} INTEGER," +
            "${OwnedSQLiteDB.Sonarr.runTime} INTEGER," +
            "${OwnedSQLiteDB.Sonarr.titleSlug} VARCHAR," +
            "${OwnedSQLiteDB.Sonarr.rating} FLOAT," +
            "${OwnedSQLiteDB.Sonarr.status} VARCHAR)"

const val deleteSonarr = "DROP TABLE IF EXISTS ${OwnedSQLiteDB.Sonarr.TABLE_NAME}"

const val createRadarr =
    "CREATE TABLE IF NOT EXISTS ${OwnedSQLiteDB.Radarr.TABLE_NAME} (" +
            "${OwnedSQLiteDB.Radarr.tmdbId} INTEGER PRIMARY KEY," +
            "${OwnedSQLiteDB.Radarr.title} VARCHAR," +
            "${OwnedSQLiteDB.Radarr.overview} VARCHAR," +
            "${OwnedSQLiteDB.Radarr.posterUrl} VARCHAR," +
            "${OwnedSQLiteDB.Radarr.website} VARCHAR," +
            "${OwnedSQLiteDB.Radarr.downloaded} VARCHAR," +
            "${OwnedSQLiteDB.Radarr.year} INTEGER," +
            "${OwnedSQLiteDB.Radarr.runTime} INTEGER," +
            "${OwnedSQLiteDB.Radarr.titleSlug} VARCHAR," +
            "${OwnedSQLiteDB.Radarr.rating} FLOAT)"

const val deleteRadarr = "DROP TABLE IF EXISTS ${OwnedSQLiteDB.Radarr.TABLE_NAME}"


class CreateNewDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createRadarr)
        db.execSQL(createSonarr)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(deleteRadarr)
        db.execSQL(deleteSonarr)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onDowngrade(db, oldVersion, newVersion)
    }
    companion object {
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "Owned.db"
    }
}

class DisplaySQLActivitySonarr : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        CreateNewDatabase(this)
        super.onCreate(savedInstanceState)
        setContentView((R.layout.activity_display_sonarr))
        recyclerViewSqlS.layoutManager = LinearLayoutManager(this)
        val input = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)
        if (input == "1") {
            readSonarr()
        } else {
            sonarrFullSQLUpdate()
        }
        val titles = mutableListOf<String>()
        val dates = mutableListOf<String>()
        val tvdbIds = mutableListOf<String>()
        val posterUrls = mutableListOf<String>()
        var count = 0
        for (i in showOutputMap) {
            count += 1
            titles.add(i["title"].toString())
            dates.add(i["release"].toString())
            tvdbIds.add(i["tvdbId"].toString())
            posterUrls.add(i["poster"].toString())
        }
        val adapter = RecyclerAdapterSQLS()
        recyclerViewSqlS.adapter = adapter
        adapter.posterUrls = posterUrls.toTypedArray()
        adapter.titles = titles.toTypedArray()
        adapter.dates = dates.toTypedArray()
        adapter.tvdbIds = tvdbIds.toTypedArray()
        adapter.notifyDataSetChanged()
    }

    private var showOutputMap = mutableListOf<Map<String, String>>()

    fun sonarrDailySQLUpdate() {
        val connectionUrl =
            "jdbc:sqlserver://73.14.117.191\\SQLEXPRESS:25022;databaseName=Owned;user=hohen;password=B619a84!"
        try {
            DriverManager.getConnection(connectionUrl).use { con ->
                con.createStatement().use { stmt ->
                    val sqlQuery = "SELECT * FROM dbo.NewSonarr"
                    val rs = stmt.executeQuery(sqlQuery)
                    while (rs.next()) {
                        val tvdbId = rs.getString("TVDB_ID").toInt()
                        val title = rs.getString("TITLE")
                        val overview = rs.getString("OVERVIEW")
                        val posterUrl = rs.getString("POSTER_URL")
                        val seasonCount = rs.getString("SEASON_COUNT").toInt()
                        var monitored = "Not Owned"
                        if (rs.getString("MONITORED").toInt() == 1) {
                            monitored = "Owned"
                        }
                        val year = rs.getString("YEAR").toInt()
                        val runTime = rs.getString("RUN_TIME").toInt()
                        val titleSlug = rs.getString("TITLE_SLUG")
                        val rating = (rs.getString("RATING")).toFloat()
                        val status = (rs.getString("STATUS"))
                        updateSonarrTable(
                            tvdbId,
                            title,
                            overview,
                            posterUrl,
                            seasonCount,
                            monitored,
                            year,
                            runTime,
                            titleSlug,
                            rating,
                            status
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun sonarrFullSQLUpdate() = runBlocking {
        withContext(Dispatchers.IO) {
            val connectionUrl =
                "jdbc:jtds:sqlserver://73.14.117.191:25022/Owned;instance=SQLEXPRESS;user=hohen;password=B619a84!"
            try {
                DriverManager.getConnection(connectionUrl).use { con ->
                    con.createStatement().use { stmt ->
                        val sqlQuery = "SELECT * FROM dbo.Sonarr"
                        val rs = stmt.executeQuery(sqlQuery)
                        while (rs.next()) {
                            val tvdbId = rs.getString("TVDB_ID").toInt()
                            val title = rs.getString("TITLE")
                            val overview = rs.getString("OVERVIEW")
                            val posterUrl = rs.getString("POSTER_URL")
                            val seasonCount = rs.getString("SEASON_COUNT").toInt()
                            var monitored = "Not Owned"
                            if (rs.getString("MONITORED").toInt() == 1) {
                                monitored = "Owned"
                            }
                            val year = rs.getString("YEAR").toInt()
                            val runTime = rs.getString("RUN_TIME").toInt()
                            val titleSlug = rs.getString("TITLE_SLUG")
                            val rating = (rs.getString("RATING")).toFloat()
                            val status = (rs.getString("STATUS"))
                            updateSonarrTable(
                                tvdbId,
                                title,
                                overview,
                                posterUrl,
                                seasonCount,
                                monitored,
                                year,
                                runTime,
                                titleSlug,
                                rating,
                                status
                            )
                        }
                    }
                }
            } 
            catch (e: SQLException) {
            }
        }
    }

    private fun updateSonarrTable(TVDB_ID: Int, TITLE: String, OVERVIEW: String, POSTER_URL: String,
                                  SEASON_COUNT: Int, MONITORED: String, YEAR: Int, RUN_TIME: Int,
                                  TITLE_SLUG: String, RATING: Float, STATUS: String) {
        val dbHelper = CreateNewDatabase(this)
        val db = dbHelper.writableDatabase
        db.execSQL(createSonarr)
        val values = ContentValues().apply{
            put(OwnedSQLiteDB.Sonarr.tvdbId, TVDB_ID)
            put(OwnedSQLiteDB.Sonarr.title, TITLE)
            put(OwnedSQLiteDB.Sonarr.overview, OVERVIEW)
            put(OwnedSQLiteDB.Sonarr.posterUrl, POSTER_URL)
            put(OwnedSQLiteDB.Sonarr.seasonCount, SEASON_COUNT)
            put(OwnedSQLiteDB.Sonarr.monitored, MONITORED)
            put(OwnedSQLiteDB.Sonarr.year, YEAR)
            put(OwnedSQLiteDB.Sonarr.runTime, RUN_TIME)
            put(OwnedSQLiteDB.Sonarr.titleSlug, TITLE_SLUG)
            put(OwnedSQLiteDB.Sonarr.rating, RATING)
            put(OwnedSQLiteDB.Sonarr.status, STATUS)
        }
        val newRowId = db?.insert(OwnedSQLiteDB.Sonarr.TABLE_NAME, null, values)
    }

    private fun readSonarr() {
        val dbHelper = CreateNewDatabase(this)
        val db = dbHelper.readableDatabase
        db.execSQL(createSonarr)
        val cursor = db.query(
            OwnedSQLiteDB.Sonarr.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndex(OwnedSQLiteDB.Sonarr.title))
                val tvdbId = getInt(getColumnIndex(OwnedSQLiteDB.Sonarr.tvdbId))
                val date = getInt(getColumnIndex(OwnedSQLiteDB.Sonarr.year))
                val poster = getString(getColumnIndex(OwnedSQLiteDB.Sonarr.posterUrl))
                showOutputMap.add(
                    mapOf(
                        "title" to title,
                        "release" to date.toString(),
                        "tvdbId" to tvdbId.toString(),
                        "poster" to poster
                    )
                )
            }
            cursor.close()
            db.close()
        }
    }
}


class DisplaySQLActivityRadarr : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView((R.layout.activity_display_radarr))
        recyclerViewSqlR.layoutManager = LinearLayoutManager(this)
        val input = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)
        if (input == "1") {
            readRadarr()
        }
        else {
            radarrFullSQLUpdate()
        }
        val titles = mutableListOf<String>()
        val releases = mutableListOf<String>()
        val tmdbIds = mutableListOf<String>()
        val posterUrls = mutableListOf<String>()
        var count = 0
        for (i in movieOutputMap) {
            count += 1
            titles.add(i["title"].toString())
            releases.add(i["release"].toString())
            tmdbIds.add(i["tmdbId"].toString())
            posterUrls.add(i["poster"].toString())
        }
        val adapter = RecyclerAdapterSQLR()
        recyclerViewSqlR.adapter = adapter
        adapter.posterUrls = posterUrls.toTypedArray()
        adapter.titles = titles.toTypedArray()
        adapter.dates = releases.toTypedArray()
        adapter.tmdbIds = tmdbIds.toTypedArray()
        adapter.notifyDataSetChanged()
    }

    private var movieOutputMap = mutableListOf<Map<String, String>>()

    fun radarrDailySQLUpdate() {
        val connectionUrl =
            "jdbc:sqlserver://73.14.117.191\\SQLEXPRESS:25022;databaseName=Owned;user=hohen;password=B619a84!"
        try {
            DriverManager.getConnection(connectionUrl).use { con ->
                con.createStatement().use { stmt ->
                    val sqlQuery = "SELECT * FROM dbo.NewRadarr"
                    val rs = stmt.executeQuery(sqlQuery)
                    while (rs.next()) {
                        val tmdbId = rs.getString("TMDB_ID").toInt()
                        val title = rs.getString("TITLE")
                        val overview = rs.getString("OVERVIEW")
                        val posterUrl = rs.getString("POSTER_URL")
                        val website = rs.getString("website")
                        var downloaded = "Not Owned"
                        if (rs.getString("DOWNLOADED").toInt() == 1) {
                            downloaded = "Owned"
                        }
                        val year = rs.getString("YEAR").toInt()
                        val runTime = rs.getString("RUN_TIME").toInt()
                        val titleSlug = rs.getString("TITLE_SLUG")
                        val rating = (rs.getString("RATING")).toFloat()
                        // updateRadarrTable(tmdbId, title, overview, posterUrl, website, downloaded, year, runTime, titleSlug, rating)
                    }
                }
            }
        }
        catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun radarrFullSQLUpdate() = runBlocking {
        withContext(Dispatchers.IO) {
            val connectionUrl =
                "jdbc:jtds:sqlserver://73.14.117.191:25022/Owned;instance=SQLEXPRESS;user=hohen;password=B619a84!"
            try {
                DriverManager.getConnection(connectionUrl).use { con ->
                    con.createStatement().use { stmt ->
                        val sqlQuery = "SELECT * FROM dbo.Radarr"
                        val rs = stmt.executeQuery(sqlQuery)
                        while (rs.next()) {
                            val tmdbId = rs.getString("TMDB_ID").toInt()
                            val title = rs.getString("TITLE")
                            val overview = rs.getString("OVERVIEW")
                            val posterUrl = rs.getString("POSTER_URL")
                            val website = rs.getString("website")
                            var downloaded = "Not Owned"
                            if (rs.getString("DOWNLOADED").toInt() == 1) {
                                downloaded = "Owned"
                            }
                            val year = rs.getString("YEAR").toInt()
                            val runTime = rs.getString("RUN_TIME").toInt()
                            val titleSlug = rs.getString("TITLE_SLUG")
                            val rating = (rs.getString("RATING")).toFloat()
                            updateRadarrTable(
                                tmdbId,
                                title,
                                overview,
                                posterUrl,
                                website,
                                downloaded,
                                year,
                                runTime,
                                titleSlug,
                                rating
                            )

                        }
                    }
                }
            }
            catch (e: SQLException) {
            }
        }
    }

    private fun updateRadarrTable(TMDB_ID: Int, TITLE: String, OVERVIEW: String, POSTER_URL: String, WEBSITE: String,
                                  DOWNLOADED: String, YEAR: Int, RUN_TIME: Int, TITLE_SLUG: String, RATING: Float) {
        val dbHelper = CreateNewDatabase(this)
        val db = dbHelper.writableDatabase
        db.execSQL(createRadarr)
        val values = ContentValues().apply{
            put(OwnedSQLiteDB.Radarr.tmdbId, TMDB_ID)
            put(OwnedSQLiteDB.Radarr.title, TITLE)
            put(OwnedSQLiteDB.Radarr.overview, OVERVIEW)
            put(OwnedSQLiteDB.Radarr.posterUrl, POSTER_URL)
            put(OwnedSQLiteDB.Radarr.website, WEBSITE)
            put(OwnedSQLiteDB.Radarr.downloaded, DOWNLOADED)
            put(OwnedSQLiteDB.Radarr.year, YEAR)
            put(OwnedSQLiteDB.Radarr.runTime, RUN_TIME)
            put(OwnedSQLiteDB.Radarr.titleSlug, TITLE_SLUG)
            put(OwnedSQLiteDB.Radarr.rating, RATING)
        }
        val newRowId = db?.insert(OwnedSQLiteDB.Radarr.TABLE_NAME, null, values)
    }

    private fun readRadarr() {
        val dbHelper = CreateNewDatabase(this)
        val db = dbHelper.readableDatabase
        db.execSQL(createRadarr)
        val cursor = db.query(
            OwnedSQLiteDB.Radarr.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndex(OwnedSQLiteDB.Radarr.title))
                val tmdbId = getInt(getColumnIndex(OwnedSQLiteDB.Radarr.tmdbId))
                val date = getInt(getColumnIndex(OwnedSQLiteDB.Radarr.year))
                val poster = getString(getColumnIndex(OwnedSQLiteDB.Radarr.posterUrl))
                movieOutputMap.add(
                    mapOf(
                        "title" to title,
                        "release" to date.toString(),
                        "tmdbId" to tmdbId.toString(),
                        "poster" to poster
                    )
                )
            }
            cursor.close()
            db.close()
        }
    }
}


