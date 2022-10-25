package ceneax.app.redux.page.simple

import ceneax.app.lib.redux.annotation.PageRoute
import ceneax.app.lib.redux.simple.RView
import ceneax.app.lib.redux.simple.Re
import ceneax.app.lib.redux.simple.builder
import ceneax.app.lib.redux.simple.reduxControl
import ceneax.app.redux.base.BaseActivity
import ceneax.app.redux.databinding.ActivitySimpleBinding

@PageRoute(path = "/app/simple")
class SimpleActivity : BaseActivity<ActivitySimpleBinding>(), RView<SimpleControl> {
    override val control by reduxControl()

    override fun bindEvent() {
        vb.btDialog.setOnClickListener {
            control.dialog()
        }

        vb.btInvalidate.setOnClickListener {
            control.invalidate()
        }
    }

    override fun Re.build() = builder {
        vb.btInvalidate.text = "invalidate $content"
    }
}