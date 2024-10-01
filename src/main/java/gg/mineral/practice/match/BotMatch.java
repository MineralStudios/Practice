package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import gg.mineral.bot.ai.goal.DrinkPotionGoal;
import gg.mineral.bot.ai.goal.EatGappleGoal;
import gg.mineral.bot.ai.goal.MeleeCombatGoal;
import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.QueueMatchData;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.items.ItemStacks;

public class BotMatch extends Match<QueueMatchData> {

    private FakePlayer fakePlayer;
    private final Difficulty difficulty;

    public BotMatch(Profile profile1, Difficulty difficulty, QueueMatchData matchData) {
        super(matchData);
        this.profile1 = profile1;
        this.difficulty = difficulty;
        addParicipants(profile1);
    }

    @Override
    public void start() {
        if (noArenas())
            return;

        MatchManager.registerMatch(this);
        Location location1 = getData().getArena().getLocation1().clone();
        Location location2 = getData().getArena().getLocation2().clone();

        setupLocations(location1, location2);

        teleportPlayers(location1, location2);

        this.fakePlayer = difficulty.spawn(profile1.getMatchData(), location2, "");
        Player bukkitPl = Bukkit.getPlayer(fakePlayer.getUuid());

        if (bukkitPl == null)
            throw new NullPointerException("Fake player is null");

        this.profile2 = ProfileManager
                .getOrCreateProfile(bukkitPl);

        handleOpponentMessages();
        startCountdown();
        addParicipants(profile2);

        prepareForMatch(participants);
    }

    @Override
    public void teleportPlayers(Location location1, Location location2) {
        PlayerUtil.teleport(profile1.getPlayer(), location1);
    }

    @Override
    public void onMatchStart() {
        super.onMatchStart();

        fakePlayer.getConfiguration().setPearlCooldown(getData().getPearlCooldown());
        fakePlayer.startGoals(new DrinkPotionGoal(fakePlayer), new EatGappleGoal(fakePlayer),
                new MeleeCombatGoal(fakePlayer));
    }

    @Override
    public void end(Profile victim) {
        super.end(victim);

        BotAPI.INSTANCE.despawn(victim.getPlayer().getUniqueId());
    }

    @Override
    public void giveQueueAgainItem(Profile profile) {
        if (BotAPI.INSTANCE.isFakePlayer(profile.getPlayer().getUniqueId()))
            return;
        if (getData().getQueueEntry() != null)
            Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE,
                    () -> profile.getInventory().setItem(profile.getInventory().getHeldItemSlot(),
                            ItemStacks.QUEUE_AGAIN,
                            () -> {
                                if (profile.getPlayerStatus() != PlayerStatus.QUEUEING) {
                                    profile.setPlayerStatus(PlayerStatus.QUEUEING);

                                    new BotMatch(profile, profile.getMatchData().getBotDifficulty(),
                                            BotMatch.this.getData()).start();
                                }
                            }),
                    20);
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        super.end(attacker, victim);

        BotAPI.INSTANCE.despawn(attacker.getPlayer().getUniqueId());
        BotAPI.INSTANCE.despawn(victim.getPlayer().getUniqueId());
    }

}
