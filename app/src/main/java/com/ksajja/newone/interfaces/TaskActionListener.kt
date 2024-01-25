package com.ksajja.newone.interfaces

import com.ksajja.newone.model.Task

/**
 * Created by ksajja on 1/30/18.
 */

interface TaskActionListener {

    fun onTaskClick(task: Task)

    fun onTaskDone(task: Task)

    fun onTaskUndone(task: Task)

    fun onTaskDelete(task: Task, groupPosition: Int, childPosition: Int)

    fun onCannedMessageClick(task: Task)
}
