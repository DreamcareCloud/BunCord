package cloud.dreamcare.bunkord.internal.listeners

import cloud.dreamcare.bunkord.dsl.Listener
import cloud.dreamcare.bunkord.dsl.listener
import dev.kord.core.behavior.requestMembers
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.gateway.PrivilegedIntent

public class ConfigurationPopulationListener {
    public fun populateConfiguration(): Listener = listener {
        on<GuildCreateEvent> {
            configuration.guild(guild.id).apply {
                name = guild.name

                guild.roles.collect { role ->
                    getRole(role.id).apply {
                        name = role.name
                        position = role.getPosition()
                    }
                }

                @OptIn(PrivilegedIntent::class)
                guild.requestMembers().collect {
                    it.members.forEach { member ->
                        member(member.id).apply {
                            displayName = member.displayName
                            active = true
                            joinedAt = member.joinedAt

                            member.roles.collect { role ->
                                getRole(role.id).apply {
                                    name = role.name
                                    position = role.getPosition()
                                }
                            }

                            getUser().apply {
                                username = member.username
                                discriminator = member.discriminator
                            }
                        }
                    }
                }
            }.also {
                configuration.save()
            }
        }
    }
}
