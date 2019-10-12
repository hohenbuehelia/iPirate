package com.example.ipirate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerAdapterTV : RecyclerView.Adapter<PhotoHolderTV>() {
    var posterUrls: Array<String> = arrayOf()
    var titles: Array<String> = arrayOf()
    var dates: Array<String> = arrayOf()
    var tvdbIds: Array<String> = arrayOf()
    var listofowned: Array<String> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolderTV {
        val imageItem = LayoutInflater.from(parent.context).inflate(R.layout.activity_display_tvshows, parent, false)
        return PhotoHolderTV(imageItem)
    }

    override fun getItemCount(): Int {
        return posterUrls.size
    }

    override fun onBindViewHolder(holder: PhotoHolderTV, position: Int) {
        val posterUrl = posterUrls[position]
        val title = titles[position]
        val date = dates[position]
        val tvdbId = tvdbIds[position]
        val owned = listofowned[position]
        holder.updateWithUrl("https://image.tmdb.org/t/p/w500/$posterUrl", title, date, tvdbId, owned)
    }
}

class PhotoHolderTV(v: View) : RecyclerView.ViewHolder(v) {

    private val tvImage: ImageView = v.findViewById(R.id.tvImage)
    private val tvTitle: TextView = v.findViewById(R.id.tvTitle)
    private val tvDate: TextView = v.findViewById(R.id.tvDate)
    private val tvTvdbID: TextView = v.findViewById(R.id.tvTvdbId)
    private val downloadShow: ImageButton = v.findViewById(R.id.downloadShow)
    private val topLine: LinearLayout = v.findViewById(R.id.tvtopline)
    private val botLine: LinearLayout = v.findViewById(R.id.tvbotline)
    private val tvInfo: ImageButton = v.findViewById(R.id.tvInfo)
    // private var posterUrlString = String()

    fun updateWithUrl(url: String, title: String, date: String, tvdbId: String, owned: String) {
        Picasso.get().load(url).error(R.drawable.no_poster).placeholder(R.drawable.tpb_logo).into(tvImage)
        tvTitle.text = title
        tvDate.text = date
        tvTvdbID.text = tvdbId
        if (owned == "false") {
            downloadShow.visibility = View.VISIBLE
        }
        topLine.visibility = View.VISIBLE
        botLine.visibility = View.VISIBLE
        tvInfo.visibility = View.VISIBLE
        // posterUrlString = urlstring
    }
}