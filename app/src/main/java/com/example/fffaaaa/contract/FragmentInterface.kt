package com.example.fffaaaa.contract

import androidx.fragment.app.Fragment
import com.example.fffaaaa.activity.NewReminderFragment

interface FragmentInterface {
    interface View {
        fun update(firstFragment: Fragment, secondFragment: Fragment)
        fun hide(fragment: Fragment)
        fun show(fragment: Fragment)
    }
    interface Presenter {
        fun updateFragment(firstFragment: Fragment, secondFragment: Fragment)
        fun hideFragment(fragment: Fragment)
        fun showFragment(fragment: Fragment)
    }
    }