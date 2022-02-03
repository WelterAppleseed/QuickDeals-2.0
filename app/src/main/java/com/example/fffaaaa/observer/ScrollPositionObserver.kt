package com.example.fffaaaa.observer

import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ScrollView


class ScrollPositionObserver(private var mScrollView: ScrollView, private var mPhotoIV : ImageView, imageHeight: Int) : ViewTreeObserver.OnScrollChangedListener {
    private val mImageViewHeight: Int = imageHeight
    override fun onScrollChanged() {
        Log.i("Function", "")
        val scrollY = mScrollView.scrollY.coerceAtLeast(0).coerceAtMost(mImageViewHeight)

        // changing position of ImageView
        mPhotoIV.translationY = (scrollY / 2).toFloat()

        // alpha you could set to ActionBar background
        val alpha = scrollY / mImageViewHeight.toFloat()
        mPhotoIV.alpha = 1-alpha
    }

}