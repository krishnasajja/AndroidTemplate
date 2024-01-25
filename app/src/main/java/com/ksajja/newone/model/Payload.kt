package com.ksajja.newone.model

import android.net.Uri

/**
 * Created by ksajja on 2/8/18.
 */

class Payload : BaseModel() {

    var alertMessage: String? = null
    var url: String? = null
    var sentTime: String? = null
    var creatorUserId: String? = null
    var urlScheme: String? = null
    var urlHost: String? = null
    var urlPaths: ArrayList<String>? =null
    var notificationType: String? = null
    var silent: Boolean = false

    fun parseUrl() {
        if (url != null) {
            val uri = Uri.parse(url)
            urlScheme = uri.scheme
            urlHost = uri.host
            urlPaths = uri.pathSegments as ArrayList<String>?
        }
    }
}
