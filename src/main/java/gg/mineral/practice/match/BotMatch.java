package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import gg.mineral.bot.ai.goal.DrinkPotionGoal;
import gg.mineral.bot.ai.goal.EatGappleGoal;
import gg.mineral.bot.ai.goal.MeleeCombatGoal;
import gg.mineral.bot.ai.goal.ReplaceArmorGoal;
import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.items.ItemStacks;
import lombok.val;

public class BotMatch extends Match {

    private ClientInstance clientInstance;
    private final BotConfiguration config;

    public BotMatch(Profile profile1, BotConfiguration config, MatchData matchData) {
        super(matchData);
        this.profile1 = profile1;
        this.config = config;
        addParicipants(profile1);
    }

    @Override
    public void start() {
        if (noArenas())
            return;

        MatchManager.registerMatch(this);
        val arena = ArenaManager.getArenas().get(getData().getArenaId());
        Location location1 = arena.getLocation1().clone();
        Location location2 = arena.getLocation2().clone();

        setupLocations(location1, location2);

        teleportPlayers(location1, location2);

        this.clientInstance = Difficulty.spawn(config, location2);
        val bukkitPl = Bukkit.getPlayer(config.getUuid());

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

        clientInstance.getConfiguration().setPearlCooldown(getData().getPearlCooldown());
        clientInstance.startGoals(new ReplaceArmorGoal(clientInstance), new DrinkPotionGoal(clientInstance),
                new EatGappleGoal(clientInstance),
                new MeleeCombatGoal(clientInstance));
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

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE,
                () -> profile.getInventory().setItem(profile.getInventory().getHeldItemSlot(),
                        ItemStacks.QUEUE_AGAIN,
                        () -> {
                            if (profile.getPlayerStatus() != PlayerStatus.QUEUEING) {
                                profile.setPlayerStatus(PlayerStatus.QUEUEING);

                                new BotMatch(profile, config,
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
