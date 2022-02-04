package com.example.fffaaaa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.daos.TDao
import com.example.fffaaaa.room.enitites.TaskEntity
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import java.time.LocalDateTime
import android.view.ViewGroup
import com.example.fffaaaa.model.Page
import kotlinx.coroutines.DelicateCoroutinesApi
import com.example.fffaaaa.*


open class ParentAdapter @DelicateCoroutinesApi constructor(
    private var fullTasksList: List<ArrayList<TaskEntity>>,
    private var tDao: TDao,
    private var fragmentPresenter: FragmentPresenter,
    private var sectorEntity: SectorEntity,
    private var sectorPosition: Int,
    private var pages: ArrayList<Page>,
    pagesSizes: ArrayList<Int>, /*pagesSized could be used for dynamically size-changing of second sector*/
    private val color: Int,
    private var context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        internal const val VIEW_TYPE_RECYCLER = 0
        internal const val VIEW_TYPE_PAGES = 1
        internal const val VIEW_TYPE_RECYCLER2 = 2
        private val contentTitles = listOf("Late", "Today", "Done")
        //private var lastPosition = 0 /*last position could be used for dynamically size-changing of second sector*/
        lateinit var defaultParams: ViewGroup.LayoutParams
    }

   // var dynamicalPagesSizes: ArrayList<Int> = pagesSizes

    fun getPages(): ArrayList<Page> {
        return pages
    }

    fun getFullTaskList(): List<ArrayList<TaskEntity>> {
        return fullTasksList
    }

    fun getLateTaskList(): ArrayList<TaskEntity> {
        return fullTasksList[VIEW_TYPE_RECYCLER]
    }

    fun getSectorCount(): Int {
        return sectorEntity.remCount
    }

    fun getColor() : Int{
        return context.getColor(color)
    }


    fun setPages(pages: ArrayList<Page>) {
        this.pages = pages
    }

    fun setFullTaskList(fullTasksList: List<ArrayList<TaskEntity>>) {
        this.fullTasksList = fullTasksList
    }

    fun changeAdapterByAddingTask(taskEntity: TaskEntity) {
        for (i in 0..2) {
            if (fullTasksList[i].isEmpty()) {
                fullTasksList[i].add(taskEntity)
                break
            }
        }
    }
    fun addToLateTaskList(taskEntity: TaskEntity) {
        fullTasksList[VIEW_TYPE_RECYCLER].add(taskEntity)
    }

    fun removeFromFullTaskList(taskEntity: TaskEntity) {
        for (i in 0..2) {
            if (fullTasksList[i].contains(taskEntity)) {
                fullTasksList[i].remove(taskEntity)
                break
            }
        }
    }

    @DelicateCoroutinesApi
    fun updateSectorCount(up: Boolean) {
        if (up) sectorEntity.remCount++ else sectorEntity.remCount--
        fragmentPresenter.updateSectorInfoTaskCount(sectorEntity.remCount)
    }

    @DelicateCoroutinesApi
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

    @DelicateCoroutinesApi
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_RECYCLER -> {
                val pageHolder = holder as RecyclerItemHolder
                pageHolder.bind(VIEW_TYPE_RECYCLER)
            }
            VIEW_TYPE_PAGES -> {
                val pageHolder = holder as ViewPagerItemHolder
                pageHolder.bind(pages, tDao)
            }
            VIEW_TYPE_RECYCLER2 -> {
                val pageHolder = holder as RecyclerItemHolder
                pageHolder.bind(VIEW_TYPE_RECYCLER2)
            }
        }
    }

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @DelicateCoroutinesApi
    fun doneTask(state: Int, taskEntity: TaskEntity) {
        Log.i("ParentAdapter", "Done task.")
        //val isInPage = fullTasksList[VIEW_TYPE_PAGES].indexOf(taskEntity) != -1
        //fullTasksList[state].remove(taskEntity)
        if (state == VIEW_TYPE_PAGES) {
            for (i in 0 until pages.size) {
                if (pages[i].taskList.isEmpty()) {
                    pages.remove(pages[i])
                    break
                }
            }
        }
        fullTasksList[state].remove(taskEntity)
        taskEntity.isOver = true
        TaskEntity.update(tDao, taskEntity)
        if (fullTasksList[VIEW_TYPE_RECYCLER2].size < 3) {
            fullTasksList[VIEW_TYPE_RECYCLER2].add(0, taskEntity)
        } else {
            fullTasksList[VIEW_TYPE_RECYCLER2][0] = taskEntity
        }
        updateSectorCount(false)
        notifyItemChanged(state)
        notifyItemChanged(VIEW_TYPE_RECYCLER2)
        fragmentPresenter.changeSectorFromStartView(sectorEntity, sectorPosition, taskEntity, false)
    }

    fun replaceContainer(recyclerView: RecyclerView, count: Int) {
        val container =
            if (recyclerView.parent is NestedScrollView) recyclerView.parent.parent as RelativeLayout else recyclerView.parent as RelativeLayout
        if (count > 2 && container.layoutParams.height != dpToPx(210)) {
            Log.i("ParentAdapter", "replaceContainer")

            defaultParams = recyclerView.layoutParams

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
            Log.i("ParentAdapter", "replaceContainerBack")
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
        }
    }


    inner class RecyclerItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var contentTitle: TextView = view.findViewById<View>(R.id.content_title) as TextView
        private var childRecyclerView: RecyclerView = view.findViewById(R.id.child_recycler_view)


        @DelicateCoroutinesApi
        @SuppressLint("ClickableViewAccessibility")
        fun bind(style: Int) {
            contentTitle.text = contentTitles[style]
            val childAdapter =
                TasksAdapter(fullTasksList[style], style, this@ParentAdapter)
            childRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            childRecyclerView.adapter = childAdapter
            if (fullTasksList[style].size != 0) {
                if (style == VIEW_TYPE_RECYCLER) {
                    if (fullTasksList[style].size > 2) {
                        replaceContainer(childRecyclerView, fullTasksList[style].size)
                    }
                    childRecyclerView.setOnTouchListener(View.OnTouchListener { _, event ->
                        if ((childRecyclerView.adapter as TasksAdapter).itemCount > 2) {
                            if (event?.action == MotionEvent.ACTION_UP) {
                                parentRecycler.requestDisallowInterceptTouchEvent(false)
                            } else {
                                parentRecycler.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                        return@OnTouchListener true
                    })
                }
            } else {
                if (style != VIEW_TYPE_RECYCLER2) {
                    val emptyAdapter = EmptyAdapter(context)
                    childRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                    childRecyclerView.adapter = emptyAdapter
                    childRecyclerView.setOnTouchListener(null)
                }
            }
        }
    }

    inner class ViewPagerItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var viewPager: ViewPager = view.findViewById(R.id.looping_view_pager)
        @DelicateCoroutinesApi
        fun bind(pages: ArrayList<Page>, tDao: TDao) {
            if (pages.isEmpty()) {
                Log.i("Parent Adapter", "Pages are empty")
                pages.add(Page(getTime(LocalDateTime.now()), arrayListOf()))
            }
            /*viewPager.setHeight(1F, pages[0].taskList.size)
            val adapter = DemoInfiniteAdapter(
                ArrayList(pages),
                true,
                tDao,
                this@ParentAdapter,
                fragmentPresenter.onExistingTasksCreating()
            )*/
            // viewPager.setPageTransformer(true, CustomPageTransformer())
            val adapter = DemoInfiniteAdapter(
                ArrayList(pages),
                false,
                tDao,
                this@ParentAdapter,
                fragmentPresenter.onExistingTasksCreating()
            )
            viewPager.layoutParams.height = dpToPx(220)
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = pages.size
            viewPager.setPadding(dpToPx(40), 0, dpToPx(40), 0)
            viewPager.clipToPadding = false
            viewPager.pageMargin = dpToPx(10)
            /* viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == 0) {
                        Log.i(
                            "ParentAdapter",
                            "$dynamicalPagesSizes and my position in pages is $regularPosition and $lastPosition agaaaaaaaaaaa"
                        )
                       val anim =
                            ValueAnimator.ofInt(viewPager.layoutParams.height, dynamicalPagesSizes[regularPosition])
                        anim.addUpdateListener {
                            val height = it.animatedValue as Int
                            val layoutParams: ViewGroup.LayoutParams = viewPager.layoutParams
                            layoutParams.height = height
                            viewPager.layoutParams = layoutParams
                        }
                        anim.duration = 200
                        anim.start()
                    }
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    thread {
                        if (position != lastPosition && position - 1 <= pages.size) {
                            Log.i(
                                "ParentAdapter",
                                "$dynamicalPagesSizes and my position in pages is $position and $lastPosition"
                            )
                            lastPosition = position
                            regularPosition = when (position) {
                                pages.size + 1 -> {
                                    0
                                }
                                0 -> {
                                    pages.size - 1
                                }
                                else -> {
                                    lastPosition - 1
                                }
                            }
                        }
                    }
                }

                override fun onPageSelected(position: Int) {

                }
            })
        }*/
        }
    }
}