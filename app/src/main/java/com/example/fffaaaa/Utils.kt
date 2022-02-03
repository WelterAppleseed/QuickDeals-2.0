@file:JvmName("Utils")

package com.example.fffaaaa

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.adapter.ParentAdapter
import com.example.fffaaaa.enums.Month
import com.example.fffaaaa.enums.Sectors
import com.example.fffaaaa.fragments.NewReminderFragment
import com.example.fffaaaa.model.Page
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.room.daos.TDao
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.enitites.TaskEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*


fun getSectorId(title: String): Long {
    return Sectors.valueOf(title.uppercase()).ordinal.toLong()
}

@DelicateCoroutinesApi
fun updateSectorInfo(
    layout: LinearLayout,
    taskList: List<TaskEntity>,
    sectorEntity: SectorEntity,
    sectorPosition: Int,
    context: Context,
    localDate: LocalDateTime,
    tDao: TDao,
    fragmentPresenter: FragmentPresenter
) {

    val sectorInfoIconImg = layout.findViewById<ImageView>(R.id.sector_info_icon_img)
    val sectorInfoTitleTV = layout.findViewById<TextView>(R.id.sector_info_title_tv)
    val sectorInfoTaskCountTV = layout.findViewById<TextView>(R.id.sector_info_task_count_tv)
    val sectorToolbar = layout.findViewById<Toolbar>(R.id.sector_toolbar)

    val color = getColorBySectorIcon(sectorEntity.icon)
    layout.backgroundTintList =  ContextCompat.getColorStateList(context, color)
    sectorToolbar.backgroundTintList =  ContextCompat.getColorStateList(context, color)

    sectorInfoIconImg.setImageResource(sectorEntity.icon)
    sectorInfoTitleTV.text = sectorEntity.title
    sectorInfoTaskCountTV.text =
        context.getString(R.string.task_count_text, sectorEntity.remCount)

    val lateArrayList = arrayListOf<TaskEntity>()
    val existArrayList = arrayListOf<TaskEntity>()
    val doneArrayList = arrayListOf<TaskEntity>()

    val dayCountSet = mutableSetOf<Long>()

    val parentRec: RecyclerView = layout.findViewById(R.id.parent_rec)
    for (taskEntity in taskList) {
        if (taskEntity.isOver) {
            doneArrayList.add(taskEntity)
        } else {
            if (taskEntity.taskDate.toEpochSecond(ZoneOffset.UTC) <= localDate.toEpochSecond(
                    ZoneOffset.UTC
                )
            ) {
                lateArrayList.add(taskEntity)
            } else {
                existArrayList.add(taskEntity)
                dayCountSet.add(
                    taskEntity.taskDate.withHour(0).withMinute(0).withNano(0).toEpochSecond(
                        ZoneOffset.UTC
                    )
                )
                dayCountSet.sorted()
            }
        }
    }

    val pages: ArrayList<Page> = arrayListOf()
    val pagesSize: ArrayList<Int> = arrayListOf()
    for (i in dayCountSet.indices) {
        val dayList = arrayListOf<TaskEntity>()
        var title = ""
        for (q in existArrayList.indices) {
            if (existArrayList[q].taskDate.withHour(0).withMinute(0).withNano(0).toEpochSecond(
                    ZoneOffset.UTC
                ) == dayCountSet.elementAt(i)
            ) {
                title = getTime(existArrayList[q].taskDate)
                dayList.add(existArrayList[q])
            }
        }
        pages.add(Page(title, dayList))
    }
    pages.sortPages()
    //Commented part is for dynamically size changes for existing task sector

    /*pages.iterator().forEach {
        pagesSize.add(getHeight(it.taskList.size))
    }*/

    val parentAdapter =  ParentAdapter(
        arrayListOf(lateArrayList, existArrayList, doneArrayList),
        tDao,
        fragmentPresenter,
        sectorEntity,
        sectorPosition,
        pages,
        pagesSize,
        color,
        context
    )
    val br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (sectorEntity.id == intent.getLongExtra("sector_id", 0)) {
                Log.i("AAAAAAAAAA", "AAAAAAAAAAAAA $intent")
                val pageList = parentAdapter.getPages()
                val task = TaskEntity.getTaskById(tDao, intent.getLongExtra("id", 0))!!
                for (i in 0 until pages.size) {
                    if (layout.isVisible) {
                        if (pageList[i].taskList.contains(task)) {
                            pageList[i].taskList.remove(task)
                            break
                        }
                    }
                }
                parentAdapter.addToLateTaskList(task)
                parentAdapter.setPages(parentAdapter.getPages())
                parentAdapter.notifyItemRangeChanged(0, 2)
            }
        }
    }
    val filter = IntentFilter()
    filter.addAction("UPDATE_PAGES")
    context.registerReceiver(br, filter)
    parentRec.adapter = parentAdapter
    parentRec.layoutManager = LinearLayoutManager(context)
}

