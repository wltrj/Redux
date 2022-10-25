package ceneax.app.lib.redux.simple

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal interface IRStateStore<S : RState> {
    val stateFlow: StateFlow<S>
    val state: S get() = stateFlow.value

    fun setState(newState: S.() -> S)
}

@PublishedApi
internal class RStateStore<S : RState>(
    override val stateFlow: MutableStateFlow<S>
) : IRStateStore<S>, ViewModel() {
    override fun setState(newState: S.() -> S) {
        stateFlow.tryEmit(newState(state))
    }
}

enum class RStateScope {
    EXCLUSIVE,
    SHARED
}