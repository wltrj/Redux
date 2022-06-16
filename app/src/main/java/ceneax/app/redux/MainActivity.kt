package ceneax.app.redux

import ceneax.app.lib.redux.IReduxView
import ceneax.app.lib.redux.ReduxView
import ceneax.app.lib.redux.observe
import ceneax.app.redux.base.BaseActivity
import ceneax.app.redux.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(), IReduxView<MainState, MainEffect> by ReduxView() {
    override fun initView() {
        setSupportActionBar(vb.toolbar)
    }

    override fun bindEvent() {
        vb.fab.setOnClickListener {
            effect.increaseCounter()
        }
    }

    override fun initData() {
        effect.startTimer()
    }

    override fun initObserver() {
        observe(MainState::time) {
            vb.tvTime.text = time.toString()
        }
    }

    override fun invalidate(state: MainState) {
        vb.tvCounter.text = state.counter.toString()
    }
}