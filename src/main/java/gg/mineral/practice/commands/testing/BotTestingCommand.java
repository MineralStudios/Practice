package gg.mineral.practice.commands.testing;

import java.util.List;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.ProfileManager;

import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.match.BotTeamMatch;

import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.Queuetype;

public class BotTestingCommand extends PlayerCommand {

    public BotTestingCommand() {
        super("bottesting", "practice.config");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        Profile profile = ProfileManager.getOrCreateProfile(pl);

        if (args.length < 2)
            return;

        int amount = Integer.valueOf(args[0]);

        Queuetype queuetype = QueuetypeManager.getQueuetypeByName("Unranked");
        Gametype gametype = GametypeManager.getGametypeByName(args[1]);

        QueueSettings queueSettings = profile.getQueueSettings();

        Difficulty difficulty = queueSettings.getBotDifficulty();

        List<BotConfiguration> friendlyTeam = new GlueList<>();
        friendlyTeam.add(difficulty.getConfiguration(queueSettings));
        friendlyTeam.add(difficulty.getConfiguration(queueSettings));

        List<BotConfiguration> opponentTeam = new GlueList<>();
        opponentTeam.add(difficulty.getConfiguration(queueSettings));
        opponentTeam.add(difficulty.getConfiguration(queueSettings));

        for (int i = 0; i < amount; i++)
            new BotTeamMatch(new GlueList<>(), new GlueList<>(),
                    friendlyTeam,
                    opponentTeam,
                    new MatchData(queuetype, gametype, profile.getQueueSettings())).start();

    }
}
