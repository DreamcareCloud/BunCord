package cloud.dreamcare.buncord.dsl

import cloud.dreamcare.buncord.internal.utils.BuilderRegister
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.on

public fun listeners(construct: ListenerBuilder.() -> Unit): Listeners = Listeners(construct)

public data class ListenerBuilder(val kord: Kord) {
    public inline fun <reified T : Event> on(crossinline listener: suspend T.() -> Unit) {
        kord.on<T> {
            listener(this)
        }
    }
}

public class Listeners(private val collector: ListenerBuilder.() -> Unit) : BuilderRegister {
    /** @suppress */
    override fun register(kord: Kord) {
        collector.invoke(ListenerBuilder(kord))
    }
}
