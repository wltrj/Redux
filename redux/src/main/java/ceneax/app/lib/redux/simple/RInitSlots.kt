package ceneax.app.lib.redux.simple

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ceneax.app.lib.redux.Param
import ceneax.app.lib.redux.RLog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

internal interface RInitSlots {
    suspend fun <V : RView<C>, C : RControl<out RState>> execute(view: V, control: C)
}

internal class ParamRInitSlots : RInitSlots {
    override suspend fun <V : RView<C>, C : RControl<out RState>> execute(view: V, control: C) {
        val bundle = when (view) {
            is FragmentActivity -> view.intent?.extras
            is Fragment -> view.arguments
            else -> null
        }

        bundle?.keySet()?.forEach {
            val filed = control.state::class.java.getDeclaredField(it)
            if (!filed.isAnnotationPresent(Param::class.java)) {
                return@forEach
            }

            filed.isAccessible = true
            filed.set(control.state, bundle[it])
        }
    }
}

internal class LaunchRInitSlots : RInitSlots {
    private val mReInvalidator = Re()

    override suspend fun <V : RView<C>, C : RControl<out RState>> execute(view: V, control: C) {
        RLog.d("${control::class.java.simpleName} -> onInit()")
        control.onInit()

        control.stateStore.stateFlow.onEach {
            with(view) {
                mReInvalidator.build()
            }
        }.collect()
    }
}