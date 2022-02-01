package com.example.fffaaaa.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.adapter.ParentAdapter
import com.example.fffaaaa.adapter.TasksAdapter
import com.example.fffaaaa.room.TDao
import com.example.fffaaaa.room.TaskEntity
import java.io.Serializable
import java.lang.Exception

data class Page(
    var title: String,
    var taskList: ArrayList<TaskEntity>
) : Serializable {
}