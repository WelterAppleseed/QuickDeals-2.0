package com.example.fffaaaa.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.model.Page
import com.example.fffaaaa.pager.LoopingPagerAdapter
import com.example.fffaaaa.room.daos.TDao


class DemoInfiniteAdapter(
    itemList: ArrayList<Page>,
    isInfinite: Boolean,
    var tDao: TDao,
    var adapter: ParentAdapter,
    var parent: RecyclerView
) : LoopingPagerAdapter<Page>(itemList, isInfinite) {
    private var viewList: MutableSet<View> = mutableSetOf()

    // Commented part is for dynamically size-changing of "existing tasks"-sector

    /*
    private var itemCount: MutableSet<Int> = mutableSetOf()

    fun getItemHeight(position: Int) : Int {
        return dpToPx(55) + dpToPx(65) * itemCount.elementAt(position)
    }*/

    override fun inflateView(
        viewType: Int,
        container: ViewGroup,
        listPosition: Int
    ): View {

        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.nested_into_viewpager_recycler, container, false)
        view.tag = listPosition
        viewList.add(view)
        return view
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
        val recycler = convertView.findViewById<RecyclerView>(R.id.nested_recycler_view)
        val childAdapter =
            itemList?.get(listPosition)?.taskList?.let { TasksAdapter(it, 1, adapter) }
        recycler.layoutManager = LinearLayoutManager(convertView.context)
        recycler.adapter = childAdapter
        recycler.isVerticalFadingEdgeEnabled = true
        recycler.setOnTouchListener(View.OnTouchListener { _, event ->
            if (itemList?.get(listPosition)?.taskList?.size!! > 2) {
                if (event?.action == MotionEvent.ACTION_UP) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            return@OnTouchListener true
        })
    }
/* fun resetHeight(viewPager: LoopingViewPager, current: Int) {
   this.current = current
   if (viewPager.childCount > current) {
       var layoutParams = viewPager.layoutParams as LinearLayout.LayoutParams?
       if (layoutParams == null) {
           layoutParams =
               LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewHeight)
       } else {
           layoutParams.height = viewHeight
       }
       setLayoutParams(layoutParams)
   }
}*/
}
