package ceneax.app.redux.page.two

import ceneax.app.redux.base.BaseActivity
import ceneax.app.redux.databinding.ActivityTwoBinding

class TwoActivity : BaseActivity<ActivityTwoBinding>() {
    override fun initView() {
        setSupportActionBar(vb.toolbar)
    }
}