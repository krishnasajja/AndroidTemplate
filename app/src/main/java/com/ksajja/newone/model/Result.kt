package com.ksajja.newone.model

/**
 * Created by ksajja on 1/29/18.
 */

class Result<T> {
    var value: T? = null
    var errors: Array<Error>? = null
    var resultCode: Int = 0
    var error: Error? = null
    var message: String? = null
}
