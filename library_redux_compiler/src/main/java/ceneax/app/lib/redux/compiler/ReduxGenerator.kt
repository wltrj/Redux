package ceneax.app.lib.redux.compiler

import ceneax.app.lib.redux.annotation.ReduxModule
import com.squareup.kotlinpoet.*

@OptIn(ExperimentalStdlibApi::class)
internal class ReduxGenerator(
    private val packageName: String,
    private val className: String,
    private val pageRouteMap: Map<String, String>
) {
    private val mModuleClass = ReduxModule::class.asClassName()
    private val mMapClass = typeNameOf<HashMap<String, Class<*>>>()

    fun generate(): FileSpec {
        val companion = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("doNothing")
                    .returns(mModuleClass)
                    .addStatement("return ${className}()")
                    .build()
            )
            .build()

        val classBuilder = TypeSpec.classBuilder(className)
            .addSuperinterface(mModuleClass)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addProperty(
                PropertySpec.builder("pageRouteMap", mMapClass)
                    .initializer("hashMapOf<String,  Class<*>>()")
                    .build()
            )
            .addInitializerBlock(generatePageRouteMapBlock())
            .addFunction(
                FunSpec.builder("getPageRoute")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return pageRouteMap")
                    .returns(mMapClass)
                    .build()
            )
            .addType(companion)

        return FileSpec.builder(packageName, className)
            .addType(classBuilder.build())
            .build()
    }

    private fun generatePageRouteMapBlock(): CodeBlock {
        val builder = CodeBlock.Builder()
        pageRouteMap.forEach { (k, v) ->
            builder.addStatement("""pageRouteMap["$k"] = ${v}::class.java""")
        }
        return builder.build()
    }
}