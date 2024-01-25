package com.ksajja.newone.network

import android.widget.Toast
import com.google.gson.Gson
import com.ksajja.newone.helper.AppHelper
import com.ksajja.newone.model.Result
import com.ksajja.newone.util.CLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by ksajja on 1/29/18.
 */

open class APICallback<T> : Callback<Result<T>> {
    override fun onResponse(call: Call<Result<T>>, response: Response<Result<T>>) {
        if (response.isSuccessful) {
            val result = response.body()
            if (result?.value != null) {
                onSuccess(result.value as T)
            }
        } else {
            val result = getErrorResult(response)
            if (result?.errors != null && result.errors!!.isNotEmpty()) {
                Toast.makeText(AppHelper.applicationContext, result.errors!![0].message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(AppHelper.applicationContext, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFailure(call: Call<Result<T>>, t: Throwable) {
        if (!t.localizedMessage.isNullOrBlank())
            Toast.makeText(AppHelper.applicationContext, t.localizedMessage, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(AppHelper.applicationContext, "Network Error!", Toast.LENGTH_SHORT).show()
    }

    open fun onSuccess(value: T) {

    }

    //onError

    private fun getErrorResult(response: Response<Result<T>>): Result<*>? {
        try {
            if (response.errorBody() != null) {
                return Gson().fromJson(response.errorBody()!!.charStream(), Result::class.java)
            }
        } catch (e: Exception) {
            CLog.e(this.javaClass.simpleName, e.message, e)
        }

        return null
    }


}
