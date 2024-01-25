package com.ksajja.newone.network

import com.google.gson.GsonBuilder
import com.ksajja.newone.BuildConfig
import com.ksajja.newone.Constants
import com.ksajja.newone.network.gson.GsonExcludeStrategy
import com.ksajja.newone.network.gson.GsonUTCDateAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by ksajja on 1/29/18.
 */

object APIClient {

    private var mClient: Retrofit? = null

    //
    val apiClient: Retrofit
        @Synchronized get() {
            if (mClient == null) {
                val gson = GsonBuilder()
                        .registerTypeAdapter(Date::class.java, GsonUTCDateAdapter())
                        .setDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT)
                        .addSerializationExclusionStrategy(GsonExcludeStrategy())
                        .create()

                mClient = Retrofit.Builder()
                        .baseUrl(BuildConfig.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(okHttpClient).build()
            }

            return mClient!!
        }


    /**
     * Create a new okhttp client and return it. We can add interceptors authenticators in future in this method
     *
     * @return okhttp client
     */
    private val okHttpClient: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            builder.readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .addNetworkInterceptor(APIInterceptor())

            if (BuildConfig.DEBUG) {
                val logger = HttpLoggingInterceptor()
                logger.level = HttpLoggingInterceptor.Level.BODY
                builder.addNetworkInterceptor(logger)
            }
            return builder.build()
        }
}
