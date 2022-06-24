package ceneax.app.redux.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ceneax.app.redux.R

class LoadingDialog : DialogFragment() {
    private var mTvContent: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_loading, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTvContent = view.findViewById(R.id.tvContent)
    }

    fun setContent(content: String) {
        mTvContent?.text = content
    }
}