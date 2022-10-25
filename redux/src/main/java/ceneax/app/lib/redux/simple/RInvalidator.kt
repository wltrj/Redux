package ceneax.app.lib.redux.simple

import android.util.ArrayMap
import kotlin.reflect.KProperty0

class Re internal constructor() {
    @PublishedApi
    internal val mRebuildHolder = ArrayMap<String, KProperty0<*>>()

    inline operator fun <T> KProperty0<T>.invoke(block: (T) -> Unit) {
        val holder = mRebuildHolder[name]
        if (holder == null) {
            mRebuildHolder[name] = this.also { block(get()) }
            return
        }

        val newValue = get()
        if (newValue == holder.get()) {
            return
        }

        mRebuildHolder[name] = this.also { block(newValue) }
    }
}

inline fun <V : RView<out RControl<S>>, S : RState> V.builder(builder: S.() -> Unit) {
    control.state.builder()
}