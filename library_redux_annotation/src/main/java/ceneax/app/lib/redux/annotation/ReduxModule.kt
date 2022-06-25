package ceneax.app.lib.redux.annotation

interface ReduxModule {
    fun getPageRoute(): Map<String, Class<*>>
}