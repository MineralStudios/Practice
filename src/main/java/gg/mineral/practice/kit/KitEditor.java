package gg.mineral.practice.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.scoreboard.impl.KitEditorScoreboard;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class KitEditor {
    @Getter
    Gametype gametype;
    @Getter
    Queuetype queuetype;
    Profile profile;

    public void save(int loadoutSlot) {
        ItemStack[] newKitContents = profile.getInventory().getContents();

        short hash = (short) (queuetype.getId() << 8 | gametype.getId());
        Int2ObjectOpenHashMap<ItemStack[]> kitLoadouts = profile.getCustomKits(queuetype, gametype, hash);

        if (kitLoadouts == null)
            kitLoadouts = new Int2ObjectOpenHashMap<>();

        kitLoadouts.put(loadoutSlot, newKitContents);
        profile.getCustomKits().put(hash, kitLoadouts);
        FileConfiguration config = ProfileManager.getPlayerConfig();
        String path = profile.getName() + ".KitData." + gametype.getName() + "."
                + queuetype.getName() + "." + loadoutSlot + ".";

        for (int f = 0; f < newKitContents.length; f++) {
            ItemStack newItem = newKitContents[f];
            ItemStack oldItem = gametype.getKit().getContents()[f];

            boolean newItemNull = newItem == null;

            if (newItemNull && oldItem == null)
                continue;

            if (newItemNull) {
                config.set(path + f, "empty");
                continue;
            }

            if (newItem.isSimilar(oldItem))
                continue;

            config.set(path + f, newItem);
        }

        config.save();

        ChatMessages.KIT_SAVED.send(profile.getPlayer());
    }

    public void start() {
        profile.setScoreboard(KitEditorScoreboard.INSTANCE);
        PlayerUtil.teleportNoGlitch(profile.getPlayer(), KitEditorManager.getLocation());
        profile.getInventory().setInventoryClickCancelled(false);
        profile.getInventory().clear();

        for (Player player : profile.getPlayer().getWorld().getPlayers())
            profile.removeFromView(player.getUniqueId());

        profile.getInventory().setContents(gametype.getKit().getContents());
    }

    public void delete(int loadoutSlot) {
        short hash = (short) (queuetype.getId() << 8 | gametype.getId());
        Int2ObjectOpenHashMap<ItemStack[]> kitLoadouts = profile.getCustomKits(queuetype, gametype, hash);

        if (kitLoadouts == null)
            return;

        kitLoadouts.remove(loadoutSlot);
        profile.getCustomKits().put(hash, kitLoadouts);
        FileConfiguration config = ProfileManager.getPlayerConfig();
        String path = profile.getName() + ".KitData." + gametype.getName() + "."
                + queuetype.getName() + "." + loadoutSlot;

        config.set(path, null);

        config.save();

        ChatMessages.KIT_DELETED.send(profile.getPlayer());
    }

}
