package ceneax.app.lib.redux

import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import ceneax.app.lib.redux.annotation.ReduxModule
import kotlin.random.Random
import kotlin.reflect.KClass

class ReduxRouterModuleController {
    private val mModules = mutableListOf<ReduxModule>()

    fun addModule(vararg module: ReduxModule) {
        module.forEach {
            mModules.add(it)
        }
    }

    fun removeModule(module: ReduxModule) {
        mModules.remove(module)
    }

    fun queryPageRoute(path: String): String {
        var className = ""
        for (module in mModules) {
            val find = module.getPageRoute()[path]
            if (find != null) {
                className = find.name
                break
            }
        }
        return className
    }
}

class ReduxRouter private constructor() {
    companion object {
        val instance by lazy { ReduxRouter() }

        val moduleController by lazy { ReduxRouterModuleController() }
    }

    fun <A : Activity> build(targetActivity: Class<A>): RouterParams {
        return RouterParams.Builder(
            targetActivity = targetActivity.name
        ).build()
    }

    fun <A : Activity> build(activity: ComponentActivity, targetActivity: Class<A>): RouterParams {
        return RouterParams.Builder(
            activity = activity,
            targetActivity = targetActivity.name
        ).build()
    }

    fun build(targetPath: String): RouterParams {
        return RouterParams.Builder(
            targetActivity = targetPath
        ).build()
    }

    fun build(activity: ComponentActivity, targetPath: String): RouterParams {
        return RouterParams.Builder(
            activity = activity,
            targetActivity = targetPath
        ).build()
    }
}

class RouterParams private constructor(private val builder: Builder) {
    data class Builder(
        val activity: ComponentActivity? = null,
        val targetActivity: String = ""
    ) {
        var bundle: Bundle? = null
        var onResult: ((ActivityResult) -> Unit)? = null

        fun build() = RouterParams(this)
    }

    fun with(bundle: Bundle): RouterParams {
        builder.bundle = bundle
        return this
    }

    fun onResult(onResult: (ActivityResult) -> Unit): RouterParams {
        builder.onResult = onResult
        return this
    }

    fun navigation() = RouterExecutor(builder).execute()
}

class RouterExecutor(private val params: RouterParams.Builder) {
    fun execute() {
        if (params.targetActivity.isEmpty()) {
            RLog.e("跳转失败，未找到目标Activity")
            return
        }

        if (params.activity == null || params.onResult == null) {
            navigationNoResult()
            return
        }

        navigationWithResult()
    }

    private fun navigationNoResult() {
        Redux.application.startActivity(Intent().also {
            it.setClassName(Redux.application, params.targetActivity)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }, params.bundle)
    }

    private fun navigationWithResult() {
        val launcher = params.activity!!.activityResultRegistry.register(
            "activity_rq#${Random(Int.MAX_VALUE).nextInt()}",
            ActivityResultContracts.StartActivityForResult(),
            params.onResult!!
        )

        params.activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    launcher.unregister()
                }
            }
        })

        launcher.launch(Intent().also {
            it.setClassName(params.activity, params.targetActivity)
            if (params.bundle != null) {
                it.putExtras(params.bundle!!)
            }
        })
    }
}