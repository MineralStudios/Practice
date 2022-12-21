package gg.mineral.practice.entity;

import org.bukkit.GameMode;

import gg.mineral.api.collection.GlueList;
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

public class Spectator {
    @Getter
    protected Profile profile;
    @Getter
    Spectatable spectatable;
    @Getter
    Profile following;
    @Getter
    GlueList<Profile> followers = new ProfileList();

    public void follow(Profile p) {

        if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        profile.setPlayerStatus(PlayerStatus.FOLLOWING);
        following = p;
        p.getFollowers().add(profile);
        profile.getInventory().setInventoryToFollow();
        new FollowingScoreboard(profile).setBoard();

        if (p.getPlayerStatus() == PlayerStatus.FIGHTING) {
            spectate(following);
        }
    }

    public void stopSpectating() {
        if (spectatable != null) {
            spectatable.getSpectators().remove(this);
            spectatable = null;
        }

        profile.teleportToLobby();

        if (profile.getPlayerStatus() == PlayerStatus.FOLLOWING) {
            profile.getInventory().setInventoryToFollow();
            return;
        }

        profile.getPlayer().setGameMode(GameMode.SURVIVAL);
        if (profile.isInParty()) {
            profile.getInventory().setInventoryForParty();
        } else {
            profile.getInventory().setInventoryForLobby();
        }
        new DefaultScoreboard(profile).setBoard();
    }

    public void stopSpectatingAndFollowing() {

        if (profile.getPlayerStatus() != PlayerStatus.SPECTATING
                && profile.getPlayerStatus() != PlayerStatus.FOLLOWING) {
            profile.message(ErrorMessages.NOT_SPEC_OR_FOLLOWING);
            return;
        }

        if (spectatable != null) {
            spectatable.getSpectators().remove(this);
            spectatable = null;
        }

        if (profile.getPlayerStatus() == PlayerStatus.FOLLOWING) {
            following.getFollowers().remove(this);
            following = null;
            profile.setPlayerStatus(PlayerStatus.IDLE);
        }

        profile.getPlayer().setGameMode(GameMode.SURVIVAL);

        profile.teleportToLobby();
        if (profile.isInParty()) {
            profile.getInventory().setInventoryForParty();
        } else {
            profile.getInventory().setInventoryForLobby();
        }
        new DefaultScoreboard(profile).setBoard();
    }

    public void spectate(Profile p) {

        if (p.equals(this)) {
            profile.message(ErrorMessages.NOT_SPEC_SELF);
            return;
        }

        if (p.isInEvent()) {
            spectateEvent(p.getEvent());
            return;
        }

        if (p.getPlayerStatus() != PlayerStatus.FIGHTING) {
            profile.message(ErrorMessages.PLAYER_NOT_IN_MATCH);
            return;
        }

        if (profile.getPlayerStatus() != PlayerStatus.IDLE && profile.getPlayerStatus() != PlayerStatus.FOLLOWING) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        this.spectatable = p.getMatch();

        if (spectatable.isEnded()) {
            return;
        }

        spectatable.getSpectators().add(this);

        profile.getPlayer().setGameMode(GameMode.SPECTATOR);

        PlayerUtil.teleport(profile.getPlayer(), p.getPlayer());
        ChatMessages.SPECTATING.clone().replace("%player%", p.getName()).send(profile.getPlayer());

        ChatMessages.STOP_SPECTATING.send(profile.getPlayer());

        ChatMessage broadcastedMessage = ChatMessages.SPECTATING_YOUR_MATCH.clone().replace("%player%",
                profile.getName());
        ProfileManager.broadcast(getSpectatable().getParticipants(), broadcastedMessage);

        profile.getInventory().setInventoryForSpectating();

        if (profile.getPlayerStatus() == PlayerStatus.FOLLOWING) {
            return;
        }

        new SpectatorScoreboard(profile).setBoard();
        profile.setPlayerStatus(PlayerStatus.SPECTATING);

        for (Profile profile : getSpectatable().getParticipants()) {
            profile.getPlayer().showPlayer(profile.getPlayer());
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

    public void spectateEvent(Event eventToSpectate) {

        if (eventToSpectate.isEnded()) {
            return;
        }

        if (profile.getPlayerStatus() != PlayerStatus.IDLE && profile.getPlayerStatus() != PlayerStatus.FOLLOWING) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        spectatable = eventToSpectate;
        getSpectatable().getSpectators().add(this);

        profile.getPlayer().setGameMode(GameMode.SPECTATOR);

        PlayerUtil.teleport(profile.getPlayer(), eventToSpectate.getEventArena().getWaitingLocation());
        ChatMessages.SPECTATING_EVENT.send(profile.getPlayer());
        ChatMessages.STOP_SPECTATING.send(profile.getPlayer());

        profile.getInventory().setInventoryForSpectating();

        if (profile.getPlayerStatus() == PlayerStatus.FOLLOWING) {
            return;
        }

        profile.setPlayerStatus(PlayerStatus.SPECTATING);

        for (Profile profile : getSpectatable().getParticipants()) {
            profile.getPlayer().showPlayer(profile.getPlayer());
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
