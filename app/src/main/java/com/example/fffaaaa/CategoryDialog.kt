package com.example.fffaaaa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.category_picker_dialog.*
import kotlinx.android.synthetic.main.category_picker_dialog.view.*

class CategoryDialog : DialogFragment(), View.OnClickListener{

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.category_picker_dialog, container, false)
            rootView.home_i.setOnClickListener(this)
            rootView.work_i.setOnClickListener(this)
            rootView.travel_i.setOnClickListener(this)
            rootView.hobby_i.setOnClickListener(this)
            rootView.market_i.setOnClickListener(this)
            rootView.study_i.setOnClickListener(this)
            return rootView
        }

    override fun onClick(v: View?) {
        var icon = R.drawable.file_def_dr
        var title = "All"
        var color = R.color.mainColor
        when(v?.id) {
            view?.home_i?.id -> {
                icon = R.drawable.home_def_dr
                title = "Home"
                color = R.color.homeColor
            }
            view?.work_i?.id -> {
                icon = R.drawable.work_def_dr
                title = "Work"
                color = R.color.workColor
            }
            view?.travel_i?.id -> {
                icon = R.drawable.travel_def_dr
                title = "Travel"
                color = R.color.travelColor
            }
            view?.hobby_i?.id -> {
                icon = R.drawable.hobby_def_dr
                title = "Hobby"
                color = R.color.hobbyColor
            }
            view?.market_i?.id -> {
                icon = R.drawable.market_def_dr
                title = "Market"
                color = R.color.marketColor
            }
            view?.study_i?.id -> {
                icon = R.drawable.study_def_dr
                title = "Study"
                color = R.color.studyColor
            }
        }
        println("$title and $icon")
        val intent = Intent()
        intent.putExtra("title", title)
        intent.putExtra("icon", icon)
        intent.putExtra("color", color)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        this.dismiss()
    }
}