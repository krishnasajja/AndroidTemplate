package com.ksajja.newone.helper

import android.content.Context
import android.content.SharedPreferences
import com.ksajja.newone.Constants

/**
 * Created by ksajja on 1/31/18.
 */

class SharedPrefsHelper {

    companion object {
        private const val APP_PREFS = "newonePrefs"
        private const val USER_PREFS = "newoneUser"

        private var sApplicationContext: Context? = null

        val appPreferences: SharedPreferences
            get() = sApplicationContext!!.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)

        var loggedInksajjaId: String? = null
            get() {
                if (field.isNullOrBlank())
                    field = appPreferences.getString(Constants.SharedPrefKeys.ksajja_ID, null)
                return field
            }
            set(value) {
                val editor = appPreferences.edit()
                editor.putString(Constants.SharedPrefKeys.ksajja_ID, value)
                editor.apply()
                field = value
            }

        private val userPreferences: SharedPreferences
            get() = sApplicationContext!!.getSharedPreferences(USER_PREFS + loggedInksajjaId, Context.MODE_PRIVATE)

        fun initialize(context: Context) {
            sApplicationContext = context
        }

        fun clearUserSharedPrefs() {
            //Clear stored ksajjaId data
            loggedInksajjaId = null
            appPreferences.edit().remove(Constants.SharedPrefKeys.ksajja_ID).apply()

            //Clear all shared preferences of that user
            userPreferences.edit().clear().apply()
        }
    }

}
