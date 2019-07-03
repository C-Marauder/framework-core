package com.xqy.androidx.framework

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-06-23 - 01:06
 **/
interface DatabaseCallback<R> {
    fun shouldFetch(result: R?):Boolean
    fun loadFromDB(): R?
    fun saveResult( result: R)
}