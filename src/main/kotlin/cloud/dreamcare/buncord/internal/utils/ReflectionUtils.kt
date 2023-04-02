package cloud.dreamcare.buncord.internal.utils

import club.minnced.jda.reactor.ReactiveEventManager
import java.lang.reflect.Method

internal interface BuilderRegister {
    fun register(manager: ReactiveEventManager)
}

internal val Class<*>.simplerName
    get() = toString().substringAfterLast('.').substringBefore('$')

internal val Method.signature
    get() = "${name}(${parameterTypes.joinToString { it.simplerName }})"
