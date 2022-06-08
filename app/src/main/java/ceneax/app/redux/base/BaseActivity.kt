package ceneax.app.redux.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    private var _viewBinding: VB? = null
    protected val vb get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // 使用反射得到ViewBinding的class
        val aClass = (this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        _viewBinding = method.invoke(null, layoutInflater) as VB

        setContentView(vb.root)

        initVariable()
        initView()
        bindEvent()
        initObserver()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }

    open fun initVariable() {}
    open fun initView() {}
    open fun bindEvent() {}
    open fun initObserver() {}
    open fun initData() {}
}