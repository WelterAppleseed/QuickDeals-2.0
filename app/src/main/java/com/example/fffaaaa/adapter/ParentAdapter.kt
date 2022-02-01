package com.example.fffaaaa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asksira.loopingviewpager.LoopingViewPager
import com.example.fffaaaa.R
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TDao
import com.example.fffaaaa.room.TaskEntity
import android.widget.RelativeLayout
import androidx.core.view.iterator
import com.example.fffaaaa.dpToPx
import com.example.fffaaaa.getTime
import java.time.LocalDateTime


open class ParentAdapter(
    private var fullTasksList: List<ArrayList<TaskEntity>>,
    private var tDao: TDao,
    private var fragmentPresenter: FragmentPresenter,
    private var sectorEntity: SectorEntity,
    private var sectorPosition: Int,
    private var pages: ArrayList<Page>,
    private var context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        internal const val VIEW_TYPE_RECYCLER = 0
        internal const val VIEW_TYPE_PAGES = 1
        internal const val VIEW_TYPE_RECYCLER2 = 2
        private val contentTitles = listOf<String>("Late", "Today", "Done")
    }

    fun getPages(): ArrayList<Page> {
        return pages
    }

    fun getFullTaskList(): List<ArrayList<TaskEntity>> {
        return fullTasksList
    }

    fun getSectorCount(): Int {
        return sectorEntity.remCount
    }


    fun setPages(pages: ArrayList<Page>) {
        this.pages = pages
    }

    fun setFullTaskList(fullTasksList: List<ArrayList<TaskEntity>>) {
        this.fullTasksList = fullTasksList
    }

    fun addToFullTaskList(taskEntity: TaskEntity) {
        for (i in 0..2) {
            if (fullTasksList[i].isEmpty()) {
                fullTasksList[i].add(taskEntity)
                break
            }
        }
    }

    fun updateSectorCount(up: Boolean) {
        if (up) sectorEntity.remCount++ else sectorEntity.remCount--
        fragmentPresenter.updateSectorInfoTaskCount(sectorEntity.remCount)
    }

    private var parentRecycler = fragmentPresenter.onExistingTasksCreating()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            VIEW_TYPE_RECYCLER -> {
                val view = inflater.inflate(R.layout.nested_recyclers_layout, parent, false)
                viewHolder = RecyclerItemHolder(view)
            }
            VIEW_TYPE_PAGES -> {
                val view = inflater.inflate(R.layout.nested_viewpager_layout, parent, false)
                viewHolder = ViewPagerItemHolder(view)
            }
            VIEW_TYPE_RECYCLER2 -> {
                val view = inflater.inflate(R.layout.nested_recyclers_layout, parent, false)
                viewHolder = RecyclerItemHolder(view)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_RECYCLER -> {
                var pageHolder = holder as RecyclerItemHolder
                pageHolder.bind(VIEW_TYPE_RECYCLER, tDao)
            }
            VIEW_TYPE_PAGES -> {
                var pageHolder = holder as ViewPagerItemHolder
                pageHolder.bind(pages, tDao)
            }
            VIEW_TYPE_RECYCLER2 -> {
                var pageHolder = holder as RecyclerItemHolder
                pageHolder.bind(VIEW_TYPE_RECYCLER2, tDao)
            }
        }
    }

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun doneTask(state: Int, taskEntity: TaskEntity) {
        Log.i("ParentAdapter", "Done task.")
        fullTasksList[state].remove(taskEntity)
        taskEntity.isOver = true
        TaskEntity.update(tDao, taskEntity)
        if (fullTasksList[VIEW_TYPE_RECYCLER2].size < 3) {
            fullTasksList[VIEW_TYPE_RECYCLER2].add(taskEntity)
        } else {
            fullTasksList[VIEW_TYPE_RECYCLER2][0] = taskEntity
        }
        updateSectorCount(false)
        notifyItemRangeChanged(state, itemCount - state)
        fragmentPresenter.changeSectorFromStartView(sectorEntity, sectorPosition, taskEntity, false)
    }

    fun replaceContainer(recyclerView: RecyclerView, count: Int) {
        val container =
            if (recyclerView.parent is NestedScrollView) recyclerView.parent.parent as RelativeLayout else recyclerView.parent as RelativeLayout
        if (count > 2 && container.layoutParams.height != dpToPx(210)) {
            println("1a")
            val nestedScrollView = NestedScrollView(context)
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.BELOW, R.id.content_title)
            params.addRule(RelativeLayout.ALIGN_PARENT_START)
            params.addRule(RelativeLayout.ALIGN_PARENT_END)

            nestedScrollView.layoutParams = params
            nestedScrollView.isVerticalFadingEdgeEnabled = true
            nestedScrollView.setFadingEdgeLength(dpToPx(30))
            nestedScrollView.tag = "added_nested_scroll_view"

            val relative = recyclerView.parent as RelativeLayout
            val rootParams = relative.layoutParams
            rootParams.height = dpToPx(210)
            relative.layoutParams = rootParams
            relative.removeView(recyclerView)
            relative.addView(nestedScrollView)
            nestedScrollView.addView(recyclerView)
        }
    }

    fun replaceContainerBack(recyclerView: RecyclerView, count: Int) {
        val container =
            if (recyclerView.parent is NestedScrollView) recyclerView.parent.parent as RelativeLayout else recyclerView.parent as RelativeLayout
        if (count < 3 && container.layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            val rootParams = container.layoutParams
            rootParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            container.layoutParams = rootParams
            container.removeView(recyclerView)
            container.removeView(container.findViewWithTag("added_nested_scroll_view"))
            if (recyclerView.parent is NestedScrollView) {
                val recyclerRootParams = (recyclerView.parent as NestedScrollView).layoutParams
                (recyclerView.parent as NestedScrollView).removeView(recyclerView)
                recyclerView.layoutParams = recyclerRootParams
            }
            container.addView(recyclerView)
            notifyDataSetChanged()
        }
    }

    /**
     * [RecyclerView.ViewHolder] holding nested [RecyclerItemHolder]
     */
    inner class RecyclerItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val childAdapter = TasksAdapter(fullTasksList[], style, tDao, this@ParentAdapter)
        var contentTitle: TextView = view.findViewById<View>(R.id.content_title) as TextView
        var childRecyclerView: RecyclerView = view.findViewById(R.id.child_recycler_view)


        @SuppressLint("ClickableViewAccessibility")
        fun bind(style: Int, tDao: TDao) {
            contentTitle.text = contentTitles[style]
            val childAdapter =
                TasksAdapter(fullTasksList[style], style, tDao, this@ParentAdapter)
            childRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            childRecyclerView.adapter = childAdapter
            if (fullTasksList[style].size != 0) {
                if (style != 2) {
                     Log.i("TASK LIST SIZE", "${fullTasksList[style].size} with style $style")
                    if (fullTasksList[style].size > 2) {
                        replaceContainer(childRecyclerView, fullTasksList[style].size)
                    }
                    childRecyclerView.setOnTouchListener(View.OnTouchListener { v, event ->
                        if ((childRecyclerView.adapter as TasksAdapter).itemCount > 2) {
                            if (event?.action == MotionEvent.ACTION_UP) {
                                parentRecycler.requestDisallowInterceptTouchEvent(false);
                            } else {
                                parentRecycler.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        return@OnTouchListener true
                    })
                    val observer = object : RecyclerView.AdapterDataObserver() {
                        override fun onChanged() {
                            replaceContainer(
                                childRecyclerView,
                                (childRecyclerView.adapter as TasksAdapter).itemCount
                            )
                            notifyDataSetChanged()
                        }
                    }
                    (childRecyclerView.adapter as TasksAdapter).registerAdapterDataObserver(observer)
                }
            } else {
                val childAdapter = EmptyAdapter(context)
                childRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                childRecyclerView.adapter = childAdapter
                childRecyclerView.setOnTouchListener(null)
            }
        }
    }

        /**
         * [RecyclerView.ViewHolder] holding nested [SquareViewPager]
         */
        inner class ViewPagerItemHolder(view: View) : RecyclerView.ViewHolder(view) {
            var viewPager: LoopingViewPager = view.findViewById(R.id.looping_view_pager)
            fun bind(pages: ArrayList<Page>, tDao: TDao) {
                if (pages.isEmpty()) {
                    pages.add(Page(getTime(LocalDateTime.now()), arrayListOf<TaskEntity>()))
                }
                val adapter = DemoInfiniteAdapter(
                    ArrayList(pages),
                    true,
                    tDao,
                    this@ParentAdapter,
                    fragmentPresenter.onExistingTasksCreating()
                )
                viewPager.adapter = adapter

            }
        }
    }