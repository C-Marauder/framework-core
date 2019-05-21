package com.androidx.frameworkcore.mvi
import androidx.lifecycle.*

@Suppress("LeakingThis")
abstract class AppPresenter<T : AppView>(val mView: T) : LifecycleObserver {
    lateinit var mLifecycleScope:LifecycleCoroutineScope
    init {
        if (mView is LifecycleOwner) {
            mLifecycleScope = mView.lifecycleScope
            mView.lifecycle.addObserver(this)
        }

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreated(){}
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStarted(){}
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResumed(){}
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPaused(){}
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStopped(){}
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroyed(){}

}