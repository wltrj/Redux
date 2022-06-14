package ceneax.app.lib.redux

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.reflect.KClass

class ReduxRouter private constructor() {
    companion object {
        val instance by lazy { ReduxRouter() }
    }

    fun build(targetActivity: KClass<Activity>): RouterParams {
        return RouterParams.Builder(
            targetActivity = targetActivity
        ).build()
    }

    fun build(activity: ComponentActivity, targetActivity: KClass<Activity>): RouterParams {
        return RouterParams.Builder(
            activity = activity,
            targetActivity = targetActivity
        ).build()
    }

    fun build(targetPath: String): RouterParams {
        return RouterParams.Builder(
        ).build()
    }
}

class RouterParams private constructor(private val builder: Builder) {
    data class Builder(
        val activity: ComponentActivity? = null,
        val targetActivity: KClass<Activity>? = null
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

    fun navigation() {
        RouterExecutor(builder).execute()
    }
}

class RouterExecutor(private val params: RouterParams.Builder) {
    fun execute() {
        if (params.targetActivity == null) {
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
        Redux.application.startActivity(
            Intent(Redux.application, params.targetActivity!!.java),
            params.bundle
        )
    }

    private fun navigationWithResult() {
        params.activity!!.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            params.onResult!!
        ).launch(Intent(params.activity, params.targetActivity!!.java).also {
            if (params.bundle != null) {
                it.putExtras(params.bundle!!)
            }
        })
    }
}