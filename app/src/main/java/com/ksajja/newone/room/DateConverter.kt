package com.ksajja.newone.room

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by ksajja on 2/25/18.
 */

object DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
