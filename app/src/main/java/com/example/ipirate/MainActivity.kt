package com.example.ipirate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText

object Globals {
    var tmdbAPI = ""
    var radarrAPI = ""
    var sonarrAPI = ""
    var homeIP = ""
    var radarrPort = ""
    var sonarrPort = ""
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))
        if (SharedPreferences(this).getValueString("tmdbAPI") != null) {
            Globals.tmdbAPI = SharedPreferences(this).getValueString("tmdbAPI")!!
        }
        if (SharedPreferences(this).getValueString("radarrAPI") != null) {
            Globals.radarrAPI = SharedPreferences(this).getValueString("radarrAPI")!!
        }
        if (SharedPreferences(this).getValueString("sonarrAPI") != null) {
            Globals.sonarrAPI = SharedPreferences(this).getValueString("sonarrAPI")!!
        }
        if (SharedPreferences(this).getValueString("homeIP") != null) {
            Globals.homeIP = SharedPreferences(this).getValueString("homeIP")!!
        }
        if (SharedPreferences(this).getValueString("radarrPort") != null) {
            Globals.radarrPort = SharedPreferences(this).getValueString("radarrPort")!!
        }
        if (SharedPreferences(this).getValueString("sonarrPort") != null) {
            Globals.sonarrPort = SharedPreferences(this).getValueString("sonarrPort")!!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun sendTVGet(view: View) {
        val tveditText = findViewById<EditText>(R.id.tveditText)
        val message = tveditText.text.toString()
        val intent = Intent(this, DisplayTVActivity::class.java).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }

    fun sendMovieGet(view: View) {
        val editText = findViewById<EditText>(R.id.editText)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayMoviesActivity::class.java).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }
}
