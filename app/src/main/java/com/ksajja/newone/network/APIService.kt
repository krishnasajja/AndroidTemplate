package com.ksajja.newone.network

/**
 * Created by ksajja on 1/29/18.
 */

object APIService {

    private val mBackendService: BackendService by lazy {
        APIClient.apiClient.create(BackendService::class.java)
    }

    val instance: BackendService
        @Synchronized get() {
            return mBackendService
        }

}
