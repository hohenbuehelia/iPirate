package com.example.ipirate

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        if (isNullorEmpty(Globals.tmdbAPI)) {
            val tmdb = "Enter TMDB API Key"
            tmdbAPIText.hint = tmdb
        }
        else {
            val tmdb = getString(R.string.tmdb_api_key, Globals.tmdbAPI)
            tmdbAPIText.hint = tmdb
        }
        if (isNullorEmpty(Globals.radarrAPI)) {
            val radarr = "Enter Radarr API Key"
            radarrAPIText.hint = radarr
        }
        else {
            val radarr = getString(R.string.radarr_api_key, Globals.radarrAPI)
            radarrAPIText.hint = radarr
        }
        if (isNullorEmpty(Globals.sonarrAPI)) {
            val sonarr = "Enter Sonarr API Key"
            sonarrAPIText.hint = sonarr
        }
        else {
            val sonarr = getString(R.string.sonarr_api_key, Globals.sonarrAPI)
            sonarrAPIText.hint = sonarr
        }
        if (isNullorEmpty(Globals.homeIP)) {
            val homeip = "Enter home IP Address"
            homeIPText.hint = homeip
        }
        else {
            val homeip = getString(R.string.home_ip, Globals.homeIP)
            homeIPText.hint = homeip
        }
        if (isNullorEmpty(Globals.radarrPort)) {
            val radarrport = "Enter Radarr Port"
            radarrPort.hint = radarrport
        }
        else {
            val radarrport = getString(R.string.radarr_port, Globals.radarrPort)
            radarrPort.hint = radarrport
        }
        if (isNullorEmpty(Globals.sonarrPort)) {
            val sonarrport = "Enter Sonarr Port"
            sonarrPort.hint = sonarrport
        }
        else {
            val sonarrport = getString(R.string.sonarr_port, Globals.sonarrPort)
            sonarrPort.hint = sonarrport
        }
        if (isNullorEmpty(Globals.sqlPort)) {
            val sqlport = "Enter SQL Port"
            sqlPort.hint = sqlport
        }
        else {
            val sqlport = getString(R.string.sql_port, Globals.sqlPort)
            sqlPort.hint = sqlport
        }
        if (isNullorEmpty(Globals.sqlUser)) {
            val sqluser = "Enter SQL User"
            sqlUser.hint = sqluser
        }
        else {
            val sqluser = getString(R.string.sql_user, Globals.sqlUser)
            sqlUser.hint = sqluser
        }
        if (isNullorEmpty(Globals.sqlPass)) {
            val sqlpass = "Enter SQL Pass"
            sqlPass.hint = sqlpass
        }
        else {
            val sqlpass = getString(R.string.sql_pass, Globals.sqlPass)
            sqlPass.hint = sqlpass
        }
    }

    private fun isNullorEmpty(str: String?): Boolean {
        if (str != null && str.isNotEmpty())
            return false
        return true

    }

    fun saveSettings(view: View) {
        if (isNullorEmpty(tmdbAPIText.text.toString())) {
            Globals.tmdbAPI
        }
        else {
            val editText = findViewById<EditText>(R.id.tmdbAPIText)
            val message = editText.text.toString()
            Globals.tmdbAPI = message
            SharedPreferences(this).save("tmdbAPI", message)
        }
        if (isNullorEmpty(radarrAPIText.text.toString())) {
            Globals.radarrAPI
        }
        else {
            val editText = findViewById<EditText>(R.id.radarrAPIText)
            val message = editText.text.toString()
            Globals.radarrAPI = message
            SharedPreferences(this).save("radarrAPI", message)
        }
        if (isNullorEmpty(sonarrAPIText.text.toString())) {
            Globals.sonarrAPI
        }
        else {
            val editText = findViewById<EditText>(R.id.sonarrAPIText)
            val message = editText.text.toString()
            Globals.sonarrAPI = message
            SharedPreferences(this).save("sonarrAPI", message)
        }
        if (isNullorEmpty(homeIPText.text.toString())) {
            Globals.homeIP
        }
        else {
            val editText = findViewById<EditText>(R.id.homeIPText)
            val message = editText.text.toString()
            Globals.homeIP = message
            SharedPreferences(this).save("homeIP", message)
        }
        if (isNullorEmpty(radarrPort.text.toString())) {
            Globals.radarrPort
        }
        else {
            val editText = findViewById<EditText>(R.id.radarrPort)
            val message = editText.text.toString()
            Globals.radarrPort = message
            SharedPreferences(this).save("radarrPort", message)
        }
        if (isNullorEmpty(sonarrPort.text.toString())) {
            Globals.sonarrPort
        }
        else {
            val editText = findViewById<EditText>(R.id.sonarrPort)
            val message = editText.text.toString()
            Globals.sonarrPort = message
            SharedPreferences(this).save("sonarrPort", message)
        }
        if (isNullorEmpty(sqlPort.text.toString())) {
            Globals.sqlPort
        }
        else {
            val editText = findViewById<EditText>(R.id.sqlPort)
            val message = editText.text.toString()
            Globals.sqlPort = message
            SharedPreferences(this).save("sqlPort", message)
        }
        if (isNullorEmpty(sqlUser.text.toString())) {
            Globals.sqlUser
        }
        else {
            val editText = findViewById<EditText>(R.id.sqlUser)
            val message = editText.text.toString()
            Globals.sqlUser = message
            SharedPreferences(this).save("sqlUser", message)
        }
        if (isNullorEmpty(sqlPass.text.toString())) {
            Globals.sqlPass
        }
        else {
            val editText = findViewById<EditText>(R.id.sqlPass)
            val message = editText.text.toString()
            Globals.sqlPass = message
            SharedPreferences(this).save("sqlPass", message)
        }
        runOnUiThread {
            run {
                Toast.makeText(this@SettingsActivity, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}