package com.ksajja.newone.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by ksajja on 1/31/18.
 */

object AndroidUtils {

    fun hideSoftKeyboard(context: Context?, view: View?) {
        if (context == null || view == null) return
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showSoftKeyboard(context: Context?, view: View?) {
        if (context == null || view == null) return
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}
