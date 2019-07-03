package com.androidx.annotation

import kotlin.reflect.KClass


/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-06-29 - 15:49
 **/
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ApiService
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Service(val name: String)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Contract(val names:Array<String>)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Module


