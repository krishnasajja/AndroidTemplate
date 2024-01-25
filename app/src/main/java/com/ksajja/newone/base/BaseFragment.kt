package com.ksajja.newone.base


import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.ksajja.newone.R
import com.ksajja.newone.activity.MainActivity
import com.ksajja.newone.interfaces.TouchEventListener
import com.ksajja.newone.widgets.DelayedProgressDialog


/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment(@LayoutRes private val layoutId: Int) : Fragment(), TouchEventListener {

    private var mUnbinder: Unbinder? = null
    private val mProgressDialog = DelayedProgressDialog()
    protected var mBinding: ViewDataBinding? = null

    companion object {
        var mCurrentFragment: BaseFragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? BaseActivity)?.registerTouchListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view: View? = null
        if (layoutId != 0) {

            mBinding = DataBindingUtil.inflate(inflater,
                    layoutId, container, false)
            view = mBinding?.root ?: inflater.inflate(layoutId, container, false)
            mUnbinder = ButterKnife.bind(this, view!!)
        }
        return view
    }

    fun showLoading(loading: Boolean) {
        if (loading)
            mProgressDialog.show(activity!!.supportFragmentManager, "tag")
        else
            mProgressDialog.cancel()
    }

    override fun onStart() {
        super.onStart()
        mCurrentFragment = this
        setupToolBar()

    }

    fun setupToolBar() {
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.title = getToolBarTitle()
        if (shouldShowHomeButton()) {
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setHomeButtonEnabled(true)
            actionBar?.setHomeAsUpIndicator(getHomeButtonDrawableId())
        } else {
            actionBar?.setDisplayHomeAsUpEnabled(false)
            actionBar?.setHomeButtonEnabled(false)
        }
    }

    /**
     * Methods related to toolbar - setting title, setting home button, setting onclick listener for
     * the home button etc.
     */
    open fun getToolBarTitle(): CharSequence? {
        return getString(R.string.app_name)
    }

    protected fun setToolBarTitle(title: String?) {
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.title = title
    }

    open fun getHomeButtonDrawableId(): Int {
        return R.drawable.ic_arrow_back_white_24px
    }

    open fun shouldShowHomeButton(): Boolean {
        return activity?.supportFragmentManager?.backStackEntryCount!! > 1
    }

    open fun onHomeButtonPressed() {
        if ((activity as? MainActivity)?.supportFragmentManager?.backStackEntryCount != null)
            if ((activity as? MainActivity)?.supportFragmentManager?.backStackEntryCount!! > 1) {
                (activity as MainActivity).supportFragmentManager?.popBackStack()
            } else if (((activity as? MainActivity)?.supportFragmentManager?.backStackEntryCount!! == 1)) {
                (activity as MainActivity).finish()
            }
    }

    /**
     * Binding Reset: Fragments have a different view lifecycle than activities.
     * When binding a fragment in onCreateView, set the views to null in onDestroyView.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder?.unbind()
    }

    override fun onDestroy() {
        (activity as? BaseActivity)?.unRegisterTouchListener(this)
        super.onDestroy()
    }

    override fun onTouch(ev: MotionEvent): Boolean {
        return true
    }
}
