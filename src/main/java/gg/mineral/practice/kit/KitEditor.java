package gg.mineral.practice.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.scoreboard.impl.KitEditorScoreboard;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class KitEditor {
    @Getter
    QueueEntry queueEntry;
    Profile profile;

    public void save() {
        ItemStack[] newKitContents = profile.getInventory().getContents();

        queueEntry.getCustomKits().put(profile.getUUID(), newKitContents);
        FileConfiguration config = ProfileManager.getPlayerConfig();
        String path = profile.getName() + ".KitData." + queueEntry.getGametype().getName() + "."
                + queueEntry.getQueuetype().getName() + ".";

        for (int f = 0; f < newKitContents.length; f++) {
            ItemStack newItem = newKitContents[f];
            ItemStack oldItem = queueEntry.getGametype().getKit().getContents()[f];

            boolean newItemNull = newItem == null;

            if (newItemNull && oldItem == null) {
                continue;
            }

            if (newItemNull) {
                config.set(path + f, "empty");
                continue;
            }

            if (newItem.isSimilar(oldItem)) {
                continue;
            }

            config.set(path + f, newItem);
        }

        config.save();

        profile.getPlayer().closeInventory();
        ChatMessages.KIT_SAVED.send(profile.getPlayer());
    }

    public void start() {
        profile.setScoreboard(KitEditorScoreboard.INSTANCE);
        PlayerUtil.teleportNoGlitch(profile.getPlayer(), KitEditorManager.getLocation());
        profile.getInventory().setInventoryClickCancelled(false);
        profile.getInventory().clear();

        for (Player player : profile.getPlayer().getWorld().getPlayers()) {
            profile.getPlayer().hidePlayer(player, false);
        }

        profile.getInventory().setContents(queueEntry.getGametype().getKit().getContents());
    }
}
