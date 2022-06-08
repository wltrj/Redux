package ceneax.app.lib.redux

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlin.reflect.KProperty1

@Suppress("UNCHECKED_CAST")
abstract class ReduxViewModel<S : IReduxState> : ViewModel() {
    private val stateStore: IReduxStateStore<S> = ReduxStateStore(
        viewModelScope,
        MutableStateFlow(this::class.newGenericsInstance(0))
    )

    internal val stateFlow: StateFlow<S> get() = stateStore.stateFlow

    val state: S get() = stateStore.state

//    val stateEmitter = stateStore::setState

    protected fun setState(block: S.() -> S) {
        stateStore.setState(block)
//        stateEmitter.invoke { block() }
    }
}

internal inline fun <S : IReduxState> ReduxViewModel<S>.observeAll(
    owner: LifecycleOwner,
    crossinline block: S.() -> Unit
) = stateFlow.onEach {
    block(stateFlow.value)
}.launchIn(owner.lifecycleScope)

internal inline fun <S : IReduxState> ReduxViewModel<S>.observe(
    owner: LifecycleOwner,
    vararg props: KProperty1<IReduxState, *>,
    crossinline block: S.() -> Unit
) {
    if (props.isEmpty()) {
        return
    }

    stateFlow.map {
        getPropertiesValues(it, *props)
    }.distinctUntilChanged(
        ::arrayAllSame
    ).onEach {
        block(stateFlow.value)
    }.launchIn(owner.lifecycleScope)
}

internal fun <R> getPropertiesValues(
    receiver: R,
    vararg props: KProperty1<R, *>
): Array<*> = Array(props.size) {
    props[it].get(receiver)
}

internal fun arrayAllSame(old: Array<*>, new: Array<*>): Boolean {
    if (old.size != new.size) return false
    for (i in new.indices) {
        if (old[i] != new[i]) {
            return false
        }
    }
    return true
}