package ceneax.app.lib.redux.simple

import androidx.lifecycle.*
import ceneax.app.lib.redux.RLog

interface RView<C : RControl<out RState>> : LifecycleOwner, ViewModelStoreOwner {
    val control: C

    fun Re.build()
}

@PublishedApi
internal fun <C : RControl<out RState>, V : RView<C>> V.withRInit(control: Lazy<C>): Lazy<C> {
    lifecycleScope.launchWhenStarted {
        listOf(
            ParamRInitSlots(),
            LaunchRInitSlots()
        ).forEach {
            it.execute(this@withRInit, control.value)
        }
    }.invokeOnCompletion {
        RLog.d("${control.value::class.java.simpleName} -> onDispose()")
        control.value.onDispose()
    }

    return control
}