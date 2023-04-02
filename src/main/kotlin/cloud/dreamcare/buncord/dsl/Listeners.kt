package cloud.dreamcare.buncord.dsl

import cloud.dreamcare.buncord.internal.utils.BuilderRegister
import club.minnced.jda.reactor.ReactiveEventManager
import net.dv8tion.jda.api.events.GenericEvent

fun listeners(construct: ListenerBuilder.() -> Unit): Listeners = Listeners(construct)

data class ListenerBuilder(val manager: ReactiveEventManager) {
    inline fun <reified T : GenericEvent> on() = manager.on(T::class.java)
}

class Listeners(private val collector: ListenerBuilder.() -> Unit) : BuilderRegister {
    override fun register(manager: ReactiveEventManager) {
        collector.invoke(ListenerBuilder(manager))
    }
}
