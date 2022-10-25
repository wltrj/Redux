package ceneax.app.lib.redux

import android.util.Log

@PublishedApi
internal object RLog {
    private const val TAG = "Log_Redux"

    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg)
        }
    }

    fun i(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg)
        }
    }

    fun w(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, msg)
        }
    }

    fun e(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg)
        }
    }
}