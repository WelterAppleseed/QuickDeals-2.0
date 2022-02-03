package com.example.fffaaaa.adapter

import androidx.recyclerview.widget.RecyclerView

import android.content.Context

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import com.example.fffaaaa.custom.TaskItem
import kotlinx.android.synthetic.main.task_item_layout.view.*


class EmptyAdapter(private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(com.example.fffaaaa.R.layout.task_shablon, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = holder.itemView.findViewById<TaskItem>(com.example.fffaaaa.R.id.empty_task)
        task.setTitle("You're doing great!")
        task.setDate("You don't have any overdue goals!")
        task.radio.isEnabled = false
        task.radio.isFocusable = false
        task.radio.alpha = 0.3F
        task.title.alpha = 0.5F
        task.date.alpha = 0.8F
    }
    override fun getItemCount(): Int {
        return 1
    }
}