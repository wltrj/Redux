package ceneax.app.lib.redux

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import kotlin.reflect.KProperty1

/**
 * [Description] : 该接口定义了 [View层] 的相关行为，[Activity] 或 [Fragment] 实现该接口即可
 *
 * [Date] : 2022/5/12 4:22
 *
 * [Author] : ceneax
 */
interface IReduxView<S : IReduxState, E : ReduxEffect<*>> {
    // 用于访问 Effect 层相关属性和方法
    val effect: E

    // 当 State 刷新时回调该方法，View 层覆写此方法即可进行 UI 刷新操作
    fun invalidate(state: S)
}

/**
 * [IReduxView] 的默认委托类
 */
class ReduxView<S : IReduxState, E : ReduxEffect<*>> : IReduxView<S, E> {
    @Suppress("UNCHECKED_CAST")
    override val effect: E = object : ReduxEffect<ReduxViewModel<*>>() {} as E

    override fun invalidate(state: S) {}
}

internal inline val IReduxView<*, *>.activity: FragmentActivity
    get() = if (this is FragmentActivity) this else (this as Fragment).requireActivity()

internal inline val IReduxView<*, *>.fragmentManager: FragmentManager
    get() = if (this is FragmentActivity) supportFragmentManager else (this as Fragment).parentFragmentManager

internal inline val IReduxView<*, *>.lifecycleOwner: LifecycleOwner
    get() = this as LifecycleOwner

internal inline val IReduxView<*, *>.viewModelStoreOwner: ViewModelStoreOwner
    get() = this as ViewModelStoreOwner

/**
 * 提供给 [View层] 调用的 [State] 监听方法，用于单独监听 [State] 类中某一个属性值的变更
 */
@Suppress("UNCHECKED_CAST")
fun <S : IReduxState, E : ReduxEffect<*>> IReduxView<S, E>.observe(
    vararg props: KProperty1<S, *>,
    block: S.() -> Unit
) {
    effect._stateManager.observe(
        this as LifecycleOwner,
        *props as Array<KProperty1<IReduxState, *>>,
        block = block as IReduxState.() -> Unit
    )
}