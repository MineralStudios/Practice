package gg.mineral.practice.commands.context

import dev.rollczi.litecommands.context.ContextProvider
import dev.rollczi.litecommands.context.ContextResult
import dev.rollczi.litecommands.invocation.Invocation
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class ProfileContext : ContextProvider<CommandSender, Profile> {

    override fun provide(invocation: Invocation<CommandSender>): ContextResult<Profile> {
        if (invocation.sender() !is Player)
            return ContextResult.error("&cOnly players can use this command!")

        return ContextResult.ok {
            ProfileManager.getOrCreateProfile(invocation.sender() as Player)
        }
    }
}