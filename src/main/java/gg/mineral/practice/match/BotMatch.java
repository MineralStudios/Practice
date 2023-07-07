package gg.mineral.practice.match;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.server.fakeplayer.FakePlayer;

public class BotMatch extends Match {

    FakePlayer fakePlayer;

    public BotMatch(Profile profile1, FakePlayer fakePlayer, MatchData matchData) {
        super(profile1, ProfileManager.getOrCreateProfile(fakePlayer.getServerSide().getBukkitEntity()), matchData);
        this.fakePlayer = fakePlayer;
    }

    @Override
    public void onMatchStart(Profile p) {
        super.onMatchStart(p);

        fakePlayer.startSprinting();
        fakePlayer.startSprintReset();
        fakePlayer.startStrafing();
        fakePlayer.startItemUsage();
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
