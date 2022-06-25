package ceneax.app.redux

import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ceneax.app.lib.redux.*
import ceneax.app.redux.page.two.TwoActivity
import ceneax.app.redux.repository.MainRepo
import kotlinx.coroutines.delay
import kotlin.concurrent.timer

class MainSlot : IReduxSlot {
    val repo = MainRepo()
}

class MainEffect : ReduxEffect<MainReducer, MainSlot>() {
    fun increaseCounter() {
        val newCounter = stateManager.state.counter
        stateManager.updateCounter(newCounter + 1)

        Toast.makeText(ctx.activity, slot.repo.getData(), Toast.LENGTH_SHORT).show()

        launch {
            loadingScope {
                delay(1000)
                setLoadingContent("123")
                delay(2000)
            }

            Toast.makeText(ctx.activity, "finish loading", Toast.LENGTH_SHORT).show()
        }
    }

    fun startTimer() {
        timer(period = 1000) {
            stateManager.updateTime(System.currentTimeMillis())
        }
    }

    fun openTwoActivity() {
        ReduxRouter.instance.build(TwoActivity::class.java).navigation()
    }

    fun openTwoActivityWithResult() {
        ReduxRouter.instance.build(ctx.activity, TwoActivity::class.java)
            .onResult {
                it.data?.extras?.apply {
                    Toast.makeText(ctx.activity, getString("text", ""), Toast.LENGTH_SHORT).show()
                }
            }
            .navigation()
    }

    fun showLoading() = launch {
        loadingScope {
            setLoadingContent("")
        }
    }
}