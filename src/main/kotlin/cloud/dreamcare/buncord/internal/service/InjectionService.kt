package cloud.dreamcare.buncord.internal.service

import cloud.dreamcare.buncord.internal.utils.signature
import cloud.dreamcare.buncord.internal.utils.simplerName
import java.lang.reflect.Method

@PublishedApi
internal class InjectionService {
    private val elementMap = HashMap<Class<*>, Any>()

    internal inline fun <reified T> invokeMethod(clazz: Class<*>, method: Method): T {
        val objects = determineArguments(method.parameterTypes, method.signature)
        return method.invoke(invokeConstructor(clazz), *objects) as T
    }

    private fun determineArguments(parameters: Array<out Class<*>>, signature: String) = if (parameters.isEmpty()) emptyArray() else
        parameters.map { arg ->
            elementMap.entries.find { arg.isAssignableFrom(it.key) }?.value
                ?: throw IllegalStateException("Couldn't inject '${arg.simplerName}' into $signature")
        }.toTypedArray()

    @Suppress("UNCHECKED_CAST")
    private fun <T> invokeConstructor(clazz: Class<T>): T {
        val constructor = clazz.constructors.first()
        val objects = determineArguments(constructor.parameterTypes, constructor.signature)
        return constructor.newInstance(*objects) as T
    }
}
