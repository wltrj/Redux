package ceneax.app.lib.redux

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import ceneax.app.lib.redux.simple.RContext
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

interface IReduxLoadingDialog<D : DialogFragment> {
    val dialog: KClass<D>

    val defaultContent: String

    fun D.setLoadingContent(content: String)
}

internal class ReduxLoadingDialog : IReduxLoadingDialog<DialogFragment> {
    override val dialog: KClass<DialogFragment> = DialogFragment::class

    override val defaultContent: String = ""

    override fun DialogFragment.setLoadingContent(content: String) {}
}

class ReduxLoadingDialogContext(
    internal val dialogInstance: DialogFragment,
    internal val reduxLoadingDialog: IReduxLoadingDialog<DialogFragment>
) {
    fun setLoadingContent(content: String) = with(reduxLoadingDialog) {
        dialogInstance.setLoadingContent(content)
    }
}

suspend fun <T> EffectContext.loadingScope(
    block: suspend CoroutineScope.(ReduxLoadingDialogContext) -> T
): T = loadingDialogContext.let {
    if (fragmentManager.findFragmentByTag(this::class.java.simpleName) != null) {
        return@let lifecycleOwner.lifecycleScope.block(it)
    }

    fragmentManager.beginTransaction().remove(it.dialogInstance).commit()
    it.dialogInstance.show(fragmentManager, this::class.java.simpleName)
    it.setLoadingContent(it.reduxLoadingDialog.defaultContent)
    val result = lifecycleOwner.lifecycleScope.block(it)
    it.dialogInstance.dismiss()
    result
}

suspend fun <T> RContext.loadingScope(
    block: suspend CoroutineScope.(ReduxLoadingDialogContext) -> T
): T = loadingDialogContext.let {
    if (fragmentManager.findFragmentByTag(this::class.java.simpleName) != null) {
        return@let coroutineScope.block(it)
    }

    fragmentManager.beginTransaction().remove(it.dialogInstance).commit()
    it.dialogInstance.show(fragmentManager, this::class.java.simpleName)
    it.setLoadingContent(it.reduxLoadingDialog.defaultContent)
    val result = coroutineScope.block(it)
    it.dialogInstance.dismiss()
    result
}