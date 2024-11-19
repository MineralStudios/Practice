package gg.mineral.practice.entity.handler;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.FollowingScoreboard;
import gg.mineral.practice.scoreboard.impl.SpectatorScoreboard;
import gg.mineral.practice.traits.Spectatable;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class SpectateHandler {
    final Profile profile;
    @Getter
    Spectatable spectatable;
    @Getter
    private Profile following;
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

        if (following.getPlayerStatus() == PlayerStatus.FIGHTING || following.isInEvent())
            spectate(this.following);
    }

    public void stopSpectating() {
        if (profile.getPlayerStatus() != PlayerStatus.SPECTATING
                && profile.getPlayerStatus() != PlayerStatus.FOLLOWING) {
            profile.message(ErrorMessages.NOT_SPEC);
            return;
        }

        if (spectatable != null) {
            spectatable.getSpectators().remove(profile);
            spectatable = null;
        }

        profile.teleportToLobby();

        profile.setGameMode(GameMode.SURVIVAL);
        if (profile.getPlayerStatus() != PlayerStatus.FOLLOWING) {
            if (profile.isInParty())
                profile.getInventory().setInventoryForParty();
            else
                profile.getInventory().setInventoryForLobby();
        }

        profile.setScoreboard(profile.getPlayerStatus() == PlayerStatus.FOLLOWING ? FollowingScoreboard.INSTANCE
                : DefaultScoreboard.INSTANCE);
    }

    public void stopFollowing() {

        if (profile.getPlayerStatus() != PlayerStatus.FOLLOWING) {
            profile.message(ErrorMessages.NOT_FOLLOWING);
            return;
        }

        following.getSpectateHandler().getFollowers().remove(profile);
        following = null;

        profile.setPlayerStatus(PlayerStatus.SPECTATING);

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

        if (spectatable == null) {
            Bukkit.getLogger().warning("SpectateHandler#spectate: spectatable is null");
            this.profile.message(ErrorMessages.PLAYER_NOT_IN_MATCH);
            return;
        }

        if (spectatable.isEnded())
            return;

        if (!toBeSpectated.isInEvent()) {
            if (toBeSpectated.getPlayerStatus() != PlayerStatus.FIGHTING) {
                this.profile.message(ErrorMessages.PLAYER_NOT_IN_MATCH);
                return;
            }

            val broadcastedMessage = ChatMessages.SPECTATING_YOUR_MATCH.clone().replace("%player%",
                    profile.getName());
            ProfileManager.broadcast(getSpectatable().getParticipants(), broadcastedMessage);
        }

        spectatable.getSpectators().add(profile);

        profile.setGameMode(GameMode.SPECTATOR);

        val arena = ArenaManager.getArenas().get(spectatable instanceof Event event ? event.getEventArenaId()
                : spectatable instanceof Match match ? match.getData().getArenaId() : 0);

        PlayerUtil.teleport(profile.getPlayer(),
                toBeSpectated.isInEvent() ? arena.getWaitingLocation()
                        : toBeSpectated.getPlayer().getLocation());

        if (toBeSpectated.isInEvent())
            ChatMessages.SPECTATING_EVENT.send(profile.getPlayer());
        else
            ChatMessages.SPECTATING.clone().replace("%player%", toBeSpectated.getName()).send(profile.getPlayer());

        ChatMessages.STOP_SPECTATING.send(profile.getPlayer());

        if (spectatable instanceof TeamMatch match) {
            val groups = match.getNametagGroups();

            if (groups != null) {
                val group = groups[0];

                if (group != null)
                    group.add(this.profile.getPlayer());
            }
        }

        profile.setScoreboard(SpectatorScoreboard.INSTANCE);
        if (profile.getPlayerStatus() == PlayerStatus.FOLLOWING)
            return;

        profile.getInventory().setInventoryForSpectating();
        profile.setPlayerStatus(PlayerStatus.SPECTATING);
    }
}
