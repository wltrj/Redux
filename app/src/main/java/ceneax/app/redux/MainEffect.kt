package ceneax.app.redux

import android.widget.Toast
import ceneax.app.lib.redux.EmptyReduxSlot
import ceneax.app.lib.redux.IReduxSlot
import ceneax.app.lib.redux.ReduxEffect
import ceneax.app.redux.repository.MainRepo
import kotlin.concurrent.timer

class MainSlot : IReduxSlot {
    val repo = MainRepo()
}

class MainEffect : ReduxEffect<MainReducer, MainSlot>() {
    fun increaseCounter() {
        val newCounter = stateManager.state.counter
        stateManager.updateCounter(newCounter + 1)

        Toast.makeText(ctx.activity, slot.repo.getData(), Toast.LENGTH_SHORT).show()
    }

    fun startTimer() {
        timer(period = 1000) {
            stateManager.updateTime(System.currentTimeMillis())
        }
    }
}