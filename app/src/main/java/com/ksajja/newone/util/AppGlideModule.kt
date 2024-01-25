package com.ksajja.newone.util

/**
 * Created by shivakartik on 3/8/18.
 */
import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class AppGlideModule : AppGlideModule(){
    override fun applyOptions(context: Context?, builder: GlideBuilder?) {
        // Apply options to the builder here.
        builder?.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
        builder?.setDiskCache(InternalCacheDiskCacheFactory(context))
    }
}