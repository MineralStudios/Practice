package gg.mineral.practice.commands.settings

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.messages.impl.ChatMessages

@Command(name = "toggleplayervisibility")
class TogglePlayerVisibilityCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        profile.playersVisible = !profile.playersVisible

        profile.updateVisibility()

        profile.message(
            ChatMessages.VISIBILITY_TOGGLED.clone().replace(
                "%toggled%",
                if (profile.playersVisible) "enabled" else "disabled"
            )
        )
    }
}
