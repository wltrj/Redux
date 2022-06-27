package ceneax.app.redux.page.two

import androidx.core.os.bundleOf
import ceneax.app.lib.redux.annotation.PageRoute
import ceneax.app.redux.base.BaseActivity
import ceneax.app.redux.databinding.ActivityTwoBinding

@PageRoute("/redux/demo/two")
class TwoActivity : BaseActivity<ActivityTwoBinding>() {
    override fun initView() {
        setSupportActionBar(vb.toolbar)
    }

    override fun finish() {
        setResult(RESULT_OK, intent.putExtras(bundleOf(
            "text" to "this is result"
        )))
        super.finish()
    }
}