package ceneax.app.lib.redux

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class ReduxDialog : DialogFragment() {
    private val mSubscribe = Subscribe<Int>()
    private var mType = Type.CANCEL

    @ReduxInternalApi
    final override fun dismiss() {
        super.dismiss()
        mSubscribe.post(mType)
    }

    @ReduxInternalApi
    final override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }

    @OptIn(ReduxInternalApi::class)
    protected open fun dismiss(type: Int) {
        mType = type
        dismiss()
    }

    @OptIn(ReduxInternalApi::class)
    suspend fun showAsSuspend(manager: FragmentManager, tag: String? = null) = suspendCoroutine<Int> {
        mSubscribe.onSubscribe { value ->
            it.resume(value)
        }
        show(manager, tag)
    }

    object Type {
        const val CANCEL = -1
        const val OK = 0
        const val NEUTRAL = 1
    }
}

inline fun Int.whenOk(block: () -> Unit): Int {
    if (this == ReduxDialog.Type.OK) {
        block()
    }
    return this
}

inline fun Int.whenCancel(block: () -> Unit): Int {
    if (this == ReduxDialog.Type.CANCEL) {
        block()
    }
    return this
}

inline fun Int.whenNeutral(block: () -> Unit): Int {
    if (this == ReduxDialog.Type.NEUTRAL) {
        block()
    }
    return this
}

internal class Subscribe<T> {
    private var mCallback: (T) -> Unit = {}

    fun post(value: T) {
        mCallback(value)
    }

    fun onSubscribe(block: (T) -> Unit) {
        mCallback = block
    }
}