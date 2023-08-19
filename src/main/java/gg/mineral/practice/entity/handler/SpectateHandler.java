package gg.mineral.practice.entity.handler;

import org.bukkit.GameMode;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.managers.EventManager;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.FollowingScoreboard;
import gg.mineral.practice.scoreboard.impl.SpectatorScoreboard;
import gg.mineral.practice.traits.Spectatable;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpectateHandler {
    final Profile profile;
    @Getter
    Spectatable spectatable;
    @Getter
    Profile following;
    @Getter
    ProfileList followers = new ProfileList();

    public void follow(Profile p) {

        if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        profile.setPlayerStatus(PlayerStatus.FOLLOWING);
        this.following = p;
        p.getSpectateHandler().getFollowers().add(profile);
        profile.getInventory().setInventoryToFollow();
        profile.setScoreboard(FollowingScoreboard.INSTANCE);

        if (following.getPlayerStatus() == PlayerStatus.FIGHTING || following.isInEvent()) {
            spectate(this.following);
        }
    }

    public void stopSpectating() {
        if (profile.getPlayerStatus() != PlayerStatus.SPECTATING) {
            profile.message(ErrorMessages.NOT_SPEC);
            return;
        }

        if (spectatable != null) {
            spectatable.getSpectators().remove(profile);
            spectatable = null;
        }

        profile.teleportToLobby();

        profile.getPlayer().setGameMode(GameMode.SURVIVAL);
        if (profile.isInParty()) {
            profile.getInventory().setInventoryForParty();
        } else {
            profile.getInventory().setInventoryForLobby();
        }
        profile.setScoreboard(DefaultScoreboard.INSTANCE);
    }

    public void stopFollowing() {

        if (profile.getPlayerStatus() != PlayerStatus.FOLLOWING) {
            profile.message(ErrorMessages.NOT_FOLLOWING);
            return;
        }

        following.getSpectateHandler().getFollowers().remove(profile);
        following = null;

        stopSpectating();
    }

    public void spectate(Profile toBeSpectated) {

        if (toBeSpectated.equals(this.profile)) {
            this.profile.message(ErrorMessages.NOT_SPEC_SELF);
            return;
        }

        if (this.profile.getPlayerStatus() != PlayerStatus.IDLE
                && this.profile.getPlayerStatus() != PlayerStatus.FOLLOWING) {
            this.profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        this.spectatable = toBeSpectated.isInEvent() ? toBeSpectated.getEvent() : toBeSpectated.getMatch();

        if (spectatable.isEnded())
            return;

        if (!toBeSpectated.isInEvent()) {
            if (toBeSpectated.getPlayerStatus() != PlayerStatus.FIGHTING) {
                this.profile.message(ErrorMessages.PLAYER_NOT_IN_MATCH);
                return;
            }

            ChatMessage broadcastedMessage = ChatMessages.SPECTATING_YOUR_MATCH.clone().replace("%player%",
                    profile.getName());
            ProfileManager.broadcast(getSpectatable().getParticipants(), broadcastedMessage);
        }

        spectatable.getSpectators().add(profile);

        profile.getPlayer().setGameMode(GameMode.SPECTATOR);

        PlayerUtil.teleport(profile.getPlayer(),
                toBeSpectated.isInEvent() ? toBeSpectated.getEvent().getEventArena().getWaitingLocation()
                        : toBeSpectated.getPlayer().getLocation());

        if (toBeSpectated.isInEvent()) {
            ChatMessages.SPECTATING_EVENT.send(profile.getPlayer());
        } else {
            ChatMessages.SPECTATING.clone().replace("%player%", toBeSpectated.getName()).send(profile.getPlayer());
        }

        ChatMessages.STOP_SPECTATING.send(profile.getPlayer());

        updateVisiblity();

        if (profile.getPlayerStatus() == PlayerStatus.FOLLOWING) {
            return;
        }

        profile.getInventory().setInventoryForSpectating();
        profile.setPlayerStatus(PlayerStatus.SPECTATING);
        profile.setScoreboard(SpectatorScoreboard.INSTANCE);
    }

    private void updateVisiblity() {
        for (Profile profile : getSpectatable().getParticipants()) {
            this.profile.getPlayer().showPlayer(profile.getPlayer());
        }

        if (getSpectatable() instanceof Match) {
            Match spectatingMatch = (Match) getSpectatable();
            for (Match match : MatchManager.getMatches()) {
                match.updateVisiblity(spectatingMatch, profile);
            }

            return;
        }

        if (getSpectatable() instanceof Event) {
            Event spectatingEvent = (Event) getSpectatable();
            for (Event event : EventManager.getEvents()) {
                event.updateVisiblity(spectatingEvent, profile);
            }
        }
    }
}
