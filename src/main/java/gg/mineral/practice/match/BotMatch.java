package gg.mineral.practice.match;

import org.bukkit.Location;

import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.server.fakeplayer.FakePlayer;

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
        PlayerUtil.teleport(profile2.getPlayer(), location2);
    }

    @Override
    public void onMatchStart(Profile p) {
        super.onMatchStart(p);

        fakePlayer.startSprinting();
        fakePlayer.startSprintReset();
        fakePlayer.startStrafing();
        fakePlayer.startItemUsage();
        fakePlayer.setPearlCooldown(data.getPearlCooldown());
        fakePlayer.startMoving(FakePlayer.Direction.FORWARDS);
    }

    @Override
    public void end(Profile victim) {
        super.end(victim);

        FakePlayer.destroy(victim.getPlayer().getHandle());
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        super.end(attacker, victim);

        FakePlayer.destroy(attacker.getPlayer().getHandle());
        FakePlayer.destroy(victim.getPlayer().getHandle());
    }

}
