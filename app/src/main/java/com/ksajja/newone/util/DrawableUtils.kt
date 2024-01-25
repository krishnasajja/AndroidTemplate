package com.ksajja.newone.util

import android.graphics.drawable.Drawable

/**
 * Created by ksajja on 2/28/18.
 */

object DrawableUtils {
    private val EMPTY_STATE = intArrayOf()

    fun clearState(drawable: Drawable?) {
        if (drawable != null) {
            drawable.state = EMPTY_STATE
        }
    }
}
