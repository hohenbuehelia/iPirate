package com.example.ipirate

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerAdapterSQLS : RecyclerView.Adapter<PhotoHolderSQLS>() {
    var posterUrls: Array<String> = arrayOf()
    var titles: Array<String> = arrayOf()
    var dates: Array<String> = arrayOf()
    var tvdbIds: Array<String> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolderSQLS {
        val imageItem = LayoutInflater.from(parent.context).inflate(R.layout.activity_display_sonarr, parent, false)
        return PhotoHolderSQLS(imageItem)
    }

    override fun getItemCount(): Int {
        return posterUrls.size
    }

    override fun onBindViewHolder(holder: PhotoHolderSQLS, position: Int) {
        val posterUrl = posterUrls[position]
        val title = titles[position]
        val date = dates[position]
        val tvdbId = tvdbIds[position]
        holder.updateWithUrl("${Globals.homeIP}${Globals.sonarrPort}/api/$posterUrl?apikey=${Globals.sonarrAPI}", title, date, tvdbId)
    }
}

class PhotoHolderSQLS(v: View) : RecyclerView.ViewHolder(v) {

    private val tvImageSqlS: ImageView = v.findViewById(R.id.tvImageSqlS)
    private val tvTitleSqlS: TextView = v.findViewById(R.id.tvTitleSqlS)
    private val tvDateSqlS: TextView = v.findViewById(R.id.tvDateSqlS)
    private val tvTvdbIDSqlS: TextView = v.findViewById(R.id.tvTvdbIdSqlS)
    private val topLineSqlS: LinearLayout = v.findViewById(R.id.toplineSqlS)
    private val botLineSqlS: LinearLayout = v.findViewById(R.id.botlineSqlS)
    // private var posterUrlString = String()

    fun updateWithUrl(url: String, title: String, date: String, tvdbId: String) {
        Picasso.get().load(url).error(R.drawable.no_poster).placeholder(R.drawable.tpb_logo).into(tvImageSqlS)
        tvTitleSqlS.text = title
        tvDateSqlS.text = date
        tvTvdbIDSqlS.text = tvdbId
        topLineSqlS.visibility = View.VISIBLE
        botLineSqlS.visibility = View.VISIBLE
        // posterUrlString = urlstring
    }
}