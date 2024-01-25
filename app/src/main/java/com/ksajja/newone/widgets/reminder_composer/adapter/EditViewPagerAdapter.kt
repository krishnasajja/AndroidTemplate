package com.ksajja.newone.widgets.reminder_composer.adapter

import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.ksajja.newone.widgets.reminder_composer.interfaces.SecondaryEditView
import java.util.*


/**
 * Created by ksajja on 3/3/18.
 */
class EditViewPagerAdapter(private var mEditViewList: LinkedList<SecondaryEditView<*>>? = null) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(mEditViewList!![position], position)
        return mEditViewList!![position]
    }

    override fun getCount(): Int {
        return mEditViewList?.size ?: 0
    }

    override fun destroyItem(container: ViewGroup, position: Int,
                             `object`: Any) {
        container.removeView(mEditViewList!![position])
    }
}