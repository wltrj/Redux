package ceneax.app.lib.redux

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

enum class ReduxReducerOwner {
    SELF,
    HOST
}

data class EffectContext(
    val activity: FragmentActivity,
    val fragmentManager: FragmentManager,
    val lifecycleOwner: LifecycleOwner,
    val viewModelStoreOwner: ViewModelStoreOwner
) {
    internal val loadingDialogContext by lazy(LazyThreadSafetyMode.NONE) {
        ReduxLoadingDialogContext(
            dialogInstance = Redux.loadingDialog.dialog.newInstance(),
            reduxLoadingDialog = Redux.loadingDialog
        )
    }
}

abstract class ReduxEffect<RR : ReduxReducer<*>, RS : IReduxSlot>(
    private val reducerOwner: ReduxReducerOwner = ReduxReducerOwner.SELF
) {
    private lateinit var mReduxView: IReduxView<*, *>

    protected val ctx: EffectContext by lazy(LazyThreadSafetyMode.NONE) {
        EffectContext(
            activity = mReduxView.activity,
            fragmentManager = mReduxView.fragmentManager,
            lifecycleOwner = mReduxView.lifecycleOwner,
            viewModelStoreOwner = mReduxView.viewModelStoreOwner
        )
    }

    protected val slot: RS = this::class.newGenericsInstance(1)

    @Suppress("UNCHECKED_CAST")
    internal val _stateManager: RR by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(
            when (reducerOwner) {
                ReduxReducerOwner.HOST -> mReduxView.activity
                else -> mReduxView.viewModelStoreOwner },
            Redux.viewModelFactory
        )[this::class.getGenericsClass(0)]
    }
    val stateManager: RR get() = _stateManager

    protected inline fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        crossinline block: suspend CoroutineScope.() -> Unit
    ): Job = ctx.lifecycleOwner.lifecycleScope.launch(context) {
        block()
    }

    suspend fun <T> loadingScope(
        block: suspend CoroutineScope.(ReduxLoadingDialogContext) -> T
    ): T = ctx.loadingScope(block)
}

internal class EmptyEffect : ReduxEffect<EmptyReducer, EmptyReduxSlot>()