package gg.mineral.practice.commands.settings

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.messages.impl.ChatMessages

@Command(name = "togglescoreboard")
class ToggleScoreboardCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        profile.scoreboardEnabled = !profile.scoreboardEnabled
        profile.message(
            ChatMessages.SCOREBOARD_TOGGLED.clone()
                .replace("%toggled%", if (profile.scoreboardEnabled) "enabled" else "disabled")
        )
    }
}
