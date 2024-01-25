package com.ksajja.newone.interfaces

import android.view.MotionEvent



/**
 * Created by ksajja on 3/6/18.
 */
interface TouchEventListener {
    fun onTouch(ev: MotionEvent): Boolean
}