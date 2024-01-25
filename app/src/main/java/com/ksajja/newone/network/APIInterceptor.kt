package com.ksajja.newone.network

import com.ksajja.newone.helper.AppHelper
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor class to add headers and handle authorization tokens
 * Note: When an interceptor throws an exception, okhttp calls the onFailure callback
 * Created by ksajja on 1/29/18.
 */

class APIInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //Handle Outgoing request
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
        setHeaders(builder)
        val newRequest = builder.build()
        val response = chain.proceed(newRequest)

        //Handle incoming response
        saveAuthToken(response.headers().get("Authorization"))

        return response
    }

    private fun setHeaders(builder: Request.Builder) {
        //Header values can not be null
        AppHelper.sAuthToken?.let { builder.header("Authorization", "Bearer ${AppHelper.sAuthToken}") }
        builder.header("deviceId", com.ksajja.newone.helper.AppHelper.sDeviceId)
    }

    private fun saveAuthToken(token: String?) {
        //Save the auth token only if the created time of the new one is greater than existing one
        //In other words - Save only if it's new
        AppHelper.sAuthToken = token
    }

}
