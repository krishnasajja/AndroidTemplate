package com.ksajja.newone.extension;

import com.ksajja.newone.Constants
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ksajja on 3/5/18.
 */

fun String?.isNotNullNorEmpty(): Boolean {
    return !this.isNullOrBlank()
}

fun String.getDate(dateFormat: String = Constants.DISPLAY_DATE_FORMAT): Date? {
    return try {
        SimpleDateFormat(dateFormat, Locale.US).parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.getTime(dateFormat: String = Constants.DISPLAY_TIME_FORMAT): Date? {
    return try {
        SimpleDateFormat(dateFormat, Locale.US).parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}