package com.example.ipirate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerAdapter : RecyclerView.Adapter<PhotoHolder>() {
    var posterUrls: Array<String> = arrayOf()
    var titles: Array<String> = arrayOf()
    var dates: Array<String> = arrayOf()
    var tmdbIds: Array<String> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val imageItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_display_movies, parent, false)
        return PhotoHolder(imageItem)
    }

    override fun getItemCount(): Int {
        return posterUrls.size
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val posterUrl = posterUrls[position]
        val title = titles[position]
        val date = dates[position]
        val tmdbId = tmdbIds[position]
        holder.updateWithUrl("https://image.tmdb.org/t/p/w500/$posterUrl", title, date, tmdbId)
    }
}

class PhotoHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val movieImage: ImageView = v.findViewById(R.id.movieImage)
    private val movieTitle: TextView = v.findViewById(R.id.movieTitle)
    private val movieDate: TextView = v.findViewById(R.id.movieDate)
    private val movieTmdbID: TextView = v.findViewById(R.id.movieTmdbId)
    private val downloadMovie: ImageButton = v.findViewById(R.id.downloadMovie)
    // val parentLayout: ConstraintLayout = v.findViewById(R.id.parent_layout)
    // private var posterUrlString = String()

    fun updateWithUrl(url: String, title: String, date: String, tmdbId: String) {
        Picasso.get().load(url).error(R.drawable.no_poster).placeholder(R.drawable.tpb_logo)
            .into(movieImage)
        movieTitle.text = title
        movieDate.text = date
        movieTmdbID.text = tmdbId
        downloadMovie.visibility = View.VISIBLE
        // posterUrlString = urlstring
    }
}
