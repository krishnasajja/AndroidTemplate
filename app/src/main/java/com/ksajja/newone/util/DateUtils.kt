package com.ksajja.newone.util

import com.ksajja.newone.Constants
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ksajja on 2/12/18.
 */

object DateUtils {

    val currentTimeToDisplay: String
        get() {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat(Constants.DISPLAY_TIME_FORMAT, Locale.US)
            return format.format(calendar.time)
        }

    val currentDateToDisplay: String
        get() {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat(Constants.DISPLAY_DATE_FORMAT, Locale.US)
            return format.format(calendar.time)
        }


    fun formatDateToDisplay(year: Int, month: Int, day: Int): String {
        val formattedDate: String
        val simpleDateFormat = SimpleDateFormat(Constants.DISPLAY_DATE_FORMAT,
                Locale.US)
        val newDate = Calendar.getInstance()
        newDate.set(year, month, day)
        formattedDate = simpleDateFormat.format(newDate.time)
        return formattedDate
    }

    fun formatTimeToDisplay(hour: Int, minute: Int): String {
        val formattedDate: String
        val simpleDateFormat = SimpleDateFormat(Constants.DISPLAY_TIME_FORMAT,
                Locale.US)
        val newDate = Calendar.getInstance()
        newDate.set(Calendar.HOUR_OF_DAY, hour)
        newDate.set(Calendar.MINUTE, minute)
        formattedDate = simpleDateFormat.format(newDate.time)
        return formattedDate
    }

    fun changeDateFormat(dateInput: String?, inputDateFormat: String?, outputDateFormat: String?): String {
        var formattedDate = ""
        val date: Date

        if (dateInput != null) {
            if (dateInput.length > 1) {
                try {
                    date = SimpleDateFormat(inputDateFormat, Locale.US).parse(dateInput)
                    formattedDate = SimpleDateFormat(outputDateFormat, Locale.US).format(date)
                } catch (e: Exception) {
                    CLog.e(DateUtils::class.java, e.localizedMessage, e)
                }

            }
        }
        return formattedDate
    }
}
