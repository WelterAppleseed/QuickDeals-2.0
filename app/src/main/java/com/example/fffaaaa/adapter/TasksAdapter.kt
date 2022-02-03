package com.example.fffaaaa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.custom.TaskItem
import com.example.fffaaaa.room.enitites.TaskEntity
import kotlinx.android.synthetic.main.task_item_layout.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

class TasksAdapter(
    private var taskList: ArrayList<TaskEntity>,
    private var state: Int,
    private var parentAdapter: ParentAdapter
) : RecyclerView.Adapter<TasksAdapter.TaskRecyclerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskRecyclerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_shablon, parent, false)
        (v as TaskItem).init(state)
        return TaskRecyclerViewHolder(v)
    }

    @DelicateCoroutinesApi
    override fun onBindViewHolder(
        holder: TaskRecyclerViewHolder, position: Int
    ) {
        val taskEntity = taskList[position]
        holder.radioB.buttonDrawable!!.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(parentAdapter.getColor(), BlendModeCompat.SRC_ATOP)
        (holder.itemView as TaskItem).setDate(taskEntity.taskDate, state)
        holder.itemView.setTitle(taskEntity.taskTitle)
        holder.itemView.setTaskItemClickListener(View.OnClickListener {
            taskList.removeAt(position)
            parentAdapter.doneTask(state, taskEntity)
            parentAdapter.replaceContainerBack(
                if (holder.itemView.parent is RecyclerView) (holder.itemView.parent as RecyclerView) else (holder.itemView.parent.parent as RecyclerView), taskList.size)
        })

    }

    override fun getItemCount(): Int {
        return if (state == 2 && taskList.size > 3) 3 else taskList.size
    }

    class TaskRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioB: RadioButton = itemView.radio
    }

    fun insertToList(taskEntity: TaskEntity) {
        taskList.add(taskEntity)
        notifyItemInserted(taskList.size-1)
    }
}