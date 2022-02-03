package com.example.fffaaaa.model

import com.example.fffaaaa.room.enitites.TaskEntity
import java.io.Serializable

data class Page(
    var title: String,
    var taskList: ArrayList<TaskEntity>
) : Serializable