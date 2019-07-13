package com.androidx.frameworkcore

import androidx.lifecycle.LifecycleOwner
import com.androidx.frameworkcore.logic.AppView
import com.xqy.androidx.framework.utils.OnError
import com.xqy.androidx.framework.utils.OnLoading
import com.xqy.androidx.framework.utils.UnConnected
import com.xqy.androidx.framework.utils.execute

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-07-02 - 16:46
 **/


fun <Params, Result> AppView.execute(
    service: String,
    params: Params? = null,
    unConnected: UnConnected? = null,
    onLoading: OnLoading? = null,
    onError: OnError? = null,
    onSuccess: (result: Result?) -> Unit
) {
    (this as LifecycleOwner).execute<Params, Result>(service, params, unConnected, onLoading, onError, onSuccess)

}
