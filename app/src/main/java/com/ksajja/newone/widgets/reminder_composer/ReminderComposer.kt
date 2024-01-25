package com.ksajja.newone.widgets.reminder_composer

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import com.ksajja.newone.widgets.reminder_composer.adapter.EditViewPagerAdapter
import com.ksajja.newone.widgets.reminder_composer.interfaces.SecondaryEditView
import com.ksajja.newone.widgets.reminder_composer.interfaces.TabMenuButtonAbstract
import com.ksajja.newone.widgets.reminder_composer.listener.ComposerStateChangeListener
import com.ksajja.newone.widgets.reminder_composer.models.ReminderComposerViewModel
import com.ksajja.newone.R
import com.ksajja.newone.databinding.ReminderComposerBinding

import com.ksajja.newone.widgets.reminder_composer.listener.ComposerClickerActionListener
import com.ksajja.newone.widgets.reminder_composer.listener.ComposerEditListener
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by ksajja on 3/1/18.
 */

class ReminderComposer @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    companion object {
        val STATE_HIDE_ALL: Int = 0
        val STATE_SHOW_TAB_MENU: Int = 1
        val STATE_SHOW_ALL: Int = 2

        val CLICK_EVENT_SUBMIT = "CLICK_EVENT_SUBMIT"
    }

    private var mBinding: ReminderComposerBinding
    private var mEditViewList: LinkedList<SecondaryEditView<*>>? = null
    private var mTabBarMenuItems: LinkedList<TabMenuButtonAbstract>? = null
    private var mStateChangeListener: ComposerStateChangeListener? = null
    private var mClickListener: ComposerClickerActionListener? = null
    private var mEditListener: ComposerEditListener? = null
    private var mCurState = STATE_HIDE_ALL


    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding = ReminderComposerBinding.inflate(inflater, this, true)
        mBinding.viewModel = ReminderComposerViewModel()
        mBinding.view = this
        mEditViewList = LinkedList()
        mTabBarMenuItems = LinkedList()
    }

    init {
        mBinding.reminderInput.onFocusChangeListener = OnFocusChangeListener({ _: View, hasFocus: Boolean ->
            if (hasFocus) {
                mEditListener?.onEditStart()
                changeState(STATE_SHOW_TAB_MENU)
            }
        })
    }

    fun <T> with(tabBarItem: TabMenuButtonAbstract
                 , secondaryEditView: SecondaryEditView<T>): ReminderComposer {
        mEditViewList!!.add(secondaryEditView)
        mTabBarMenuItems!!.add(tabBarItem)
        return this
    }

    fun build() {
        mBinding.viewPager.adapter = EditViewPagerAdapter(mEditViewList)
        mBinding.viewPager.offscreenPageLimit = if (mEditViewList!!.size / 2 > 1) mEditViewList!!.size / 2 else 1
        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val item = mTabBarMenuItems!![position]
                setTabSelected(item)
            }
        })
        mTabBarMenuItems!!.forEach { menuItem -> addTabMenuItem(menuItem) }
        mEditViewList!!.forEach { it.setEditListener(mEditListener) }
    }

    private fun setTabSelected(item: TabMenuButtonAbstract) {
        item.setIsFocused(true)
        for (i in 0 until mTabBarMenuItems!!.size) {
            if (mTabBarMenuItems!![i] != item) {
                mTabBarMenuItems!![i].setIsFocused(false)
                mEditViewList!![i].clearAllFocus();
            }
        }

        mTabBarMenuItems!!.forEach {
            if (it != item) it.setIsFocused(false)
        }
    }

    private fun addTabMenuItem(item: TabMenuButtonAbstract) {
        var lp = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.setMargins(15, 0, 15, 0)
        item.layoutParams = lp
        item.setOnClickListener({
            val index = mTabBarMenuItems!!.indexOf(item)
            setTabSelected(item)
            mBinding.viewPager.setCurrentItem(index, false)

            mBinding.reminderInput.clearFocus()
            val imm = mBinding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mBinding.reminderInput.windowToken, 0)

            changeState(STATE_SHOW_ALL)
        })
        mBinding.tabButtonsContent.addView(item);
    }

    fun hideEditPage() {
        mBinding.reminderInput.clearFocus()
        changeState(STATE_HIDE_ALL)
        val imm = mBinding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mBinding.reminderInput.windowToken, 0)
    }

    fun dismiss() {
        mBinding.reminderInput.setText("")
        mBinding.reminderInput.clearFocus()
        hideEditPage()
    }

    fun setStateChangeListener(listener: ComposerStateChangeListener): ReminderComposer {
        mStateChangeListener = listener;
        return this
    }

    fun setOnClickListener(listener: ComposerClickerActionListener): ReminderComposer {
        mClickListener = listener
        return this
    }

    fun setOnEditListener(listener: ComposerEditListener): ReminderComposer {
        mEditListener = listener
        return this
    }

    fun setEditContentHeight(height: Int) {
        mBinding.viewModel!!.setEditContentHeight(height.toFloat())
    }

    fun onItemClicked(v: View) {
        if (v.id == R.id.add_button) {
            mClickListener?.onClicked(CLICK_EVENT_SUBMIT, buildReminderTask())
        }
    }

    private fun buildReminderTask(): HashMap<String, Any?> {
        val res = HashMap<String, Any?>()
        res.put("Title", mBinding.viewModel?.taskTitle!!.get())
        mEditViewList?.forEach { res.put(it.TAG, it.getData()) }
        return res
    }

    fun hasChildInEdit(): Boolean {
        mEditViewList?.forEach { if (it.isInEdit()) return true }
        return false
    }

    fun curState(): Int {
        return mCurState
    }

    private fun changeState(state: Int) {
        if (state != STATE_HIDE_ALL && state < mCurState) return
        when (state) {
            STATE_HIDE_ALL -> {
                mBinding.viewModel!!.showEditTabMenu(false)
                mBinding.viewModel!!.showEditContent(false)
                mBinding.viewModel!!.showBiggerInputArea(false)
                mBinding.viewPager.setCurrentItem(0, false)
                mTabBarMenuItems!!.forEach { it.setIsFocused(false) }
            }
            STATE_SHOW_TAB_MENU -> {
                mBinding.viewModel!!.showEditTabMenu(true)
                mBinding.viewModel!!.showBiggerInputArea(true)
                mBinding.viewModel!!.showEditContent(false)
            }
            STATE_SHOW_ALL -> {
                mBinding.viewModel!!.showEditTabMenu(true)
                mBinding.viewModel!!.showBiggerInputArea(true)
                mBinding.viewModel!!.showEditContent(true)
            }
        }
        mCurState = state
        mStateChangeListener?.onComposerStateChange(state)
    }
}