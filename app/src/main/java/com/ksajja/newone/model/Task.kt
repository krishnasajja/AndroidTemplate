package com.ksajja.newone.model

import com.ksajja.newone.model.enums.TaskStatus
import com.ksajja.newone.network.gson.Exclude
import java.util.*

/**
 * Created by ksajja on 1/25/18.
 */
//@Entity(foreignKeys = @ForeignKey(entity = com.ksajja.newone.model.List.class,
//                                parentColumns = "listId",
//                                childColumns = "listId",
//                                onDelete = CASCADE))
class Task(var title: String) : BaseModel() {

    var taskId: String? = null

    var listId: String? = null
    var source: String? = null
    var sourceId: String? = null
    //public String assistantStatus;
    var assistantUserId: String? = null
    var collaborators: ArrayList<String>? = null
    var creatorUserId: String? = null
    var note: String? = null
    var taskStatus: TaskStatus? = null
    var reminderDate: Date? = null
    var dueDate: Date? = null
    var priority: String? = null
    @Exclude
    var packages: ArrayList<Package>? = null
    @Exclude
    var isSelected = false

    var cannedMessage: Template? = null
}
