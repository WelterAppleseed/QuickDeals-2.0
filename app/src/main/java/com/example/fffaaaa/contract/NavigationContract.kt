package com.example.fffaaaa.contract

import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.presenter.NavigationPresenter

interface NavigationContract {
    interface View {
        fun attachNavigationPresenter(navigation: NavigationPresenter)
        fun attachFragmentPresenter(fragmentPresenter: FragmentPresenter)
    }
}