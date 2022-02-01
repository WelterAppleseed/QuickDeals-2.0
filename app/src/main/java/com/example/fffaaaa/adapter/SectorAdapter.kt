package com.example.fffaaaa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.presenter.StartPresenter
import com.example.fffaaaa.room.SectorEntity

class SectorAdapter(
    public var sectorList: ArrayList<SectorEntity>,
    private var fragmentPresenter: FragmentPresenter) :
    RecyclerView.Adapter<SectorAdapter.GridRecyclerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SectorAdapter.GridRecyclerViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return GridRecyclerViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: SectorAdapter.GridRecyclerViewHolder,
        position: Int
    ) {
        var sector = sectorList[position]
        sector.icon.let { holder.sectorIcon.setImageResource(it) }
        holder.sectorTitle.text = sector.title
        holder.sectorNotesCount.text =  "${sector.remCount} Tasks"
        holder.itemView.setOnClickListener {
            fragmentPresenter.onSectorClicked(sectorList[position])}
    }

    override fun getItemCount(): Int {
        return sectorList.size
    }

    class GridRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var sectorTitle: TextView
        var sectorNotesCount: TextView
        var sectorIcon: ImageView

        init {
            sectorTitle = itemView.findViewById(R.id.sectorTitle) as TextView
            sectorNotesCount = itemView.findViewById(R.id.sectorNotesCount) as TextView
            sectorIcon = itemView.findViewById(R.id.sectorIcon) as ImageView
        }

    }

}