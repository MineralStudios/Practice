package gg.mineral.practice.commands.testing;

import java.util.LinkedList;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.match.BotTeamMatch;
import gg.mineral.practice.match.data.MatchData;
import lombok.val;

public class BotTestingCommand extends PlayerCommand {

    public BotTestingCommand() {
        super("bottesting", "practice.config");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        val profile = ProfileManager.getOrCreateProfile(pl);

        if (args.length < 2)
            return;

        val amount = Integer.valueOf(args[0]);

        val queuetype = QueuetypeManager.getQueuetypeByName("Unranked");
        val gametype = GametypeManager.getGametypeByName(args[1]);

        val queueSettings = profile.getQueueSettings();

        val difficulty = queueSettings.getOpponentBotDifficulty();

        for (int i = 0; i < amount; i++) {
            val friendlyTeam = new GlueList<BotConfiguration>();
            friendlyTeam.add(difficulty.getConfiguration(queueSettings));
            friendlyTeam.add(difficulty.getConfiguration(queueSettings));

            val opponentTeam = new GlueList<BotConfiguration>();
            opponentTeam.add(difficulty.getConfiguration(queueSettings));
            opponentTeam.add(difficulty.getConfiguration(queueSettings));
            new BotTeamMatch(new LinkedList<>(), new LinkedList<>(),
                    friendlyTeam,
                    opponentTeam,
                    new MatchData(queuetype, gametype, profile.getQueueSettings())).start();
        }

    }
}
