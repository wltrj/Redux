package ceneax.app.redux

import ceneax.app.redux.base.BaseActivity
import ceneax.app.redux.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initView() {
        setSupportActionBar(vb.toolbar)
    }
}