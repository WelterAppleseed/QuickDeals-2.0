package com.example.fffaaaa.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.example.fffaaaa.R
import com.example.fffaaaa.room.TDao
import android.view.MotionEvent
import android.widget.AbsListView
import com.example.fffaaaa.getTime
import android.R.string.no
import com.example.fffaaaa.dpToPx


class DemoInfiniteAdapter(
    itemList: ArrayList<Page>,
    isInfinite: Boolean,
    var tDao: TDao,
    var adapter: ParentAdapter,
    var parent: RecyclerView
) : LoopingPagerAdapter<Page>(itemList, isInfinite) {

    //This method will be triggered if the item View has not been inflated before.
    override fun inflateView(
        viewType: Int,
        container: ViewGroup,
        listPosition: Int
    ): View {
        return LayoutInflater.from(container.context)
            .inflate(R.layout.nested_into_viewpager_recycler, container, false)
    }

    //Bind your data with your item View here.
    //Below is just an example in the demo app.
    //You can assume convertView will not be null here.
    //You may also consider using a ViewHolder pattern.
    @SuppressLint("ClickableViewAccessibility")
    override fun bindView(
        convertView: View,
        listPosition: Int,
        viewType: Int
    ) {
        convertView.findViewById<TextView>(R.id.inf_page_content_title).text =
            itemList?.get(listPosition)?.title
        var recycler = convertView.findViewById<RecyclerView>(R.id.nested_recycler_view)
        val childAdapter =
            itemList?.get(listPosition)?.taskList?.let { TasksAdapter(it, 1, tDao, adapter) }
        recycler.layoutManager = LinearLayoutManager(convertView.context)
        recycler.adapter = childAdapter
        recycler.isVerticalFadingEdgeEnabled = true
        recycler.setOnTouchListener(View.OnTouchListener { v, event ->
                if (itemList?.get(listPosition)?.taskList?.size!! > 2) {
                    if (event?.action == MotionEvent.ACTION_UP) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    } else {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
            return@OnTouchListener true
        })
        val observer = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                adapter.updateSectorCount(true)
                adapter.notifyDataSetChanged()
            }
        }
        (recycler.adapter as TasksAdapter).registerAdapterDataObserver(observer)
    }
}
