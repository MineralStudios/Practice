package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import gg.mineral.botapi.BotAPIPlugin;
import gg.mineral.botapi.entity.player.self.FakePlayer;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.items.ItemStacks;
import net.minecraft.server.v1_8_R3.EntityPlayer;

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
        this.profile2 = ProfileManager
                .getOrCreateProfile(((EntityPlayer) fakePlayer.getServerSide()).getBukkitEntity());
        addParicipants(profile2);
    }

    @Override
    public void onMatchStart() {
        super.onMatchStart();

        fakePlayer.startTasks();
        fakePlayer.setSprintingHeld(true);
        fakePlayer.startMoving(FakePlayer.Direction.FORWARDS);
        fakePlayer.getConfiguration().setPearlCooldown(data.getPearlCooldown());
    }

    @Override
    public void end(Profile victim) {
        super.end(victim);

        BotAPIPlugin.INSTANCE.getFakePlayerUtil().destroy(victim.getPlayer());
    }

    @Override
    public void giveQueueAgainItem(Profile profile) {
        if (BotAPIPlugin.INSTANCE.getFakePlayerUtil().isFakePlayer(profile.getPlayer()))
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

        BotAPIPlugin.INSTANCE.getFakePlayerUtil().destroy(attacker.getPlayer());
        BotAPIPlugin.INSTANCE.getFakePlayerUtil().destroy(victim.getPlayer());
    }

}
