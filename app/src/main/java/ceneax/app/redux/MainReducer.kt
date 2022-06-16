package ceneax.app.redux

import ceneax.app.lib.redux.IReduxState
import ceneax.app.lib.redux.ReduxReducer

data class MainState(
    val counter: Int = 0,
    val time: Long = 0
) : IReduxState

class MainReducer : ReduxReducer<MainState>() {
    fun updateCounter(newCounter: Int) = setState {
        copy(counter = newCounter)
    }

    fun updateTime(newTime: Long) = setState {
        copy(time = newTime)
    }
}