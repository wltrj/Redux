package ceneax.app.lib.redux

import android.os.Bundle
import androidx.core.os.bundleOf
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal fun <R> Class<R>.newInstance(vararg args: Any): R {
    return getDeclaredConstructor(*args.map { it::class.java }.toTypedArray()).newInstance(*args)
}

@Suppress("UNCHECKED_CAST")
internal fun <R : Any, T> KClass<R>.getGenericsClass(index: Int): Class<T> {
    val type = java.genericSuperclass as ParameterizedType
    return type.actualTypeArguments[index] as Class<T>
}

internal fun <R : Any> KClass<R>.newInstance(vararg args: Any): R {
    return java.newInstance(*args)
}

internal fun <R : Any, T> KClass<R>.newGenericsInstance(index: Int, vararg args: Any): T {
    return getGenericsClass<R, T>(index).newInstance(*args)
}

internal fun Type.getActualTypeClass(index: Int): Class<*> {
    return (this as ParameterizedType).actualTypeArguments[index] as Class<*>
}

internal fun Class<*>.setDeclaredField(instance: Any, name: String, value: Any) {
    getDeclaredField(name).let {
        it.isAccessible = true
        it.set(instance, value)
    }
}

internal fun <R : Any> R.setDeclaredField(name: String, value: Any): R {
    this::class.java.setDeclaredField(this, name, value)
    return this
}

@Suppress("UNCHECKED_CAST")
internal tailrec fun Class<*>.getSuperEffectClass(): Class<ReduxEffect<*>> {
    if (isAssignableFrom(ReduxEffect::class.java)) {
        return this as Class<ReduxEffect<*>>
    }
    if (isAssignableFrom(Any::class.java)) {
        error("目标类必须继承 ReduxEffect 类")
    }
    return superclass.getSuperEffectClass()
}

fun bundleOf(vararg pairs: Pair<KProperty1<*, *>, Any?>): Bundle = bundleOf(
    *Array(pairs.size) {
        Pair(pairs[it].first.name, pairs[it].second)
    }
)