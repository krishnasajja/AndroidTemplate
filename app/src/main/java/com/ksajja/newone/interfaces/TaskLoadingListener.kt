package com.ksajja.newone.interfaces

import com.ksajja.newone.model.Task

/**
 * Created by ksajja on 3/2/18.
 */
interface TaskLoadingListener {

    fun onDataReady(taskList: List<Task>)
}