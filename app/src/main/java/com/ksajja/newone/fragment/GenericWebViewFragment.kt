package com.ksajja.newone.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ksajja.newone.R
import com.ksajja.newone.base.BaseFragment
import com.ksajja.newone.extension.isNotNullNorEmpty
import kotlinx.android.synthetic.main.fragment_generic_web_view.*


class GenericWebViewFragment : BaseFragment(R.layout.fragment_generic_web_view) {
    private var mAlertDialog: AlertDialog? = null
    private var mUrl: String? = null
    protected var toolbarTitle: String? = null
        private set
    private var mRootView: View? = null

    protected val rootViewId: Int
        get() = R.id.parent_view

    val screenName: String
        get() = SCREEN_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        loadArguments()
    }

    private fun loadArguments() {
        mUrl = arguments!!.getString(WEBVIEW_URL)
        toolbarTitle = arguments!!.getString(WEBVIEW_TITLE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null)
            mAlertDialog = AlertDialog.Builder(activity).create()
        loadWebView()
    }

    override fun onResume() {
        super.onResume()
        setLoading(true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected fun loadWebView() {
        genericWebView.webViewClient = GenericWebView()
        genericWebView.settings.javaScriptEnabled = true
        genericWebView.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            genericWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        genericWebView.loadUrl(mUrl)
    }

    private inner class GenericWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            setLoading(false)
        }

        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse {
            return super.shouldInterceptRequest(view, url)
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            setLoading(false)
            if (mAlertDialog != null) {
                mAlertDialog!!.setTitle("Error")
                mAlertDialog!!.setMessage(description)
                mAlertDialog!!.setButton("Retry") { dialog, which -> genericWebView.loadUrl(mUrl) }
                mAlertDialog!!.show()
            }
        }
    }

    private fun setLoading(start: Boolean) {
        if (activity == null || activity!!.isFinishing)
            return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity!!.isDestroyed)
                return
        }
        showLoading(start)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                if (fragmentManager != null)
                    fragmentManager!!.popBackStackImmediate()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        val TAG = GenericWebViewFragment::class.java.simpleName
        val WEBVIEW_URL = "webViewUrl"
        private val SCREEN_NAME = "Web View"

        val WEBVIEW_TITLE = "webview_title"

        fun newInstance(url: String?, title: String): GenericWebViewFragment {
            val fragment = GenericWebViewFragment()

            val args = Bundle()
            if(url.isNotNullNorEmpty())
            args.putString(WEBVIEW_URL, url)
            args.putString(WEBVIEW_TITLE, title)
            fragment.arguments = args

            return fragment
        }
    }
}// Required empty public constructor
