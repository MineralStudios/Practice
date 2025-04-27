package gg.mineral.practice.commands.spectator

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile

@Command(name = "stopspectating", aliases = ["stopspec"])
class StopSpectatingCommand {
    @Execute
    fun execute(@Context profile: Profile) = profile.stopSpectating()
}
