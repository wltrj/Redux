package ceneax.app.lib.redux

@Target(AnnotationTarget.FIELD)
annotation class BD

@RequiresOptIn("Redux 内部专属API，外部禁止调用", RequiresOptIn.Level.ERROR)
internal annotation class ReduxInternalApi

@Target(AnnotationTarget.FIELD)
annotation class Param