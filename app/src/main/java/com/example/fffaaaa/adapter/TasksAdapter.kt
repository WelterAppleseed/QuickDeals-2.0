package com.example.fffaaaa.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.custom.TaskItem
import com.example.fffaaaa.room.TDao
import com.example.fffaaaa.room.TaskEntity
import kotlinx.android.synthetic.main.task_item_layout.view.*

class TasksAdapter(
    var taskList: ArrayList<TaskEntity>,
    private var state: Int,
    private var tDao: TDao,
    private var parentAdapter: ParentAdapter
) : RecyclerView.Adapter<TasksAdapter.TaskRecyclerViewHolder>() {
    private var i = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TasksAdapter.TaskRecyclerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_shablon, parent, false)
        (v as TaskItem).init(state)
        return TaskRecyclerViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: TasksAdapter.TaskRecyclerViewHolder, position: Int
    ) {
        val taskEntity = taskList[position]
        holder.radioB.buttonDrawable!!.setColorFilter(parentAdapter.getColor(), PorterDuff.Mode.SRC_IN);
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

        val dateTV: TextView
        val taskTV: TextView
        val radioB: RadioButton

        init {
            dateTV = (itemView as TaskItem).getTitleTextView()
            taskTV = itemView.getDateTextView();
            radioB = itemView.radio
        }
    }

    fun insertToList(taskEntity: TaskEntity) {
        taskList.add(taskEntity)
        notifyItemInserted(taskList.size-1)
    }
}