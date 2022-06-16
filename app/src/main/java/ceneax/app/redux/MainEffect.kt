package ceneax.app.redux

import ceneax.app.lib.redux.ReduxEffect
import kotlin.concurrent.timer

class MainEffect : ReduxEffect<MainReducer>() {
    fun increaseCounter() {
        val newCounter = stateManager.state.counter
        stateManager.updateCounter(newCounter + 1)
    }

    fun startTimer() {
        timer(period = 1000) {
            stateManager.updateTime(System.currentTimeMillis())
        }
    }
}