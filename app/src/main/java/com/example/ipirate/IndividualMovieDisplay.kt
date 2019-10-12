package com.example.ipirate

import android.os.Bundle
import android.provider.AlarmClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class IndividualMovieDisplay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_movies)
        val movieInfo = intent.getStringArrayListExtra(AlarmClock.EXTRA_MESSAGE)!!
        val titleTextView = findViewById<TextView>(R.id.movieTitleText)
        titleTextView.text = movieInfo[0]
        val descTextView = findViewById<TextView>(R.id.movieDescText)
        descTextView.text = movieInfo[4]
        val dateTextView = findViewById<TextView>(R.id.movieReleaseDateText)
        val dateSt = getString(R.string.released, movieInfo[2])
        dateTextView.text = dateSt
        val rateTextView = findViewById<TextView>(R.id.movieRate)
        val rateSt = getString(R.string.rating, movieInfo[3])
        rateTextView.text = rateSt
    }
}