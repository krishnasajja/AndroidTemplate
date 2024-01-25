package com.ksajja.newone.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.webkit.*
import com.ksajja.newone.BuildConfig
import com.ksajja.newone.Constants
import com.ksajja.newone.R
import com.ksajja.newone.base.BaseActivity
import com.ksajja.newone.helper.SharedPrefsHelper
import com.ksajja.newone.network.APICallback
import com.ksajja.newone.network.APIService
import com.ksajja.newone.network.EndPoints.ksajja_LOGIN_URL
import com.ksajja.newone.util.CLog
import kotlinx.android.synthetic.main.activity_login.*
import java.net.HttpCookie
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
class LoginActivity : BaseActivity(R.layout.activity_login) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Show on-boarding if not shown already
        if (SharedPrefsHelper.appPreferences.getBoolean(Constants.SharedPrefKeys.SHOULD_SHOW_ONBOARDING, true)) {
            goToOnboarding()
        }

        //Go to main activity if logged in
        if (!SharedPrefsHelper.loggedInksajjaId.isNullOrEmpty()) {
            goToNextActivity()
        } else {
            loadLoginWebView()
        }
    }

    private fun login(ksajjaUserId: String, ksajjaToken: String) {
        APIService.instance?.login(ksajjaUserId, ksajjaToken)?.enqueue(object : APICallback<Boolean>() {
            override fun onSuccess(value: Boolean) {
                SharedPrefsHelper.loggedInksajjaId = ksajjaUserId
                goToNextActivity()
            }
        })
    }

    private fun goToOnboarding() {
        startActivity(Intent(this@LoginActivity, OnboardingActivity::class.java))
    }

    private fun goToNextActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }


    //AndroidUtils.hideSoftKeyboard(this, phoneEditText);
    //

    private fun loadLoginWebView() {
        loginWebView.webViewClient = LoginWebView()
        loginWebView.settings.javaScriptEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loginWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        loginWebView.clearCache(true)
        loginWebView.loadUrl(ksajja_LOGIN_URL)
    }


    private inner class LoginWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            CLog.i(this, url)

            if (url.startsWith("gettoken://getPlatformToken") || url.startsWith("https://www.shopyourway" + ".com/secured/mp/connect/native?token")) {
                CLog.d(this.javaClass.simpleName, url)
                saveLoginCredentials(url)
                return true
            }

            view.loadUrl(url)
            return false
        }

        private fun saveLoginCredentials(url: String) {
            try {
                val tokenArray = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val tokenString = tokenArray[tokenArray.size - 1]
                val tokenStringArray = tokenString.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val ksajjaUserId = tokenStringArray[0]

                //                AppCredentials.getInstance().setAuthToken(tokenString);
                //                AppCredentials.getInstance().setksajjaUserId(ksajjaUserId);
                //                AppCredentials.getInstance().save();
                login(ksajjaUserId, tokenString)
            } catch (e: Exception) {
                CLog.e(this, e.localizedMessage, e)
            }

        }


        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            val value = "{\"BundleId\":\"" + BuildConfig.APPLICATION_ID + "\",\"LegalURL\":\"openTOCPSC://\"}"
            val cookie = HttpCookie("ksajjaAppContext", value)
            cookie.domain = url
            // This timeout value is defined by IT Team
            cookie.maxAge = Calendar.getInstance().timeInMillis + 2500000
            cookie.version = 0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setCookie(url, cookie.toString())
                CookieManager.getInstance().flush()
            } else {
                val cookieSyncMngr = CookieSyncManager.createInstance(this@LoginActivity)
                cookieSyncMngr.startSync()
                CookieManager.getInstance().setCookie(url, cookie.toString())
                cookieSyncMngr.sync()
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            //Hiding the splash page when there's an error or when the page is done loading.
            //In case of auto-login, the login activity is finished, So, no need to make this change
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            super.onReceivedSslError(view, handler, error)
            //Hiding the splash page when there's an error or when the page is done loading.
            //In case of auto-login, the login activity is finished, So, no need to make this change
            //AnalyticsUtils.sendException("SSLError for device:" + DeviceUtils.getDeviceInfo() + "\nError: " +
            //        String.valueOf(error), true);
        }

    }


}
