package ceneax.app.lib.redux

import android.app.Activity
import android.app.Application
import android.content.Context
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
        app.registerActivityLifecycleCallbacks(ActivityLifecycleCallback())
    }

    /**
     * 提供给外部使用的初始化，用于自定义某些配置
     */
    fun init(
        loadingDialog: IReduxLoadingDialog<*> = ReduxLoadingDialog(),
        modelFactory: ViewModelProvider.Factory = ReduxViewModelFactory()
    ) {
        _loadingDialog = loadingDialog
        _viewModelFactory = modelFactory
    }
}

/**
 * [Description] : Activity生命周期监听类
 *
 * [Date] : 2022-05-27 16:54
 *
 * [Author] : ceneax
 */
internal class ActivityLifecycleCallback : IActivityLifecycleCallback {
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

    (effect as ReduxEffect<*>).let {
        it._stateManager
        it.setBeforeData(bundle ?: Bundle())
    }
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

interface IReduxLoadingDialog<D : DialogFragment> {
    val dialog: D?

    val defaultContent: String

    fun setLoadingContent(content: String)
}

internal class ReduxLoadingDialog : IReduxLoadingDialog<DialogFragment> {
    override val dialog: DialogFragment? = null

    override val defaultContent: String = ""

    override fun setLoadingContent(content: String) {
    }
}