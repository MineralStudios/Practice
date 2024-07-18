package gg.mineral.practice.commands.testing;

import java.util.List;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.QueueEntryManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.QueueMatchData;
import gg.mineral.practice.queue.QueueEntry;
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

        Integer amount = Integer.valueOf(args[0]);

        Queuetype queuetype = QueuetypeManager.getQueuetypeByName("Unranked");
        Gametype gametype = GametypeManager.getGametypeByName(args[1]);
        QueueEntry queueEntry = QueueEntryManager.newEntry(queuetype, gametype);

        Difficulty difficulty = profile.getMatchData().getBotDifficulty();

        List<Difficulty> friendlyTeam = new GlueList<>();
        friendlyTeam.add(difficulty);
        friendlyTeam.add(difficulty);

        List<Difficulty> opponentTeam = new GlueList<>();
        opponentTeam.add(difficulty);
        opponentTeam.add(difficulty);

        TeamMatch m;
        for (int i = 0; i < amount; i++) {
            m = new TeamMatch(new GlueList<>(), new GlueList<>(),
                    friendlyTeam,
                    opponentTeam,
                    profile.getMatchData().cloneBotAndArenaData(() -> new QueueMatchData(queueEntry)));
            m.start();
        }

    }
}
