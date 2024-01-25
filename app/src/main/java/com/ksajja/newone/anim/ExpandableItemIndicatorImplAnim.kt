package com.ksajja.newone.anim

import android.content.Context
import android.graphics.drawable.Animatable
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.LayoutInflater

import com.ksajja.newone.R
import com.ksajja.newone.widgets.ExpandableItemIndicator

/**
 * Created by ksajja on 2/28/18.
 */

class ExpandableItemIndicatorImplAnim : ExpandableItemIndicator.Impl() {
    private var mImageView: AppCompatImageView? = null

    override fun onInit(context: Context, attrs: AttributeSet?, defStyleAttr: Int, thiz: ExpandableItemIndicator) {
        val v = LayoutInflater.from(context).inflate(R.layout.widget_expandable_item_indicator, thiz, true)
        mImageView = v.findViewById(R.id.image_view)
    }

    override fun setExpandedState(isExpanded: Boolean, animate: Boolean) {
        if (animate) {
            @DrawableRes val resId = if (isExpanded) R.drawable.ic_expand_more_to_expand_less else R.drawable.ic_expand_less_to_expand_more
            mImageView!!.setImageResource(resId)
            (mImageView!!.drawable as Animatable).start()
        } else {
            @DrawableRes val resId = if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            mImageView!!.setImageResource(resId)
        }
    }
}
