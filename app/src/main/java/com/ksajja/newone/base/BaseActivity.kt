package com.ksajja.newone.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import butterknife.ButterKnife
import com.ksajja.newone.interfaces.TouchEventListener
import com.ksajja.newone.widgets.DelayedProgressDialog
import java.util.*


/**
 * Created by ksajja on 2/4/18.
 */

abstract class BaseActivity(@LayoutRes private val layoutId: Int) : AppCompatActivity() {
    private val onTouchListeners = LinkedList<TouchEventListener>()
    protected var mBinding: ViewDataBinding? = null
    private val mProgressDialog = DelayedProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        mBinding = DataBindingUtil.setContentView(this, layoutId)
        ButterKnife.bind(this)
    }

    fun registerTouchListener(listener: TouchEventListener) {
        onTouchListeners.add(listener)
    }

    fun unRegisterTouchListener(listener: TouchEventListener) {
        onTouchListeners.remove(listener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        for (listener in onTouchListeners) {
            listener.onTouch(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun showLoading(loading: Boolean) {
        if (loading)
            mProgressDialog.show(supportFragmentManager, "tag")
        else
            mProgressDialog.cancel()
    }
}
