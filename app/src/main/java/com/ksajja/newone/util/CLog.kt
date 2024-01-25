package com.ksajja.newone.util

import android.util.Log

import com.google.android.gms.analytics.StandardExceptionParser
import com.ksajja.newone.BuildConfig
import com.ksajja.newone.helper.AnalyticsHelper
import com.ksajja.newone.helper.AppHelper

/**
 * Created by ksajja on 2/8/18.
 */

object CLog {

    private val isLoggingEnabled = BuildConfig.DEBUG

    fun i(tag: String, msg: String) {
        if (isLoggingEnabled) Log.i(tag, msg)
    }

    fun w(tag: String, msg: String) {
        if (isLoggingEnabled) Log.w(tag, msg)
    }

    fun d(tag: String, msg: String) {
        if (isLoggingEnabled) Log.d(tag, msg)
    }

    fun i(o: Any?, msg: String) {
        if (o == null) return
        if (isLoggingEnabled) Log.i(o.javaClass.simpleName, msg)
    }

    fun w(o: Any?, msg: String) {
        if (o == null) return
        if (isLoggingEnabled) Log.w(o.javaClass.simpleName, msg)
    }

    fun d(o: Any?, msg: String) {
        if (o == null) return
        if (isLoggingEnabled) Log.d(o.javaClass.simpleName, msg)
    }

    fun e(o: Any?, msg: String) {
        if (o == null) return
        if (isLoggingEnabled) Log.e(o.javaClass.simpleName, msg)
        //we can put the below line in else statement if we don't want exception analytics on staging
        AnalyticsHelper.sendException(msg)
    }

    fun e(tag: String, msg: String) {
        if (isLoggingEnabled) Log.e(tag, msg)
        //we can put the below line in else statement if we don't want exception analytics on staging
        AnalyticsHelper.sendException(msg)
    }

    fun e(o: Any?, msg: String, e: Throwable) {
        if (o == null) return
        if (isLoggingEnabled) Log.e(o.javaClass.simpleName, msg, e)
        //we can put the below line in else statement if we don't want exception analytics on staging
        AnalyticsHelper.sendException(StandardExceptionParser(AppHelper.applicationContext, null)
                .getDescription(Thread.currentThread().name, e) + ": " + e.message)
    }

    fun e(s: String, msg: String?, e: Throwable) {
        if (isLoggingEnabled) Log.e(s, msg, e)
        //we can put the below line in else statement if we don't want exception analytics on staging
        AnalyticsHelper.sendException(StandardExceptionParser(AppHelper.applicationContext, null)
                .getDescription(Thread.currentThread().name, e) + ": " + e.message)
    }

}
