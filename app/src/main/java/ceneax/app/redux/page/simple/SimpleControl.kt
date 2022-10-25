package ceneax.app.redux.page.simple

import android.widget.Toast
import ceneax.app.lib.redux.simple.RContext
import ceneax.app.lib.redux.simple.RControl
import ceneax.app.lib.redux.simple.RState
import ceneax.app.lib.redux.simple.launch
import kotlinx.coroutines.delay

data class SimpleState(
    val content: String = ""
) : RState

class SimpleControl(private val context: RContext) : RControl<SimpleState>(context) {
    fun dialog() = launch {
        loadingScope {
            delay(1000)
            it.setLoadingContent("123")
            delay(2000)
        }

        Toast.makeText(context.activity, "finish loading", Toast.LENGTH_SHORT).show()
    }

    fun invalidate() {
        setState { copy(content = System.currentTimeMillis().toString()) }
    }
}