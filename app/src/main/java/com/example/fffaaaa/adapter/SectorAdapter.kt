package com.example.fffaaaa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.room.enitites.SectorEntity

class SectorAdapter(
    var sectorList: ArrayList<SectorEntity>,
    private var fragmentPresenter: FragmentPresenter) :
    RecyclerView.Adapter<SectorAdapter.GridRecyclerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GridRecyclerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return GridRecyclerViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: GridRecyclerViewHolder,
        position: Int
    ) {
        val sector = sectorList[position]
        sector.icon.let { holder.sectorIcon.setImageResource(it) }
        holder.sectorTitle.text = sector.title
        holder.sectorNotesCount.text =  holder.itemView.context.getString(R.string.sector_tasks, sector.remCount)
        holder.itemView.setOnClickListener {
            fragmentPresenter.onSectorClicked(this.sectorList[position])}
    }

    override fun getItemCount(): Int {
        return sectorList.size
    }

    class GridRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var sectorTitle: TextView = itemView.findViewById(R.id.sectorTitle) as TextView
        var sectorNotesCount: TextView = itemView.findViewById(R.id.sectorNotesCount) as TextView
        var sectorIcon: ImageView = itemView.findViewById(R.id.sectorIcon) as ImageView

    }

}