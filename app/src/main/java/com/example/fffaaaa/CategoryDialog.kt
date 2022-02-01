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
        var icon = R.drawable.file_def
        var title = "All"
        when(v?.id) {
            view?.home_i?.id -> {
                icon = R.drawable.dog_house_def
                title = "Home"
            }
            view?.work_i?.id -> {
                icon = R.drawable.briefcase_def
                title = "Work"
            }
            view?.travel_i?.id -> {
                icon = R.drawable.plane_def
                title = "Travel"
            }
            view?.hobby_i?.id -> {
                icon = R.drawable.color_palette_def
                title = "Hobby"
            }
            view?.market_i?.id -> {
                icon = R.drawable.shopping_cart_def
                title = "Market"
            }
            view?.study_i?.id -> {
                icon = R.drawable.book_def
                title = "Study"
            }
        }
        println("$title and $icon")
        val intent = Intent()
        intent.putExtra("title", title)
        intent.putExtra("icon", icon)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        this.dismiss()
    }
}