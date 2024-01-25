package com.ksajja.newone.widgets.reminder_composer.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.android.databinding.library.baseAdapters.BR
import com.ksajja.newone.R

/**
 * Created by ksajja on 3/2/18.
 */
class DefaultTabMenuBtnViewModel(
        private var offButtonResId: Int = 0, private var onButtonResId: Int = 0
        , private var unfocusedBgResId: Int = 0, private var focusedBgResId: Int = 0) : BaseObservable() {

    private var isFocusedMode: Boolean = false
    private var isActivatedMode: Boolean = false

    fun setIsFocused(focused: Boolean) {
        isFocusedMode = focused
        notifyPropertyChanged(BR.buttonIconRes)
        notifyPropertyChanged(BR.backgroundRes)
        notifyPropertyChanged(BR.buttonTint)
    }

    fun setIsActivated(activated: Boolean) {
        isActivatedMode = activated
    }

    @Bindable
    fun getButtonIconRes(): Int {
        return if (isFocusedMode || isActivatedMode) onButtonResId else offButtonResId
    }

    @Bindable
    fun getBackgroundRes(): Int {
        return if (isFocusedMode) focusedBgResId else unfocusedBgResId
    }

    //temp code
    @Bindable
    fun getButtonTint(): Int {
        return if (isFocusedMode || isActivatedMode) R.color.black else R.color.grey_600
    }
}