package ceneax.app.lib.redux.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class PageRoute(
    val path: String
)