package com.ksajja.newone.network.gson

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

/**
 * Created by ksajja on 2/22/18.
 */

class GsonExcludeStrategy : ExclusionStrategy {
    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return f.getAnnotation(Exclude::class.java) != null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return false
    }
}
