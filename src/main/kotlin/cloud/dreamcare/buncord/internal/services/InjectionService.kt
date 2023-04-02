package cloud.dreamcare.buncord.internal.services

import cloud.dreamcare.buncord.internal.utils.signature
import cloud.dreamcare.buncord.internal.utils.simplerName
import java.lang.reflect.Method

@PublishedApi
internal class InjectionService {
    private val elementMap = HashMap<Class<*>, Any>()

    internal inline fun <reified T: Any> invokeMethod(method: Method, listenerClass: Any): T {
        val objects = determineArguments(method.parameterTypes, method.signature)
        return method.invoke(listenerClass, *objects) as T
    }

    private fun determineArguments(parameters: Array<out Class<*>>, signature: String) = if (parameters.isEmpty()) emptyArray() else
        parameters.map { arg ->
            elementMap.entries.find { arg.isAssignableFrom(it.key) }?.value
                ?: throw IllegalStateException("Couldn't inject '${arg.simplerName}' into $signature")
        }.toTypedArray()
}
