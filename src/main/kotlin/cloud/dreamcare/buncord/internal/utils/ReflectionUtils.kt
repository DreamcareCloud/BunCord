package cloud.dreamcare.buncord.internal.utils

import cloud.dreamcare.buncord.diService
import cloud.dreamcare.buncord.dsl.Commands
import cloud.dreamcare.buncord.dsl.Listeners
import dev.kord.core.Kord
import org.reflections.Reflections
import org.reflections.scanners.Scanners.*
import java.lang.reflect.Constructor
import java.lang.reflect.Method

internal interface BuilderRegister {
    fun register(kord: Kord)
}

public class ReflectionUtils {
    public fun registerFunctions(path: String, kord: Kord) {
        register<Listeners>(path, kord)
        register<Commands>(path, kord)
    }

    private inline fun <reified T : BuilderRegister> register(path: String, kord: Kord) {
        Reflections(path, SubTypes, TypesAnnotated, MethodsReturn)
            .get(MethodsReturn.with(T::class.java).`as`(Method::class.java))
            .forEach {
                diService.invokeMethod<T>(it.declaringClass, it).register(kord)
            }
    }
}

internal val Class<*>.simplerName
    get() = toString().substringAfterLast('.').substringBefore('$')

internal val Method.signature
    get() = "${name}(${parameterTypes.joinToString { it.simplerName }})"

internal val Constructor<*>.signature
    get() = "${name}(${parameterTypes.joinToString { it.simplerName }})"
