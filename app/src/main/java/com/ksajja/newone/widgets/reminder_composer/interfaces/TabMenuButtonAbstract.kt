package com.ksajja.newone.widgets.reminder_composer.interfaces

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * Created by ksajja on 3/1/18.
 */
abstract class TabMenuButtonAbstract @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    abstract fun setIsFocused(focused : Boolean)
    abstract fun setIsActivated(activated : Boolean)
}