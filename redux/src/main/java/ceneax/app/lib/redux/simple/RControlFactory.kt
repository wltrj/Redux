package ceneax.app.lib.redux.simple

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

@PublishedApi
internal interface IAtControlFactory {
    fun <C : RControl<out RState>> create(cls: Class<C>, context: RContext): C
}

@PublishedApi
internal class DefaultAtControlFactory : IAtControlFactory {
    override fun <C : RControl<out RState>> create(
        cls: Class<C>,
        context: RContext
    ): C = cls.getConstructor(cls.constructors[0].parameterTypes[0]).newInstance(context)
}

@PublishedApi
internal inline fun <reified C : RControl<out RState>> createAtControl(
    context: RContext,
    factory: IAtControlFactory = DefaultAtControlFactory()
): C = factory.create(C::class.java, context)

inline fun <V, reified C : RControl<out RState>> V.reduxControl(): Lazy<C>
where V : FragmentActivity, V : RView<C> = withRInit(lazy(LazyThreadSafetyMode.NONE) {
    createAtControl(
        RContext.Activity(
        activity = this,
        fragmentManager = supportFragmentManager
    ))
})

inline fun <V, reified C : RControl<out RState>> V.reduxControl(): Lazy<C>
where V : Fragment, V : RView<C> = withRInit(lazy(LazyThreadSafetyMode.NONE) {
    createAtControl(
        RContext.Fragment(
        activity = requireActivity(),
        fragmentManager = parentFragmentManager,
        fragment = this
    ))
})