package com.ksajja.newone.widgets.reminder_composer.interfaces

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.ksajja.newone.widgets.reminder_composer.listener.ComposerEditListener

/**
 * Created by ksajja on 3/1/18.
 */
abstract class SecondaryEditView<T> @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    protected var mIsInEdit = false
    protected var mOnEditListener: ComposerEditListener? = null

    abstract var TAG: String
    abstract fun getData(): T
    abstract fun setData(dataInput: T)

    open fun clearAllFocus() {

    }

    fun setEditListener(listener: ComposerEditListener?){
        mOnEditListener = listener
    }

    fun isInEdit(): Boolean {
        return mIsInEdit
    }
}