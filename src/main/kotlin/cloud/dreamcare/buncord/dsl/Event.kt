package cloud.dreamcare.buncord.dsl

import cloud.dreamcare.buncord.kord
import dev.kord.core.event.Event
import dev.kord.core.on
import kotlinx.coroutines.Job

inline fun <reified T: Event> event(noinline consumer: suspend T.() -> Unit): Job = kord.on<T>(kord, consumer)
