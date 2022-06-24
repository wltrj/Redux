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

    fun <A : Activity> build(targetActivity: KClass<A>): RouterParams<A> {
        return RouterParams.Builder(
            targetActivity = targetActivity
        ).build()
    }

    fun <A : Activity> build(activity: ComponentActivity, targetActivity: KClass<A>): RouterParams<A> {
        return RouterParams.Builder(
            activity = activity,
            targetActivity = targetActivity
        ).build()
    }

//    fun build(targetPath: String): RouterParams<*> {
//        return RouterParams.Builder(
//        ).build()
//    }
}

class RouterParams<A : Activity> private constructor(private val builder: Builder<A>) {
    data class Builder<A : Activity>(
        val activity: ComponentActivity? = null,
        val targetActivity: KClass<A>? = null
    ) {
        var bundle: Bundle? = null
        var onResult: ((ActivityResult) -> Unit)? = null

        fun build() = RouterParams(this)
    }

    fun with(bundle: Bundle): RouterParams<A> {
        builder.bundle = bundle
        return this
    }

    fun onResult(onResult: (ActivityResult) -> Unit): RouterParams<A> {
        builder.onResult = onResult
        return this
    }

    fun navigation() = RouterExecutor(builder).execute()
}

class RouterExecutor<A : Activity>(private val params: RouterParams.Builder<A>) {
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
            Intent(Redux.application, params.targetActivity!!.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
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