package com.example.playtronic.fragments.fragmentsMenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playtronic.Player
import com.example.playtronic.R

class RankingAdapter(private val playerList: List<Player>) :
    RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ranking_item, parent, false)
        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val player = playerList[position]
        holder.tvUsername.text = player.userName
        holder.tvPJ.text = player.PJ.toString()
        holder.tvPG.text = player.PG.toString()
        holder.tvPP.text = player.PP.toString()
        holder.tvPts.text = player.Pts.toString()
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvPJ: TextView = itemView.findViewById(R.id.tvPJ)
        val tvPG: TextView = itemView.findViewById(R.id.tvPG)
        val tvPP: TextView = itemView.findViewById(R.id.tvPP)
        val tvPts: TextView = itemView.findViewById(R.id.tvPts)
    }
}
