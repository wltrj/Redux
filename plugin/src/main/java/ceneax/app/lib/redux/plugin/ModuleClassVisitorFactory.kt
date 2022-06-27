package ceneax.app.lib.redux.plugin

import com.android.build.api.instrumentation.*
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.concurrent.ConcurrentHashMap

object ModuleHolder {
    private val mModuleMap = ConcurrentHashMap<String, String>()

    fun addModule(name: String) {
        mModuleMap[name] = name
    }

    fun forEach(block: (String) -> Unit) {
        mModuleMap.values.forEach {
            block(it)
        }
    }

    fun clearModule() {
        mModuleMap.clear()
    }
}

abstract class ModuleClassVisitorFactory : AsmClassVisitorFactory<ModuleClassVisitorFactory.ModuleInstrumentation> {
    interface ModuleInstrumentation : InstrumentationParameters {
        @get:Input
        @get:Optional
        val invalidate: Property<Long>
    }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ModuleClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        if (classData.interfaces.contains("ceneax.app.lib.redux.annotation.ReduxModule")) {
            ModuleHolder.addModule(classData.className)
        }

        return classData.superClasses.contains("android.app.Application")
    }
}

class ModuleClassVisitor(nextClassVisitor: ClassVisitor) : ClassVisitor(Opcodes.ASM7, nextClassVisitor) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "onCreate") {
            mv = ModuleMethodVisitor(mv)
        }
        return mv
    }
}

class ModuleMethodVisitor(methodVisitor: MethodVisitor) : MethodVisitor(Opcodes.ASM7, methodVisitor) {
    override fun visitCode() {
        super.visitCode()

        ModuleHolder.forEach {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "ceneax/app/lib/redux/ReduxRouterCore", "INSTANCE", "Lceneax/app/lib/redux/ReduxRouterCore;")
            mv.visitLdcInsn(it)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "ceneax/app/lib/redux/ReduxRouterCore", "addModuleName", "(Ljava/lang/String;)V", false)
        }
    }
}