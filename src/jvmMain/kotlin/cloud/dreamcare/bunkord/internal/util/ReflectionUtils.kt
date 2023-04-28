package cloud.dreamcare.bunkord.internal.util

import cloud.dreamcare.bunkord.config.Configuration
import cloud.dreamcare.bunkord.injectionService
import cloud.dreamcare.bunkord.dsl.Command
import cloud.dreamcare.bunkord.dsl.Listener
import dev.kord.core.Kord
import mu.KLogger
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.KClass

internal interface BuilderRegister {
    fun register(kord: Kord, configuration: Configuration)
}

internal class ReflectionUtils(path: String) {
    private val reflections = Reflections(path, Scanners.SubTypes, Scanners.TypesAnnotated, Scanners.MethodsReturn, Scanners.values())
    private val logger: KLogger = KotlinLogging.logger { }

    fun registerFunctions(kord: Kord, configuration: Configuration) {
        register<Listener>(kord, configuration)
        register<Command>(kord, configuration)
    }

    private inline fun <reified T : BuilderRegister> register(kord: Kord, configuration: Configuration) = reflections
        .get(Scanners.MethodsReturn.with(T::class.java).`as`(Method::class.java))
        .filter { !it.declaringClass.packageName.contains("dsl") }
        .also { logger.info { "Registering ${it.size} ${T::class.simplerName}s" } }
        .forEach {
            logger.debug { "register<${T::class.simplerName}>(${it.declaringClass.name}::${it.name})" }
            injectionService.invokeMethod<T>(it).register(kord, configuration)
        }

    inline fun <reified T : Annotation> detectClassesWith(): Set<Class<*>> = reflections.get(
        Scanners.SubTypes.of<T>(
            Scanners.TypesAnnotated.with(T::class.java)).asClass<T>())
}

internal val Class<*>.simplerName
    get() = toString().substringAfterLast('.').substringBefore('$')

internal val KClass<*>.simplerName: String
    get() = java.simplerName

internal val Method.signature
    get() = "${name}(${parameterTypes.joinToString { it.simplerName }})"

internal val Constructor<*>.signature
    get() = "${name}(${parameterTypes.joinToString { it.simplerName }})"
