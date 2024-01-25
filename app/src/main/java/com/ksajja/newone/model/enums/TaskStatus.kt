package com.ksajja.newone.model.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by ksajja on 2/19/18.
 */

enum class TaskStatus private constructor(private val status: String) {

    @SerializedName("Completed")
    COMPLETED("Completed"),
    @SerializedName("Open")
    OPEN("Open");

    override fun toString(): String {
        return status
    }
}
