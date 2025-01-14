package gg.mineral.practice.commands.testing

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.api.collection.GlueList
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.QueuetypeManager.getQueuetypeByName
import gg.mineral.practice.match.BotTeamMatch
import gg.mineral.practice.match.data.MatchData
import java.util.*

@Command(name = "bottesting")
@Permission("practice.config")
class BotTestingCommand {
    @Execute
    fun execute(@Context profile: Profile, @Arg amount: Int, @Arg gametype: Gametype) {
        val queuetype = getQueuetypeByName("Unranked") ?: return

        val queueSettings = profile.queueSettings

        val difficulty = queueSettings.opponentBotDifficulty

        for (i in 0..<amount) {
            val friendlyTeam = GlueList<BotConfiguration>()
            friendlyTeam.add(difficulty.getConfiguration(queueSettings))
            friendlyTeam.add(difficulty.getConfiguration(queueSettings))

            val opponentTeam = GlueList<BotConfiguration>()
            opponentTeam.add(difficulty.getConfiguration(queueSettings))
            opponentTeam.add(difficulty.getConfiguration(queueSettings))
            BotTeamMatch(
                LinkedList(), LinkedList(),
                friendlyTeam,
                opponentTeam,
                MatchData(queuetype, gametype, profile.queueSettings)
            ).start()
        }
    }
}
