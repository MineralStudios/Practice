package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.entity.Item;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.util.messages.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class EventMatch extends Match {

    Event event;

    public EventMatch(Profile player1, Profile player2, MatchData matchData, Event event) {
        super(player1, player2, matchData);
        nameTag.clearTagOnMatchStart(player1.bukkit().getPlayer(), player2.bukkit().getPlayer());
        this.event = event;
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        int attackerHealth = (int) attacker.getPlayer().getHealth();
        attacker.heal();
        attacker.removePotionEffects();
        attacker.getPlayer().hidePlayer(victim.getPlayer());

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
        attacker.getPlayer().sendMessage(CC.SEPARATOR);
        attacker.getPlayer().sendMessage(viewinv);
        attacker.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);
        attacker.getPlayer().sendMessage(CC.SEPARATOR);
        victim.getPlayer().sendMessage(CC.SEPARATOR);
        victim.getPlayer().sendMessage(viewinv);
        victim.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);
        victim.getPlayer().sendMessage(CC.SEPARATOR);
        attacker.setPearlCooldown(0);
        victim.setPearlCooldown(0);
        new DefaultScoreboard(profile1).setBoard();
        new DefaultScoreboard(profile2).setBoard();
        victim.removeFromMatch();
        MatchManager.remove(this);

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
            public void run() {
                if (victim.getPlayer().isDead()) {
                    victim.getPlayer().getHandle().playerConnection
                            .a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
                }

                victim.heal();
                victim.removePotionEffects();
                victim.teleportToLobby();
                victim.setInventoryForLobby();
                nameTag.giveTagAfterMatch(player1.bukkit().getPlayer(), player2.bukkit().getPlayer());
            }
        }, 1);

        Match match = this;

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, new Runnable() {
            public void run() {

                attacker.removeFromMatch();
                event.removePlayer(victim);
                event.removeMatch(match);

                if (!event.isEnded()) {
                    attacker.teleport(event.getEventArena().getWaitingLocation());
                    attacker.setPlayerStatus(PlayerStatus.IN_EVENT);
                    attacker.setInventoryForEvent();
                } else {
                    attacker.teleportToLobby();
                    attacker.setInventoryForLobby();
                }

                for (Profile p : getSpectators()) {
                    p.getPlayer().sendMessage(CC.SEPARATOR);
                    p.getPlayer().sendMessage(viewinv);
                    p.getPlayer().spigot().sendMessage(winmessage, splitter, losemessage);
                    p.getPlayer().sendMessage(CC.SEPARATOR);
                    p.stopSpectating();
                }
            }
        }, 40);

        for (Item item : data.getArena().getLocation1().getWorld().getEntitiesByClass(Item.class)) {
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
