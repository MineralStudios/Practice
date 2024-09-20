package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import gg.mineral.bot.ai.goal.DrinkPotionGoal;
import gg.mineral.bot.ai.goal.MeleeCombatGoal;
import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
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

    public void spawnBots() {
        Location location2 = getData().getArena().getLocation2().clone();
        this.fakePlayer = difficulty.spawn(profile1.getMatchData(), location2, "");
    }

    @Override
    public void start() {
        spawnBots();
        super.start();
    }

    @Override
    public void teleportPlayers(Location location1, Location location2) {
        PlayerUtil.teleport(profile1.getPlayer(), location1);

        Player bukkitPl = Bukkit.getPlayer(fakePlayer.getUuid());

        if (bukkitPl == null)
            throw new NullPointerException("Fake player is null");

        this.profile2 = ProfileManager
                .getOrCreateProfile(bukkitPl);
        addParicipants(profile2);
    }

    @Override
    public void onMatchStart() {
        super.onMatchStart();

        fakePlayer.getConfiguration().setPearlCooldown(getData().getPearlCooldown());
        fakePlayer.startGoals(new DrinkPotionGoal(fakePlayer), new MeleeCombatGoal(fakePlayer));
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
                            () -> new BotMatch(profile, profile.getMatchData().getBotDifficulty(),
                                    BotMatch.this.getData()).start()),
                    20);
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        super.end(attacker, victim);

        BotAPI.INSTANCE.despawn(attacker.getPlayer().getUniqueId());
        BotAPI.INSTANCE.despawn(victim.getPlayer().getUniqueId());
    }

}
