package com.ksajja.newone.helper

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.webkit.CookieManager
import com.ksajja.newone.Constants
import com.ksajja.newone.newoneApplication
import com.ksajja.newone.extension.getDate
import com.ksajja.newone.model.DeviceInfo
import com.ksajja.newone.util.CLog
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.JwtMap
import java.util.*

/**
 * Created by ksajja on 2/8/18.
 */

object AppHelper {

    val sDeviceId: String by lazy {
        var deviceId = SharedPrefsHelper.appPreferences.getString(Constants.SharedPrefKeys.UNIQUE_DEVICE_ID, null)
        if (deviceId.isNullOrBlank()) {
            //If null, generate new
            deviceId = UUID.randomUUID().toString()
            val editor = SharedPrefsHelper.appPreferences.edit()
            editor.putString(Constants.SharedPrefKeys.UNIQUE_DEVICE_ID, deviceId)
            editor.apply()
        }
        deviceId
    }

    var sAuthToken: String? = null
        get() {
            if (field.isNullOrBlank())
                field = SharedPrefsHelper.appPreferences.getString(Constants.SharedPrefKeys.AUTH_TOKEN, null)
            return field
        }
        set(authToken) {
            //Replace current token if the new token is 'valid' and latest
            if (isValidToken(field, authToken)) {
                field = authToken
                val editor = SharedPrefsHelper.appPreferences.edit()
                editor.putString(Constants.SharedPrefKeys.AUTH_TOKEN, authToken)
                editor.apply()
            }
        }

    private fun isValidToken(currentToken: String?, newToken: String?): Boolean {
        //New token is null or empty - Don't replace the current one
        if (newToken.isNullOrBlank()) return false
        //There is no current token - replace it with new token. No need of date check
        if (currentToken.isNullOrBlank()) return true

        //Both are not null - compare dates and replace if newToken is the latest
        try {
            val newTokenDate = ((Jwts.parser().parse(newToken).body as JwtMap)["createdOn"] as String)
                    .getDate(Constants.DEFAULT_DATE_TIME_FORMAT)
            val currentTokenDate = ((Jwts.parser().parse(currentToken).body as JwtMap)["createdOn"] as String)
                    .getDate(Constants.DEFAULT_DATE_TIME_FORMAT)
            if (newTokenDate != null && newTokenDate > currentTokenDate) return true
        } catch (e: Exception) {
            CLog.e("TokenValidation", e.message, e)
        }
        return false
    }

    var sDeviceToken: String? = null
        get() {
            if (field.isNullOrBlank())
                field = SharedPrefsHelper.appPreferences.getString(Constants.SharedPrefKeys.DEVICE_TOKEN, null)
            return field
        }
        set(deviceToken) {
            field = deviceToken
            val editor = SharedPrefsHelper.appPreferences.edit()
            editor.putString(Constants.SharedPrefKeys.DEVICE_TOKEN, deviceToken)
            editor.apply()
        }

    val deviceInfo: DeviceInfo
        get() {
            val deviceInfo = DeviceInfo()
            deviceInfo.deviceModel = Build.MODEL
            deviceInfo.deviceType = "Android"
            deviceInfo.osVersion = Build.VERSION.RELEASE
            deviceInfo.deviceId = sDeviceId
            deviceInfo.deviceToken = sDeviceToken
            return deviceInfo
        }

    val applicationContext: Context?
        get() = newoneApplication.sContext

    fun logout() {
        SharedPrefsHelper.clearUserSharedPrefs()
        //Remove web view cookies
        val manager = CookieManager.getInstance()
        if (android.os.Build.VERSION.SDK_INT < 21)
            manager.removeAllCookie()
        else
            manager.removeAllCookies(null)
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }


}
