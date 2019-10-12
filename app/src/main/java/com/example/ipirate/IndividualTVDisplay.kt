package com.example.ipirate

import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class IndividualTVDisplay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_tv)
        val tvInfo = intent.getStringArrayListExtra(AlarmClock.EXTRA_MESSAGE)!!
        val titleTextView = findViewById<TextView>(R.id.tvTitleText)
        titleTextView.text = tvInfo[0]
        Log.d("FUUUUUU", tvInfo[0].toString() + "IndividualTVDisplay1")
        val descTextView = findViewById<TextView>(R.id.tvDescText)
        descTextView.text = tvInfo[4]
        val dateTextView = findViewById<TextView>(R.id.tvReleaseDateText)
        Log.d("FUUUUUU", tvInfo[2].toString() + "IndividualTVDisplay2")
        val dateSt = getString(R.string.released, tvInfo[2])
        dateTextView.text = dateSt
        val rateTextView = findViewById<TextView>(R.id.tvRate)
        val rateSt = getString(R.string.rating, tvInfo[3])
        rateTextView.text = rateSt
    }
}