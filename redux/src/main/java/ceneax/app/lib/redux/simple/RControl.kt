package ceneax.app.lib.redux.simple

import ceneax.app.lib.redux.ReduxLoadingDialogContext
import ceneax.app.lib.redux.loadingScope
import ceneax.app.lib.redux.newGenericsInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class RControl<S : RState> @JvmOverloads constructor(
    private val context: RContext,
    private val stateScope: RStateScope = RStateScope.EXCLUSIVE
) : RLifecycle {
    internal val internalContext get() = context

    @PublishedApi
    @Suppress("UNCHECKED_CAST")
    internal val stateStore = createStateStore(
        context,
        this::class.newGenericsInstance(0),
        stateScope
    ) as RStateStore<S>

    val state: S get() = stateStore.state

    protected inline fun setState(crossinline newState: S.() -> S) = stateStore.setState {
        newState()
    }

    suspend fun <T> loadingScope(
        block: suspend CoroutineScope.(ReduxLoadingDialogContext) -> T
    ): T = internalContext.loadingScope(block)
}

fun RControl<out RState>.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = internalContext.coroutineScope.launch(context, start, block)