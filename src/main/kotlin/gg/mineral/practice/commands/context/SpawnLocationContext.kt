package gg.mineral.practice.commands.context

import dev.rollczi.litecommands.context.ContextProvider
import dev.rollczi.litecommands.context.ContextResult
import dev.rollczi.litecommands.invocation.Invocation
import gg.mineral.practice.util.world.SpawnLocation
import gg.mineral.practice.util.world.appender.toSpawnLocation
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnLocationContext : ContextProvider<CommandSender, SpawnLocation> {
    override fun provide(invocation: Invocation<CommandSender>): ContextResult<SpawnLocation> {
        val sender = invocation.sender()
        if (sender !is Player)
            return ContextResult.error("&cOnly players can use this command!")

        return ContextResult.ok {
            sender.location.toSpawnLocation()
        }
    }
}
