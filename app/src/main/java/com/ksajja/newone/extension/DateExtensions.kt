package com.ksajja.newone.extension

import com.ksajja.newone.Constants
import java.text.SimpleDateFormat
import java.util.*


/**
 * Extentions for Date, to check if date is today, tomorrow or in next week.
 * Created by ksajja on 3/1/18.
 */

fun Date.isInNextWeek(): Boolean {
    val nextWeekStart = Calendar.getInstance()
    nextWeekStart.add(Calendar.DAY_OF_WEEK, 7)
    val nextWeekEnd = Calendar.getInstance()
    nextWeekEnd.add(Calendar.DAY_OF_WEEK, 14)
    return (nextWeekStart.time.startOfTheDay() < this.time && this.time < nextWeekEnd.time.endOfTheDay())
}

fun Date.isInThisWeek(): Boolean {
    val thisWeekStart = Calendar.getInstance()
    val thisWeekEnd = Calendar.getInstance()
    thisWeekEnd.add(Calendar.DAY_OF_WEEK, 7)
    return (thisWeekStart.time.startOfTheDay() < this.time && this.time < thisWeekEnd.time.endOfTheDay())
}

fun Date.isTodayOrPast(): Boolean {
    return this.isToday() || this.isInPast()
}

fun Date.isToday(): Boolean {
    val todaysDate = Calendar.getInstance()
    return todaysDate.time.startOfTheDay() < this.time && this.time < todaysDate.time.endOfTheDay()
}

fun Date.isInPast(): Boolean {
    val todaysDate = Calendar.getInstance()
    return this.time < todaysDate.time.time
}

fun Date.isTomorrow(): Boolean {
    val tomorrowsDate = Calendar.getInstance()
    tomorrowsDate.add(Calendar.DAY_OF_MONTH, 1)
    return tomorrowsDate.time.startOfTheDay() < this.time && this.time < tomorrowsDate.time.endOfTheDay()
}

fun Date.startOfTheDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun Date.endOfTheDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

fun Date.formatDateToDisplay(dateFormat: String = Constants.DISPLAY_DATE_FORMAT): String {
    val formattedDate: String
    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
    formattedDate = simpleDateFormat.format(this)
    return formattedDate
}

fun Date.formatTimeToDisplay(dateFormat: String = Constants.DISPLAY_TIME_FORMAT): String {
    val formattedDate: String
    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
    formattedDate = simpleDateFormat.format(this)
    return formattedDate
}