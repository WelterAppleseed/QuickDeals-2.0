@file:JvmName("Utils")

package com.example.fffaaaa

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.adapter.ParentAdapter
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TDao
import com.example.fffaaaa.room.TaskEntity
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import android.view.ViewGroup

import android.view.View

import android.view.View.OnTouchListener

import com.example.fffaaaa.activity.NewReminderFragment
import com.example.fffaaaa.contract.KeyboardInterface
import com.example.fffaaaa.adapter.Page
import com.example.fffaaaa.presenter.FragmentPresenter
import kotlin.collections.ArrayList


fun getSectorId(title: String): Long {
    return Sectors.valueOf(title.uppercase()).ordinal.toLong()
}

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

    sectorInfoIconImg.setImageResource(sectorEntity.icon)
    sectorInfoTitleTV.text = sectorEntity.title
    sectorInfoTaskCountTV.text =
        context.getString(R.string.task_count_text, sectorEntity.remCount)

    var lateArrayList = arrayListOf<TaskEntity>()
    var existArrayList = arrayListOf<TaskEntity>()
    var doneArrayList = arrayListOf<TaskEntity>()

    var dayCountSet = mutableSetOf<Long>()

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
    pages.iterator().forEach { it.title }
    parentRec.adapter =
        ParentAdapter(
            arrayListOf(lateArrayList, existArrayList, doneArrayList),
            tDao,
            fragmentPresenter,
            sectorEntity,
            sectorPosition,
            pages,
            context
        )
    parentRec.layoutManager = LinearLayoutManager(context)
}

fun getDatePickerDialog(context: Context, textView: TextView): DatePickerDialog {

    val currentDateTime = Calendar.getInstance()
    val startYear = currentDateTime.get(Calendar.YEAR)
    val startMonth = currentDateTime.get(Calendar.MONTH)
    val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
    val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
    val startMinute = currentDateTime.get(Calendar.MINUTE)

    return DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, day ->
        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
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
    var pattern = ""
    pattern =
        if (stringDate.indexOf(",") == 1) "d, MMMM yyyy, HH:mm" else "dd, MMMM yyyy, HH:mm"
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

    // Set up touch listener for non-text box views to hide keyboard.
    if (view is MenuItem) {
        view.setOnTouchListener(OnTouchListener { v, event ->
            hideSoftKeyboard(context)
            v.performClick()
            false
        })
    }
    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until (view as ViewGroup).childCount) {
            val innerView: View = (view as ViewGroup).getChildAt(i)
            println("ge")
            setupUI(fragment, innerView, context)
        }
    }
}

fun ImageView.scaleDown() {
    val scaleDownX: ObjectAnimator = ObjectAnimator.ofFloat(this, "scaleX", 0.7f)
    val scaleDownY: ObjectAnimator = ObjectAnimator.ofFloat(this, "scaleY", 0.7f)
    scaleDownX.duration = 1500
    scaleDownY.duration = 1500

    val moveUpY: ObjectAnimator = ObjectAnimator.ofFloat(this, "translationY", (-100).toFloat())
    moveUpY.duration = 1500
    val scaleDown = AnimatorSet()
    val moveUp = AnimatorSet()
    scaleDown.play(scaleDownX).with(scaleDownY)
    moveUp.play(moveUpY)
}

fun setGlobalLayoutListener(
    view: View,
    isKeyBoardShown: Boolean,
    keyboardInterface: KeyboardInterface
) {
    view.viewTreeObserver.addOnGlobalLayoutListener {
        var r: Rect = Rect()
        view.getWindowVisibleDisplayFrame(r)
        var screenH = view.rootView.height
        var keypadH = screenH - r.bottom

        if (keypadH > screenH * 0.15) {
            if (!isKeyBoardShown) {
                keyboardInterface.onKeyboardVisibilityChanged(true)
            }
        } else {
            if (isKeyBoardShown) {
                keyboardInterface.onKeyboardVisibilityChanged(false)
            }
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
    return (px / Resources.getSystem().getDisplayMetrics().density).toInt()
}

fun updatedPages(taskEntity: TaskEntity, pages: ArrayList<Page>): ArrayList<Page> {
    var dayCountSet: MutableList<Long> = mutableListOf()
    for (i in pages) {
            dayCountSet.add(i.taskList[0].taskDate.withHour(0).withMinute(0).withNano(0).toEpochSecond(
                ZoneOffset.UTC))
    }
    dayCountSet.iterator().forEach {  }
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
    }
    return pages
}
fun getTime(localDate: LocalDateTime) : String {
    val pattern = if (localDate.dayOfMonth-10 < 0) "d, MMMM yyyy" else "dd, MMMM yyyy"
    return localDate.format(DateTimeFormatter.ofPattern(pattern))
}