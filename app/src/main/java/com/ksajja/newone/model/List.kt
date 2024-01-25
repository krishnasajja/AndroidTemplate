package com.ksajja.newone.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.parceler.Parcel

/**
 * Created by ksajja on 2/16/18.
 */
@Entity
@Parcel
class List {

    @PrimaryKey
    var listId: String? = null
    var name: String? = null
    var source: String? = null
    var sourceId: String? = null
    var isDefault: Boolean? = null
    var taskCount: Int? = null
}
