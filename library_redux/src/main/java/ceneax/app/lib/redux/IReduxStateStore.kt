package ceneax.app.lib.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * [Description] : 该 [interface] 定义了 [Redux] 框架状态管理的行为逻辑
 *
 * [Date] : 2022/5/12 3:50
 *
 * [Author] : ceneax
 */
internal interface IReduxStateStore<S : IReduxState> {
    // 用于存储和获取状态的 Flow ，只有子类实现该属性时使用 MutableStateFlow 才可进行 emit 操作
    val stateFlow: StateFlow<S>

    // 仅用来获取 Flow 中已经存储的 State
    val state: S get() = stateFlow.value

    /**
     * 该方法用于实现状态刷新的逻辑
     *
     * [block] 函数参数，返回一个新的 [State] 用于后续的刷新操作
     */
    fun setState(block: S.() -> S)
}

/**
 * [Description] : 该类实现了 [IReduxStateStore] 接口
 *
 * [Date] : 2022/5/12 4:16
 *
 * [Author] : ceneax
 */
internal class ReduxStateStore<S : IReduxState>(
    // 协程作用域，启动一个协程用于对 Flow 进行 emit 操作
    private val scope: CoroutineScope,
    // 这里实现了 stateFlow 属性，并将 StateFlow 转为了 MutableStateFlow
    override val stateFlow: MutableStateFlow<S>
    ) : IReduxStateStore<S> {
    override fun setState(block: S.() -> S) {
        scope.launch {
            stateFlow.emit(block(state))
        }
    }
}