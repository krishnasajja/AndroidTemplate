package com.ksajja.newone.room

import androidx.room.Query

/**
 * Created by ksajja on 2/25/18.
 */

interface ListDao {

    @get:Query("SELECT * from List")
    val lists: List<com.ksajja.newone.model.List>
}
