package ceneax.app.lib.redux

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

object Redux {
    private lateinit var _application: Application
    internal val application get() = _application

    private var _viewModelFactory: ViewModelProvider.Factory = ReduxViewModelFactory()
    internal val viewModelFactory: ViewModelProvider.Factory get() = _viewModelFactory

    private var _loadingDialog: IReduxLoadingDialog<*> = ReduxLoadingDialog()
    internal val loadingDialog: IReduxLoadingDialog<*> get() = _loadingDialog

    /**
     * 框架内部自动初始化方法，用于获取 Application 注册生命周期
     */
    internal fun init(app: Application) {
        _application = app
        _application.registerActivityLifecycleCallbacks(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityLifecycleCallbackQ()
        } else {
            ActivityLifecycleCallback()
        })
    }

    /**
     * 提供给外部使用的初配置方法，用于自定义某些配置
     */
    fun config(
        loadingDialog: IReduxLoadingDialog<*>? = null,
        modelFactory: ViewModelProvider.Factory? = null
    ) {
        loadingDialog?.let {
            _loadingDialog = it
        }
        modelFactory?.let {
            _viewModelFactory = it
        }
    }
}

/**
 * [Description] : Activity生命周期监听类，低于 Android Q 的版本
 *
 * [Date] : 2022-05-27 16:54
 *
 * [Author] : ceneax
 */
internal class ActivityLifecycleCallback : IActivityLifecycleCallback {
    /**
     * 最开始用的是 [onActivityPreCreated]，后面发现 Android Q 之前的版本没有 [onActivityPreCreated] 回调
     *
     * 并且也没有 [onActivityPostCreated] 回调，所以低于 Android Q 的版本只能用 [onActivityCreated] 处理初始化
     */
    @Suppress("UNCHECKED_CAST")
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (activity !is IReduxView<*, *>) return
        if (activity !is FragmentActivity) return

        initReduxByBeforeCreate(activity, activity.intent?.extras)

        (activity as FragmentActivity).supportFragmentManager
            .registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacksImpl(), true)

        (activity as IReduxView<IReduxState, *>).let {
            it.effect._stateManager.observeAll(activity) {
                it.invalidate(this)
            }
        }
    }
}

/**
 * [Description] : Activity生命周期监听类，高于 Android Q 的版本
 *
 * [Date] : 2022-05-27 16:54
 *
 * [Author] : ceneax
 */
internal class ActivityLifecycleCallbackQ : IActivityLifecycleCallback {
    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity !is IReduxView<*, *>) return
        if (activity !is FragmentActivity) return

        initReduxByBeforeCreate(activity, activity.intent?.extras)

        (activity as FragmentActivity).supportFragmentManager
            .registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacksImpl(), true)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity !is IReduxView<*, *>) return
        if (activity !is FragmentActivity) return

        (activity as IReduxView<IReduxState, *>).let {
            it.effect._stateManager.observeAll(activity) {
                it.invalidate(this)
            }
        }
    }
}

/**
 * [Description] : Fragment生命周期监听类
 *
 * [Date] : 2022-05-27 16:54
 *
 * [Author] : ceneax
 */
internal class FragmentLifecycleCallbacksImpl : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
        if (f !is IReduxView<*, *>) return

        initReduxByBeforeCreate(f, f.arguments)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        if (f !is IReduxView<*, *>) return

        (f as IReduxView<IReduxState, *>).let {
            it.effect._stateManager.observeAll(f) {
                it.invalidate(this)
            }
        }
    }
}

/**
 * 初始化 [Redux]
 */
private fun <T : IReduxView<*, *>> initReduxByBeforeCreate(target: T, bundle: Bundle?) {
    val reduxViewGeneric = target::class.java.genericInterfaces.find {
        IReduxView::class.java.isAssignableFrom((it as ParameterizedType).rawType as Class<*>)
    } ?: error("${target::class.java.simpleName} 必须实现 IReduxView 接口")

    val effect = reduxViewGeneric.getActualTypeClass(1).newInstance().also {
        it::class.java.getSuperEffectClass().setDeclaredField(it, "mReduxView", target)
    }

    val reduxViewDelegate = target::class.java.declaredFields.find {
        it.type.isAssignableFrom(ReduxView::class.java) && it.name.startsWith("$\$delegate_")
    } ?: error("${target::class.java.simpleName} 的 IReduxView 必须委托于 ReduxView")

    reduxViewDelegate.isAccessible = true
    reduxViewDelegate[target].let {
        it::class.java.setDeclaredField(it!!, "effect", effect)
    }

    (effect as ReduxEffect<*, *>)._stateManager.setBeforeData(bundle ?: Bundle())
}

/**
 * [Description] : 框架默认的[ViewModelFactory]
 *
 * [Date] : 2022-05-27 16:51
 *
 * [Author] : ceneax
 */
internal class ReduxViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.newInstance()
    }
}