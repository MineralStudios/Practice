package gg.mineral.practice.commands.settings

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.messages.impl.ChatMessages

@Command(name = "togglepartyrequests")
class TogglePartyRequestsCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        profile.partyRequests = !profile.partyRequests
        profile.message(
            ChatMessages.PARTY_REQUESTS_TOGGLED.clone()
                .replace("%toggled%", if (profile.partyRequests) "enabled" else "disabled")
        )
    }
}
