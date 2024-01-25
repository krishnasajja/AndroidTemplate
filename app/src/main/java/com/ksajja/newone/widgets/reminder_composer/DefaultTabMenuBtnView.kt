package com.ksajja.newone.widgets.reminder_composer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.ksajja.newone.widgets.reminder_composer.interfaces.TabMenuButtonAbstract
import com.ksajja.newone.widgets.reminder_composer.models.DefaultTabMenuBtnViewModel
import com.ksajja.newone.databinding.ComposerTabMenuButtonBinding

/**
 * Created by ksajja on 3/3/18.
 */
class DefaultTabMenuBtnView @JvmOverloads constructor(
        context: Context, offButtonResId: Int = 0, onButtonResId: Int = 0
        , unfocusedBgResId: Int = 0, focusedBgResId: Int = 0
        , attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TabMenuButtonAbstract(context, attrs, defStyleAttr) {

    private var mBinding: ComposerTabMenuButtonBinding

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding = ComposerTabMenuButtonBinding.inflate(inflater, this, true)
        mBinding.viewModel = DefaultTabMenuBtnViewModel(offButtonResId, onButtonResId, unfocusedBgResId, focusedBgResId)
    }

    override fun setIsFocused(focused: Boolean) {
        mBinding.viewModel?.setIsFocused(focused)
    }

    override fun setIsActivated(activated: Boolean) {
        mBinding.viewModel?.setIsActivated(activated)
    }
}