package com.ksajja.newone.widgets.reminder_composer.models

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.viewpager.widget.ViewPager

/**
 * Created by ksajja on 3/4/18.
 */
class ReminderComposerViewModel {
    var showEditTabMenu: ObservableField<Boolean> = ObservableField()
    var showEditContent: ObservableField<Boolean> = ObservableField()
    var showBiggerInputArea: ObservableField<Boolean> = ObservableField()
    val taskTitle: ObservableField<String> = ObservableField()
    private var editContentHeight: Float = 0f

    init {
        showEditTabMenu.set(false)
        showEditContent.set(false)
    }

    fun setEditContentHeight(height: Float) {
        editContentHeight = height
    }

    fun getEditContentHeight(): Float {
        return editContentHeight
    }

    fun showEditTabMenu(show: Boolean) {
        showEditTabMenu.set(show)
    }

    fun showEditContent(show: Boolean) {
        showEditContent.set(show)
    }

    fun showBiggerInputArea(show: Boolean) {
        showBiggerInputArea.set(show)
    }
}

@BindingAdapter("height")
fun setLayoutHeight(view: ViewPager, height: Float) {
    val layoutParams = view.layoutParams
    layoutParams.height = height.toInt()
    view.layoutParams = layoutParams
}