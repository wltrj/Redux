package ceneax.app.redux.dialog

import android.os.Bundle
import android.view.View
import ceneax.app.lib.redux.ReduxDialog
import ceneax.app.redux.databinding.DialogLoadingBinding

class SuspendDialog : ReduxDialog<DialogLoadingBinding, String>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vb.btClose.setOnClickListener {
            dismiss(Result(Type.OK))
        }
    }
}