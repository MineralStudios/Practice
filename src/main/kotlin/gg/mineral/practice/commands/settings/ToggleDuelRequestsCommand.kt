package gg.mineral.practice.commands.settings

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.messages.impl.ChatMessages

@Command(name = "toggleduelrequests")
class ToggleDuelRequestsCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        profile.duelRequests = !profile.duelRequests
        profile.message(
            ChatMessages.DUEL_REQUESTS_TOGGLED.clone()
                .replace("%toggled%", if (profile.duelRequests) "enabled" else "disabled")
        )
    }
}
