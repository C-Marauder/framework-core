package com.androidx.frameworkcore.logic

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-07-01 - 17:20
 **/

@Suppress("LeakingThis")
abstract class AppPresenter<T : AppView>(val mView: T) : LifecycleEventObserver {
    val mLifecycleScope: LifecycleCoroutineScope by lazy {
        mView.mLifecycleScope
    }

    init {
        mView.mLifecycle.addObserver(this)

    }

    open fun onUiInit(){}
    open fun onCreated() {}
    open fun onResumed() {}
    open fun onPaused() {}
    open fun onStopped() {}
    open fun onDestroyed() {}

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {

        when (event) {
            Lifecycle.Event.ON_CREATE -> onCreated()
            Lifecycle.Event.ON_START -> onUiInit()
            Lifecycle.Event.ON_RESUME->onResumed()
            Lifecycle.Event.ON_PAUSE->onPaused()
            Lifecycle.Event.ON_STOP -> onStopped()
            Lifecycle.Event.ON_DESTROY->onDestroyed()

            else -> {

            }
        }

        if (mView.mLifecycle.currentState <= Lifecycle.State.DESTROYED) {
            mView.mLifecycle.removeObserver(this)
        }
    }

}

interface AppView {
    private val mLifecycleOwner: LifecycleOwner
        get() {
            return when (this) {
                is AppCompatActivity -> this
                is Fragment -> this
                else -> throw IllegalArgumentException("${AppView::class.java.simpleName} is not subType of AppCompatActivity or Fragment")
            }
        }
    val mLifecycle: Lifecycle get() = mLifecycleOwner.lifecycle

    val mLifecycleScope:LifecycleCoroutineScope get() = mLifecycleOwner.lifecycleScope
}

internal object PresenterManager{
    val mPresenterMap:ConcurrentHashMap<KClass<out AppPresenter<out AppView>>,String> by lazy {
        ConcurrentHashMap<KClass<out AppPresenter<out AppView>>,String>()
    }
}
