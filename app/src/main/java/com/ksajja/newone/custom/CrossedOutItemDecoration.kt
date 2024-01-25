package com.ksajja.newone.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View

import com.ksajja.newone.R

/**
 * Created by ksajja on 1/26/18.
 */

class CrossedOutItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val mCrossedOut: Drawable?

    init {
        mCrossedOut = ContextCompat.getDrawable(context, R.drawable.line_divider)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        var child: View? = parent.getChildAt(0)
        if (child != null) {
            var params = child.layoutParams as RecyclerView.LayoutParams
            val left = params.leftMargin
            val right = parent.width - params.rightMargin
            var beg = child.bottom + params.bottomMargin
            var bottom = beg + mCrossedOut!!.intrinsicHeight
            mCrossedOut.setBounds(left, beg / 2, right, bottom / 2 + 2)
            mCrossedOut.draw(c)
            beg /= 2

            for (i in 1 until parent.childCount) {
                child = parent.getChildAt(i)

                params = child!!.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                bottom = top + mCrossedOut.intrinsicHeight

                mCrossedOut.setBounds(left, top - beg, right, bottom - beg)
                mCrossedOut.draw(c)
            }
        }
    }

}
