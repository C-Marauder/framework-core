package com.xqy.androidx.framework.model

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-05-25 - 14:42
 **/
data class ApiKeyModel(val pair: Pair<String,Class<*>>,val contentKey:String,val msgKey:String?=null)