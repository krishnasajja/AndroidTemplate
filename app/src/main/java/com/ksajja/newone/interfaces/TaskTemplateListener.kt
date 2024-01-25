package com.ksajja.newone.interfaces

import com.ksajja.newone.model.TemplateItem

/**
 * Created by shivakartik on 3/8/18.
 */
interface TaskTemplateListener {
    fun onTemplateItemClick(templateItem: TemplateItem?)
    fun onMarkAsDoneClicked(markAsDone:Boolean)
}