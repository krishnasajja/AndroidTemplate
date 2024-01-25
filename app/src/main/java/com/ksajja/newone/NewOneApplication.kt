package com.ksajja.newone

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

import com.ksajja.newone.helper.SharedPrefsHelper

/**
 * Created by ksajja on 1/26/18.
 */

class NewOneApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        sContext = this
        SharedPrefsHelper.initialize(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var sContext: Context? = null
    }
}
