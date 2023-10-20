package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import gg.mineral.botapi.entity.FakePlayer;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.items.ItemStacks;

public class BotMatch extends Match {

    FakePlayer fakePlayer;
    Difficulty difficulty;

    public BotMatch(Profile profile1, Difficulty difficulty, MatchData matchData) {
        super(matchData);
        this.profile1 = profile1;
        this.difficulty = difficulty;
        addParicipants(profile1);
    }

    @Override
    public void teleportPlayers(Location location1, Location location2) {
        PlayerUtil.teleport(profile1.getPlayer(), location1);
        fakePlayer = difficulty.spawn(profile1, location2, "");
        this.profile2 = ProfileManager.getOrCreateProfile(fakePlayer.getServerSide().getBukkitEntity());
        addParicipants(profile2);
    }

    @Override
    public void onMatchStart() {
        super.onMatchStart();

        fakePlayer.startAiming();
        fakePlayer.startAttacking();
        fakePlayer.startSprinting();
        fakePlayer.startSprintReset();
        fakePlayer.startStrafing();
        fakePlayer.startItemUsage();
        fakePlayer.getConfiguration().setPearlCooldown(data.getPearlCooldown());
        fakePlayer.startMoving(FakePlayer.Direction.FORWARDS);
        fakePlayer.startPathfinding();
    }

    @Override
    public void end(Profile victim) {
        super.end(victim);

        FakePlayer.destroy(victim.getPlayer().getHandle());
    }

    @Override
    public void giveQueueAgainItem(Profile profile) {
        if (FakePlayer.isFakePlayer(profile.getPlayer().getHandle()))
            return;
        if (getData().getQueueEntry() != null) {
            Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
                int slot = profile.getInventory().getHeldItemSlot();

                profile.getInventory().setItem(slot, ItemStacks.QUEUE_AGAIN,
                        () -> {
                            BotMatch m = new BotMatch(profile, profile.getMatchData().getBotDifficulty(),
                                    BotMatch.this.getData());
                            m.start();
                        });
            }, 20);
        }
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        super.end(attacker, victim);

        FakePlayer.destroy(attacker.getPlayer().getHandle());
        FakePlayer.destroy(victim.getPlayer().getHandle());
    }

}
