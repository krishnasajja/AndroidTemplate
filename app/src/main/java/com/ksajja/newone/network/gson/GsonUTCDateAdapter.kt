package com.ksajja.newone.network.gson

import com.google.gson.*
import com.ksajja.newone.Constants
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ksajja on 2/15/18.
 */

class GsonUTCDateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {
    private val dateFormat: DateFormat

    init {
        dateFormat = SimpleDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT, Locale.US)      //This is the format I need
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")                               //This is the key line which converts the date to UTC which cannot be accessed with the default serializer
    }

    @Synchronized
    override fun serialize(date: Date, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        return JsonPrimitive(dateFormat.format(date))
    }

    @Synchronized
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): Date {
        try {
            return dateFormat.parse(jsonElement.asString)
        } catch (e: ParseException) {
            throw JsonParseException(e)
        }

    }
}