fun getDatePickerDialog(context: Context, textView: TextView): DatePickerDialog {

    val currentDateTime = Calendar.getInstance()
    val startYear = currentDateTime.get(Calendar.YEAR)
    val startMonth = currentDateTime.get(Calendar.MONTH)
    val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
    val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
    val startMinute = currentDateTime.get(Calendar.MINUTE)

    return DatePickerDialog(context, { _, year, month, day ->
        TimePickerDialog(context, { _, hour, minute ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year, month, day, hour, minute)
            textView.text = context.getString(
                R.string.new_task_date_shablon,
                day,
                Month.values()[month],
                year,
                hour,
                minute,
            )
            textView.setTextColor(context.getColor(R.color.black))
        }, startHour, startMinute, false).show()
    }, startYear, startMonth, startDay)
}

fun dateFromString(stringDate: String): LocalDateTime {
    val pattern: String = if (stringDate.indexOf(",") == 1) "d, MMMM yyyy, HH:mm" else "dd, MMMM yyyy, HH:mm"
    var dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern)
            .toFormatter(Locale.CANADA)
    dateTimeFormatter = dateTimeFormatter.withLocale(Locale.CANADA)
    return LocalDateTime.parse(stringDate, dateTimeFormatter)
}

fun String.beginWithUpperCase(): String {
    return when (this.length) {
        0 -> ""
        1 -> this.uppercase()
        else -> this[0].uppercase() + this.substring(1)
    }
}

fun hideSoftKeyboard(context: Context) {
    val activity = context as Activity
    activity.currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(context, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun setupUI(fragment: NewReminderFragment, view: View, context: Context) {

    if (view is MenuItem) {
        view.setOnTouchListener { v, _ ->
            hideSoftKeyboard(context)
            v.performClick()
            false
        }
    }
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView: View = view.getChildAt(i)
            setupUI(fragment, innerView, context)
        }
    }
}

fun TextView.invalidValue() {
    this.setTextColor(context.getColor(R.color.redColor))
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun updatedPages(taskEntity: TaskEntity, pages: ArrayList<Page>): ArrayList<Page> {
    val dayCountSet: MutableList<Long> = mutableListOf()
    for (i in pages) {
            dayCountSet.add(i.taskList[0].taskDate.withHour(0).withMinute(0).withNano(0).toEpochSecond(
                ZoneOffset.UTC))
    }
    var isFound = false
    for (c in 0 until pages.size) {
        if (dayCountSet[c] == taskEntity.taskDate.withHour(0).withMinute(0).withNano(0).toEpochSecond(
                ZoneOffset.UTC)
        ) {
            pages[c].taskList.add(taskEntity)
            isFound = true
            break
        }
    }
    if (!isFound) {
        pages.add(Page(getTime(taskEntity.taskDate), arrayListOf(taskEntity)))
        pages.sortPages()
    }
    return pages
}
fun MutableList<Page>.sortPages() {
    this.sortBy { it.taskList[0].taskDate }
}
fun getTime(localDate: LocalDateTime) : String {
    val pattern = if (localDate.dayOfMonth-10 < 0) "d, MMMM yyyy" else "dd, MMMM yyyy"
    return localDate.format(DateTimeFormatter.ofPattern(pattern))
}

// Commented part is for dynamically size-changing of "existing tasks"-sector

/*
fun View.setHeight(positionOffSet: Float, itemCount: Int) {
    val float = (dpToPx(65) + itemCount * dpToPx(60) * positionOffSet).toInt()
    if (float > this.layoutParams.height) {
        this.layoutParams.height = float
    }
    //Log.i("Parent Adapter", "Height is $float")
}
fun getHeight(itemCount: Int) : Int {
    return if (itemCount > 2) {
        dpToPx(210)
    } else {
        (dpToPx(55) + itemCount * dpToPx(65))
    }
}
fun View.setHeight(positionOffSet: Float, itemCount: Int, nextItemCount: Int) {
    println(positionOffSet)
    val height = 2 * (dpToPx(65) + (nextItemCount * dpToPx(60)) * positionOffSet).toInt()
    if (nextItemCount > itemCount) {
        if (height >= this.layoutParams.height) {
            //Log.i("Parent Adapter", "Up: Height is ${pxToDp(height)}, last was ${this.layoutParams.height}")
            this.layoutParams.height = pxToDp(height)
        }
    } else {
        if (height < this.layoutParams.height) {
            //Log.i("Parent Adapter", "Down: Height is ${pxToDp(height)}, last was ${this.layoutParams.height}")
            this.layoutParams.height = pxToDp(height)
        }
    }
}*/

fun getColorBySectorIcon(icon: Int) : Int {
    var color = R.color.mainColor
    when (icon) {
        R.drawable.home_def_dr -> color = R.color.homeColor
        R.drawable.work_def_dr -> color = R.color.workColor
        R.drawable.travel_def_dr -> color = R.color.travelColor
        R.drawable.hobby_def_dr -> color = R.color.hobbyColor
        R.drawable.market_def_dr -> color = R.color.marketColor
        R.drawable.study_def_dr -> color = R.color.studyColor
    }
    println(color)
    return color
}