package gg.mineral.practice.match;

import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.scoreboard.Team;

import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.util.Countdown;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.WorldUtil;
import gg.mineral.practice.util.messages.ErrorMessages;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class BedwarsMatch extends Match {
    ProfileList[] teams;
    Boolean[] bedsAlive;

    public BedwarsMatch(ProfileList profiles, int numberOfTeams, MatchData m) {
        super(m);
        participants.addAll(profiles);
        this.teams = new ProfileList[numberOfTeams];
        this.bedsAlive = new Boolean[numberOfTeams];
        Arrays.fill(teams, new ProfileList());
        Arrays.fill(bedsAlive, Boolean.TRUE);
        int teamSize = Math.max(participants.size() / numberOfTeams, 1);
        Iterator<Profile> iter = participants.iterator();

        for (int i = 0; i < numberOfTeams; i++) {
            ProfileList profileList = teams[i];

            while (profileList.size() < teamSize) {
                if (iter.hasNext()) {
                    profileList.add(iter.next());
                    continue;
                }

                return;
            }
        }

    }

    @Override
    public void start() {

        if (m.getArena() == null) {
            playerManager.broadcast(participants, ErrorMessages.ARENA_NOT_FOUND);
            end(participants.get(0));
            return;
        }

        matchManager.registerMatch(this);

        setWorldParameters(((CraftWorld) m.getArena().getBedWarsLocation(ChatColor.AQUA).getWorld()).getHandle());

        for (int i = 0; i < teams.length; i++) {
            ProfileList team = teams[i];
            ChatColor color = Arena.INCLUDED_COLORS.get(i);
            Location location = m.getArena().getBedWarsLocation(color);
            org.bukkit.scoreboard.Scoreboard teamsb = getDisplayNameBoard(i, color);

            for (Profile profile : team) {
                prepareForMatch(profile);
                profile.teleport(location);
                profile.bukkit().setScoreboard(teamsb);
            }
        }

        Countdown countdown = new Countdown(5, this);
        countdown.start();
    }

    @Override
    public void end(Profile victim) {
        if (ended) {
            return;
        }

        int teamIndex = getTeamIndex(victim);
        victim.setPearlCooldown(0);
        victim.heal();

        if (bedsAlive[teamIndex]) {
            // TODO: respawn
            return;
        }

        ProfileList remaining = teams[teamIndex];
        remaining.remove(victim);

        if (remaining.isEmpty()) {
            teams[teamIndex] = null;
        }

        int lastTeamIndex = getFinalTeamIndex();
        victim.removeFromMatch();

        if (lastTeamIndex == -1) {
            participants.remove(victim);

            Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
                if (victim.bukkit().isDead()) {
                    victim.bukkit().getHandle().playerConnection
                            .a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
                }

                victim.removePotionEffects();
                victim.spectate(participants.get(0));
                new Scoreboard(victim).setBoard();
            }, 1);
            return;
        }

        ProfileList attackerTeam = teams[lastTeamIndex];

        if (remaining.size() <= 1) {

            ended = true;

            for (int i = 0; i < attackerTeam.size(); i++) {
                Profile a = attackerTeam.get(i);
                a.heal();
                a.removePotionEffects();
                a.bukkit().sendMessage(CC.GOLD + "You won");
                a.setPearlCooldown(0);
                new Scoreboard(a).setBoard();
                a.removeFromMatch();
                Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
                    a.teleportToLobby();
                    if (a.isInParty()) {
                        a.setInventoryForParty();
                    } else {
                        a.setInventoryForLobby();
                    }
                }, 40);

            }

            matchManager.remove(this);
            victim.bukkit().sendMessage(CC.RED + "You lost");
            new Scoreboard(victim).setBoard();

            Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
                if (victim.bukkit().isDead()) {
                    victim.bukkit().getHandle().playerConnection
                            .a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
                }

                victim.removePotionEffects();
                victim.teleportToLobby();

                if (victim.isInParty()) {
                    victim.setInventoryForParty();
                } else {
                    victim.setInventoryForLobby();
                }
            }, 1);

            if (getSpectators().size() > 0) {
                Iterator<Profile> it = getSpectators().iterator();
                while (it.hasNext()) {
                    Profile p = it.next();
                    p.stopSpectating();
                }
            }

            if (world != null) {
                WorldUtil.deleteWorld(world);
                return;
            }

            for (Item item : m.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class)) {
                EntityHuman lastHolder = ((EntityItem) ((CraftItem) item).getHandle()).lastHolder;

                for (Profile participant : participants) {
                    if (lastHolder.getBukkitEntity().getUniqueId() == participant.getUUID()) {
                        item.remove();
                    }
                }
            }

            return;
        }

    }

    @Override
    public ProfileList getTeam(Profile p) {
        for (ProfileList list : teams) {
            if (list.contains(p)) {
                return list;
            }
        }

        return null;
    }

    public int getTeamIndex(Profile p) {
        for (int i = 0; i < teams.length; i++) {
            ProfileList list = teams[i];

            if (list.contains(p)) {
                return i;
            }
        }

        return -1;
    }

    public int getFinalTeamIndex() {
        int lastTeamIndex = -1;
        boolean isFinal = true;
        for (int i = 0; i < teams.length; i++) {
            ProfileList list = teams[i];

            if (list == null) {
                continue;
            }

            if (!isFinal) {
                return -1;
            }

            isFinal = false;
            lastTeamIndex = i;
        }

        return lastTeamIndex;
    }

    public org.bukkit.scoreboard.Scoreboard getDisplayNameBoard(int teamId, ChatColor color) {

        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Team teammates = scoreboard.registerNewTeam(color.name());
        teammates.setPrefix(color.toString());

        for (int i = 0; i < teams.length; i++) {
            ProfileList team = teams[i];
            ChatColor opponentColor = Arena.INCLUDED_COLORS.get(i);

            if (opponentColor == color) {
                for (int x = 0; x < team.size(); x++) {
                    teammates.addEntry(team.get(x).getName());
                }
                continue;
            }

            Team opponents = scoreboard.registerNewTeam(opponentColor.name());
            opponents.setPrefix(opponentColor.toString());

            for (int x = 0; x < team.size(); x++) {
                opponents.addEntry(team.get(x).getName());
            }
        }

        return scoreboard;
    }
}
