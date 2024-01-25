package com.ksajja.newone.extension

import android.view.View
import android.widget.TextView

/**
 * Created by ksajja on 3/9/18.
 */

fun TextView.setTextWithVisibility(strText: String?) {
    if (strText == null) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
        this.text = strText
    }
}

fun TextView.setTextWithVisibility(strText: Int?) {
    if (strText == null) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
        this.text = strText.toString()
    }
}