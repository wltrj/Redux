package ceneax.app.redux.page.two

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.lifecycle.lifecycleScope
import ceneax.app.lib.redux.annotation.PageRoute
import ceneax.app.lib.redux.whenCancel
import ceneax.app.lib.redux.whenOk
import ceneax.app.redux.base.BaseActivity
import ceneax.app.redux.databinding.ActivityTwoBinding
import ceneax.app.redux.dialog.SuspendDialog
import kotlinx.coroutines.launch

@PageRoute("/redux/demo/two")
class TwoActivity : BaseActivity<ActivityTwoBinding>() {
    override fun initVariable() {
        intent?.extras?.apply {
            Toast.makeText(this@TwoActivity, getString("data", ""), Toast.LENGTH_SHORT).show()
        }
    }

    override fun initView() {
        setSupportActionBar(vb.toolbar)
    }

    override fun bindEvent() {
        vb.btDialog.setOnClickListener {
            lifecycleScope.launch {
                a()
            }
        }
    }

    override fun finish() {
        setResult(RESULT_OK, intent.putExtras(bundleOf(
            "text" to "this is result"
        )))
        super.finish()
    }

    private suspend fun a() {
        SuspendDialog().showAsSuspend(supportFragmentManager).whenCancel {
            Toast.makeText(this, "===", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "!!!!", Toast.LENGTH_SHORT).show()
    }
}