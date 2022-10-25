package ceneax.app.lib.redux.simple

/**
 * 初始化 [Activity] => [android.app.Activity.onCreate] -> [onInit]
 *
 * [Fragment] => [androidx.fragment.app.Fragment.onViewCreated] -> [onInit]
 *
 * 销毁 => [android.app.Activity.onDestroy] -> [onDispose]
 */
interface RLifecycle {
    fun onInit() {}

    fun onDispose() {}
}