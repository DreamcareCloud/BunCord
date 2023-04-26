package cloud.dreamcare.buncord.internal.util

import cloud.dreamcare.buncord.injectionService
import cloud.dreamcare.buncord.dsl.Command
import cloud.dreamcare.buncord.dsl.Listener
import dev.kord.core.Kord
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.KClass

internal interface BuilderRegister {
    fun register(kord: Kord)
}

internal class ReflectionUtils(path: String) {
    private val reflections = Reflections(path, Scanners.SubTypes, Scanners.TypesAnnotated, Scanners.MethodsReturn, Scanners.values())

    fun registerFunctions(kord: Kord) {
        register<Listener>(kord)
        register<Command>(kord)
    }

    private inline fun <reified T : BuilderRegister> register(kord: Kord) = reflections
        .get(Scanners.MethodsReturn.with(T::class.java).`as`(Method::class.java))
        .forEach {
            runCatching { injectionService.invokeMethod<T>(it).register(kord) }
        }

    inline fun <reified T : Annotation> detectClassesWith(): Set<Class<*>> = reflections.get(
        Scanners.SubTypes.of<T>(
            Scanners.TypesAnnotated.with(T::class.java)).asClass<T>())
}

internal val Class<*>.simplerName
    get() = toString().substringAfterLast('.').substringBefore('$')

@PublishedApi
internal val KClass<*>.simplerName: String
    get() = java.simplerName

internal val Method.signature
    get() = "${name}(${parameterTypes.joinToString { it.simplerName }})"

internal val Constructor<*>.signature
    get() = "${name}(${parameterTypes.joinToString { it.simplerName }})"
