package com.example.fffaaaa.presenter

import androidx.fragment.app.Fragment
import com.example.fffaaaa.contract.FragmentInterface

class NavigationPresenter(private var view: FragmentInterface.View) : FragmentInterface.Presenter {



    override fun updateFragment(firstFragment: Fragment, secondFragment: Fragment ) {
        view.update(firstFragment, secondFragment)
    }

    override fun hideFragment(fragment: Fragment) {
        view.hide(fragment)
    }

    override fun showFragment(fragment: Fragment) {
        view.show(fragment)
    }

}