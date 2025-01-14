package gg.mineral.practice.commands.stats

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.menus.LeaderboardMenu
import gg.mineral.practice.managers.QueuetypeManager.queuetypes

@Command(name = "leaderboards", aliases = ["leaderboard", "lb"])
class LeaderboardsCommand {
    @Execute
    fun execute(@Context profile: Profile) =
        queuetypes.values.first { it.ranked }.let { queuetype -> profile.openMenu(LeaderboardMenu(queuetype)) }
}
