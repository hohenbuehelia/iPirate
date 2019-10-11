package com.example.ipirate

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerAdapterSQLR : RecyclerView.Adapter<PhotoHolderSQLR>() {
    var posterUrls: Array<String> = arrayOf()
    var titles: Array<String> = arrayOf()
    var dates: Array<String> = arrayOf()
    var tmdbIds: Array<String> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolderSQLR {
        val imageItem = LayoutInflater.from(parent.context).inflate(R.layout.activity_display_radarr, parent, false)
        return PhotoHolderSQLR(imageItem)
    }

    override fun getItemCount(): Int {
        return posterUrls.size
    }

    override fun onBindViewHolder(holder: PhotoHolderSQLR, position: Int) {
        val posterUrl = posterUrls[position]
        val title = titles[position]
        val date = dates[position]
        val tmdbId = tmdbIds[position]
        holder.updateWithUrl("${Globals.homeIP}${Globals.radarrPort}/api/$posterUrl?apikey=${Globals.radarrAPI}", title, date, tmdbId)
    }
}

class PhotoHolderSQLR(v: View) : RecyclerView.ViewHolder(v) {

    private val movieImageSqlR: ImageView = v.findViewById(R.id.movieImageSqlR)
    private val movieTitleSqlR: TextView = v.findViewById(R.id.movieTitleSqlR)
    private val movieDateSqlR: TextView = v.findViewById(R.id.movieDateSqlR)
    private val movieTmdbIDSqlR: TextView = v.findViewById(R.id.movieTmdbIdSqlR)
    private val topLineSqlR: LinearLayout = v.findViewById(R.id.toplineSqlR)
    private val botLineSqlR: LinearLayout = v.findViewById(R.id.botlineSqlR)
    // private var posterUrlString = String()

    fun updateWithUrl(url: String, title: String, date: String, tmdbId: String) {
        Picasso.get().load(url).error(R.drawable.no_poster).placeholder(R.drawable.tpb_logo).into(movieImageSqlR)
        movieTitleSqlR.text = title
        movieDateSqlR.text = date
        movieTmdbIDSqlR.text = tmdbId
        topLineSqlR.visibility = View.VISIBLE
        botLineSqlR.visibility = View.VISIBLE
        // posterUrlString = urlstring
    }
}