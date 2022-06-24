package ceneax.app.lib.redux

import androidx.fragment.app.DialogFragment
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
    block: suspend ReduxLoadingDialogContext.() -> T
): T = loadingDialogContext.let {
    it.dialogInstance.show(fragmentManager, this::class.java.simpleName)
    it.setLoadingContent(it.reduxLoadingDialog.defaultContent)
    val result = block(it)
    it.dialogInstance.dismiss()
    result
}