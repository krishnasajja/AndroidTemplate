package com.ksajja.newone.service

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.ksajja.newone.helper.AppHelper
import com.ksajja.newone.model.DeviceInfo
import com.ksajja.newone.network.APICallback
import com.ksajja.newone.network.APIService

/**
 * Created by ksajja on 2/4/18.
 */

class FCMInstanceIdListenerService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val token = FirebaseInstanceId.getInstance().token
        //Save in Shared Pref
        AppHelper.sDeviceToken = token
        //Send device token to Server
        val deviceInfo = AppHelper.deviceInfo
        deviceInfo.deviceToken = token
        APIService.instance.sendDeviceInfo(deviceInfo).enqueue(object : APICallback<DeviceInfo>() {
        })
    }

}
