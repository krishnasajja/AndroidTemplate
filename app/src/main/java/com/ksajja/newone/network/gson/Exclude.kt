package com.ksajja.newone.network.gson

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotation to be used on Fields for which we don't want to serialise
 * Created by ksajja on 2/21/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Exclude
