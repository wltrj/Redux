package ceneax.app.lib.redux

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

internal class ReduxInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Redux.init(context as Application)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}