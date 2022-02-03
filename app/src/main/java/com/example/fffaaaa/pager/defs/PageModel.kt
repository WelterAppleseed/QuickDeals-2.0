package com.example.fffaaaa.pager.defs

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal model of a page
 * @param <T> the datatype of the {@link com.thehayro.view.InfinitePagerAdapter} indicator.
 */
public final class PageModel<T> {
    private var mIndicator: T = TODO()

    private var mParentView: ViewGroup

    private var mChildren: List<View>

   constructor(parent: ViewGroup, indicator: Any?) {
        mParentView = parent;
        mIndicator = indicator as T;
        val size = parent.getChildCount();
        var mChildren = ArrayList<View>(size);

        for (i in 0..size) {
            mChildren.add(parent.getChildAt(i));
        }
    }

    /**
     *
     * @return {@code true} if the model has child views.
     */
   fun hasChildren() : Boolean {
        return mChildren.size != 0;
    }


    private fun emptyChildren() {
        if (hasChildren()) {
            mChildren.clear();
        }
    }

    fun getChildren() : List<View> {
        return mChildren;
    }

   fun removeAllChildren() {
        mParentView.removeAllViews();
        emptyChildren();
    }

    fun addChild(child: View) {
        addViewToParent(child);
        mChildren.add(child);
    }

    fun removeViewFromParent(view: View) {
        mParentView.removeView(view);
    }

    fun addViewToParent(view: View) {
        mParentView.addView(view);
    }

    fun getParentView() : ViewGroup {
        return mParentView;
    }

    fun getIndicator() : T {
        return mIndicator;
    }

    fun setIndicator(indicator : T) {
        mIndicator = indicator;
    }
}