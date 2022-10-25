package ceneax.app.lib.redux.simple

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import ceneax.app.lib.redux.Redux
import ceneax.app.lib.redux.ReduxLoadingDialogContext
import ceneax.app.lib.redux.newInstance
import kotlinx.coroutines.CoroutineScope

sealed interface RContext {
    val activity: FragmentActivity
    val fragmentManager: FragmentManager
    val coroutineScope: CoroutineScope

    val loadingDialogContext: ReduxLoadingDialogContext

    data class Activity(
        override val activity: FragmentActivity,
        override val fragmentManager: FragmentManager
    ) : RContext {
        override val coroutineScope get() = activity.lifecycleScope

        override val loadingDialogContext by lazy(LazyThreadSafetyMode.NONE) {
            ReduxLoadingDialogContext(
                dialogInstance = Redux.loadingDialog.dialog.newInstance(),
                reduxLoadingDialog = Redux.loadingDialog
            )
        }
    }

    data class Fragment(
        override val activity: FragmentActivity,
        override val fragmentManager: FragmentManager,
        val fragment: androidx.fragment.app.Fragment
    ) : RContext {
        override val coroutineScope get() = fragment.lifecycleScope

        override val loadingDialogContext by lazy(LazyThreadSafetyMode.NONE) {
            ReduxLoadingDialogContext(
                dialogInstance = Redux.loadingDialog.dialog.newInstance(),
                reduxLoadingDialog = Redux.loadingDialog
            )
        }
    }
}