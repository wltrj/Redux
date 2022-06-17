package ceneax.app.lib.redux

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class ReduxEffect<RR : ReduxReducer<*>, RS : IReduxSlot> {
    private lateinit var mReduxView: IReduxView<*, *>

    protected val ctx: EffectContext by lazy(LazyThreadSafetyMode.NONE) {
        EffectContext(
            activity = mReduxView.activity,
            lifecycleOwner = mReduxView.lifecycleOwner,
            fragmentManager = mReduxView.fragmentManager
        )
    }

    protected val slot: RS = this::class.newGenericsInstance(1)

    @Suppress("UNCHECKED_CAST")
    internal val _stateManager: RR by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(
            mReduxView.viewModelStoreOwner,
            Redux.viewModelFactory
        )[this::class.getGenericsClass(0)]
    }
    protected val stateManager: RR get() = _stateManager

    internal fun setBeforeData(data: Bundle) = data.keySet().forEach {
        runCatching {
            _stateManager::class.java.getDeclaredField(it)
        }.onSuccess { field ->
            if (!field.isAnnotationPresent(BD::class.java)) {
                return@forEach
            }

            field.isAccessible = true
            field.set(_stateManager, data[it])
        }
    }

    protected inline fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        crossinline block: suspend CoroutineScope.() -> Unit
    ): Job = ctx.lifecycleOwner.lifecycleScope.launch(context) {
        block()
    }

    suspend fun <T> loadingScope(
        block: suspend IReduxLoadingDialog<*>.() -> T
    ): T = ctx.loadingScope(block)

    data class EffectContext(
        val activity: Activity,
        val lifecycleOwner: LifecycleOwner,
        val fragmentManager: FragmentManager
    )
}

suspend fun <T> ReduxEffect.EffectContext.loadingScope(
    block: suspend IReduxLoadingDialog<*>.() -> T
): T = Redux.loadingDialog.let {
    it.dialog?.show(fragmentManager, this::class.java.simpleName)
    it.setLoadingContent(Redux.loadingDialog.defaultContent)
    val res = block(Redux.loadingDialog)
    it.dialog?.dismiss()
    res
}