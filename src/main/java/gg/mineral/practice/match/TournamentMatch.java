package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.tournaments.Tournament;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class TournamentMatch extends Match {

    Tournament tournament;

    public TournamentMatch(Profile player1, Profile player2, MatchData m, Tournament t) {
        super(player1, player2, m);
        this.tournament = t;
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        int attackerHealth = (int) attacker.bukkit().getHealth();
        attacker.heal();
        attacker.removePotionEffects();
        attacker.bukkit().hidePlayer(victim.bukkit());

        int attackerAmountOfPots = attacker.getNumber(Material.POTION, (short) 16421)
                + attacker.getNumber(Material.MUSHROOM_SOUP);

        setInventoryStats(attacker, attackerHealth, attackerAmountOfPots);

        int victimAmountOfPots = victim.getNumber(Material.POTION, (short) 16421)
                + victim.getNumber(Material.MUSHROOM_SOUP);

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
        attacker.bukkit().sendMessage(CC.SEPARATOR);
        attacker.bukkit().sendMessage(viewinv);
        attacker.bukkit().spigot().sendMessage(winmessage, splitter, losemessage);
        attacker.bukkit().sendMessage(CC.SEPARATOR);
        victim.bukkit().sendMessage(CC.SEPARATOR);
        victim.bukkit().sendMessage(viewinv);
        victim.bukkit().spigot().sendMessage(winmessage, splitter, losemessage);
        victim.bukkit().sendMessage(CC.SEPARATOR);
        attacker.setPearlCooldown(0);
        victim.setPearlCooldown(0);
        new Scoreboard(player1).setBoard();
        new Scoreboard(player2).setBoard();
        victim.removeFromMatch();
        matchManager.remove(this);

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
            public void run() {
                if (victim.bukkit().isDead()) {
                    victim.bukkit().getHandle().playerConnection
                            .a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
                }

                victim.heal();
                victim.removePotionEffects();
                victim.teleportToLobby();
                victim.setInventoryForLobby();
            }
        }, 1);

        Match match = this;

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
            public void run() {

                attacker.removeFromMatch();
                tournament.removePlayer(victim);
                tournament.removeMatch(match);

                if (!tournament.isEnded()) {
                    if (tournament.isEvent()) {
                        attacker.teleport(tournament.getWaitingLocation());
                    } else {
                        attacker.teleportToLobby();
                    }

                    attacker.setPlayerStatus(PlayerStatus.IN_TOURAMENT);
                    attacker.setInventoryForTournament();
                } else {
                    attacker.teleportToLobby();
                    attacker.setInventoryForLobby();
                }

                if (getSpectators().size() > 0) {
                    for (Profile p : getSpectators()) {
                        p.bukkit().sendMessage(CC.SEPARATOR);
                        p.bukkit().sendMessage(viewinv);
                        p.bukkit().spigot().sendMessage(winmessage, splitter, losemessage);
                        p.bukkit().sendMessage(CC.SEPARATOR);
                        p.stopSpectating();
                    }
                }
            }
        }, 40);

        for (Item item : m.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class)) {
            EntityHuman lastHolder = ((EntityItem) ((CraftItem) item).getHandle()).lastHolder;

            if (lastHolder == null) {
                continue;
            }

            for (Profile participant : participants) {
                if (lastHolder.getBukkitEntity().getUniqueId() == participant.getUUID()) {
                    item.remove();
                }
            }
        }

        for (Location location : buildLog) {
            location.getBlock().setType(Material.AIR);
        }
    }
}
