package ceneax.app.lib.redux.simple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

private const val ViewModelProviderDefaultKey = "ceneax.app.lib.redux.ViewModelProvider.DefaultKey"

internal interface IRStateStoreFactory : ViewModelProvider.Factory {
    val stateFlow: MutableStateFlow<out RState>
}

internal class DefaultRStateStoreFactory(
    override val stateFlow: MutableStateFlow<out RState>
) : IRStateStoreFactory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MutableStateFlow::class.java).newInstance(stateFlow)
    }
}

private fun <S : RState> getViewModelKey(stateClass: KClass<S>): String =
    "$ViewModelProviderDefaultKey:${stateClass.java.canonicalName}:${RStateStore::class.java.canonicalName}"

internal fun <S : RState> createStateStore(
    context: RContext,
    initialState: S,
    stateScope: RStateScope
): RStateStore<out RState> = ViewModelProvider(
    when (context) {
        is RContext.Activity -> context.activity.viewModelStore
        is RContext.Fragment -> if (stateScope == RStateScope.EXCLUSIVE) {
            context.fragment.viewModelStore
        } else {
            context.activity.viewModelStore
        }
    },
    DefaultRStateStoreFactory(MutableStateFlow(initialState))
)[getViewModelKey(initialState::class), RStateStore::class.java]