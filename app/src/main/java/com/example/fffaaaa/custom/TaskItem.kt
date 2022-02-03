package com.example.fffaaaa.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.example.fffaaaa.R
import com.example.fffaaaa.beginWithUpperCase
import java.time.LocalDateTime

class TaskItem : RelativeLayout {

    companion object {
        const val STRIKETHROUGH_SPAN = Paint.STRIKE_THRU_TEXT_FLAG
        const val LATE = 0
        const val TODAY = 1
        const val DONE = 2
        const val RED_COLORED = 0
        const val DEFAULT = 1
        const val STRIKETHROUGH = 2
    }

    private var title: TextView = TextView(context)
    private var date: TextView = TextView(context)
    private var radioButton: RadioButton = RadioButton(context)
    private var titleText = DEFAULT
        set(title) {
            field = title
            invalidate()
        }
    private val dateText: String? = null
    var visualState = LATE
        set(state) {
            field = state
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init(LATE)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.TaskItem, 0, 0)
        visualState = typedArray.getInt(R.styleable.TaskItem_state, LATE)
        titleText = typedArray.getInt(R.styleable.TaskItem_textType, DEFAULT)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.task_item_layout, this, true)
        title = findViewById(R.id.title)
        date = findViewById(R.id.date)
        radioButton = findViewById(R.id.radio)
        typedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(LATE)
    }

    fun init(style: Int) {
        if (style == STRIKETHROUGH) {
            title.paintFlags = STRIKETHROUGH_SPAN
            title.setTextColor(context.getColor(R.color.mainColor))
            radioButton.isEnabled = false
            radioButton.isFocusable = false
            radioButton.isChecked = true
            this.alpha = 0.5F
        }
        if (style == RED_COLORED) {
            date.setTextColor(context.getColor(R.color.redColor))
            date.setTypeface(null, Typeface.BOLD)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        init(visualState)
    }

    fun setDate(localDateTime: LocalDateTime, style: Int) {
        when (style) {
            LATE -> {
                date.text = context.getString(
                    R.string.late_date_shablon,
                    localDateTime.hour,
                    localDateTime.minute,
                    localDateTime.month.toString().lowercase().beginWithUpperCase(),
                    localDateTime.dayOfMonth
                )
            }
            DONE -> {
                date.text = context.getString(
                    R.string.today_date_shablon,
                    localDateTime.hour,
                    localDateTime.minute
                )
            }
            TODAY -> {
                date.text = context.getString(
                    R.string.today_date_shablon,
                    localDateTime.hour,
                    localDateTime.minute
                )
            }
        }
    }
    fun setDate(textDate: String) {
        date.text = textDate
        date.setTextColor(context.getColor(R.color.mainColor))
    }

    fun getTitleTextView(): TextView {
        return title
    }

    fun getDateTextView(): TextView {
        return date
    }

    fun setTaskItemClickListener(clickListener: OnClickListener) {
        radioButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { radioButton, _ ->
            radioButton.isChecked = true
            radioButton.isFocusable = false
            radioButton.isEnabled = false
        })
        radioButton.setOnClickListener(clickListener)
    }

    fun setTitle(title: String) {
        this.title.text = title
    }
}