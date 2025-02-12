package gg.mineral.practice.commands.stats

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.Material

@Command(name = "pots", aliases = ["potions"])
class PotsCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        if (profile.playerStatus !== PlayerStatus.FIGHTING) return profile.message(ErrorMessages.NOT_IN_MATCH)

        val pots = profile.inventory.getNumber(Material.POTION, 16421.toShort())
        profile.message(ChatMessages.POTS.clone().replace("%pots%", pots.toString()))
    }
}
