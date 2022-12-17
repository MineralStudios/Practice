package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.messages.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class TournamentMatch extends Match {

    Tournament tournament;

    public TournamentMatch(Profile profile1, Profile profile2, MatchData matchData, Tournament tournament) {
        super(profile1, profile2, matchData);
        nameTag.clearTagOnMatchStart(profile1.getPlayer(), profile2.getPlayer());
        this.tournament = tournament;
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        int attackerHealth = (int) attacker.getPlayer().getHealth();
        attacker.heal();
        attacker.removePotionEffects();
        attacker.getPlayer().hidePlayer(victim.getPlayer());

        int attackerAmountOfPots = attacker.getInventory().getNumber(Material.POTION, (short) 16421)
                + attacker.getInventory().getNumber(Material.MUSHROOM_SOUP);

        setInventoryStats(attacker, attackerHealth, attackerAmountOfPots);

        int victimAmountOfPots = victim.getInventory().getNumber(Material.POTION, (short) 16421)
                + victim.getInventory().getNumber(Material.MUSHROOM_SOUP);

        setInventoryStats(victim, 0, victimAmountOfPots);
        String viewinv = CC.YELLOW + "Match Results";
        TextComponent winmessage = new TextComponent(CC.GREEN + "Winner: " + CC.GRAY + attacker.getName());
        TextComponent splitter = new TextComponent(CC.D_GRAY + " - ");
        TextComponent losemessage = new TextComponent(CC.RED + " Loser: " + CC.GRAY + victim.getName());
        losemessage
                .setHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(CC.RED + "Health Potions Remaining: " + victimAmountOfPots + "\n"
                                        + CC.RED + "Hits: " + victim.getHitCount() + "\n" + CC.RED + "Health: 0")
                                        .create()));
        winmessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(CC.GREEN + "Health Potions Remaining: " + attackerAmountOfPots + "\n" + CC.GREEN
                        + "Hits: " + attacker.getHitCount() + "\n" + CC.GREEN + "Health: " + attackerHealth).create()));
        losemessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + victim.getName()));
        winmessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinventory " + attacker.getName()));
        attacker.getPlayer().sendMessage(CC.SEPARATOR);
        attacker.getPlayer().sendMessage(viewinv);
        attacker.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);
        attacker.getPlayer().sendMessage(CC.SEPARATOR);
        victim.getPlayer().sendMessage(CC.SEPARATOR);
        victim.getPlayer().sendMessage(viewinv);
        victim.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);
        victim.getPlayer().sendMessage(CC.SEPARATOR);
        resetPearlCooldown(attacker, victim);
        new DefaultScoreboard(profile1).setBoard();
        new DefaultScoreboard(profile2).setBoard();
        victim.removeFromMatch();
        MatchManager.remove(this);

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
            if (victim.getPlayer().isDead()) {
                victim.getPlayer().getHandle().playerConnection
                        .a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
            }

            victim.heal();
            victim.removePotionEffects();
            victim.teleportToLobby();
            victim.setInventoryForLobby();
            nameTag.giveTagAfterMatch(profile1.getPlayer(), profile2.getPlayer());
        }, 1);

        Match match = this;

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {

            attacker.removeFromMatch();
            tournament.removePlayer(victim);
            tournament.removeMatch(match);

            attacker.teleportToLobby();

            if (!tournament.isEnded()) {
                attacker.setPlayerStatus(PlayerStatus.IN_TOURAMENT);
                attacker.setInventoryForTournament();
            } else {
                attacker.setInventoryForLobby();
            }
            for (Profile p : getSpectators()) {
                p.getPlayer().sendMessage(CC.SEPARATOR);
                p.getPlayer().sendMessage(viewinv);
                p.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);
                p.getPlayer().sendMessage(CC.SEPARATOR);
                p.stopSpectating();
            }

            clearWorld();
        }, 40);

    }
}
