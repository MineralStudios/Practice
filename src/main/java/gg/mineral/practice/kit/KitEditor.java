package gg.mineral.practice.kit;

import gg.mineral.practice.entity.PlayerStatus;
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
import lombok.val;

@AllArgsConstructor
public class KitEditor {
    @Getter
    Gametype gametype;
    @Getter
    Queuetype queuetype;
    Profile profile;

    public void save(int loadoutSlot) {
        val newKitContents = profile.getInventory().getContents();

        short hash = (short) (queuetype.getId() << 8 | gametype.getId());
        var kitLoadouts = profile.getCustomKits(queuetype, gametype, hash);

        if (kitLoadouts == null)
            kitLoadouts = new Int2ObjectOpenHashMap<>();

        kitLoadouts.put(loadoutSlot, newKitContents);
        profile.getCustomKits().put(hash, kitLoadouts);
        val config = ProfileManager.getPlayerConfig();
        val path = profile.getName() + ".KitData." + gametype.getName() + "."
                + queuetype.getName() + "." + loadoutSlot + ".";

        for (int f = 0; f < newKitContents.length; f++) {
            val newItem = newKitContents[f];
            val oldItem = gametype.getKit().getContents()[f];

            boolean newItemNull = newItem == null;

            if (newItemNull && oldItem == null)
                continue;

            if (newItemNull) {
                config.set(path + f, "empty");
                continue;
            }

            if (newItem != null && newItem.isSimilar(oldItem))
                continue;

            config.set(path + f, newItem);
        }

        config.save();

        ChatMessages.KIT_SAVED.send(profile.getPlayer());
    }

    public void start() {
        profile.setPlayerStatus(PlayerStatus.KIT_EDITOR);
        profile.setScoreboard(KitEditorScoreboard.INSTANCE);
        PlayerUtil.teleportNoGlitch(profile, KitEditorManager.getLocation());
        profile.getInventory().setInventoryClickCancelled(false);
        profile.getInventory().clear();

        for (val player : profile.getPlayer().getWorld().getPlayers())
            profile.removeFromView(player.getUniqueId());

        profile.getInventory().setContents(gametype.getKit().getContents());
    }

    public void delete(int loadoutSlot) {
        short hash = (short) (queuetype.getId() << 8 | gametype.getId());
        val kitLoadouts = profile.getCustomKits(queuetype, gametype, hash);

        if (kitLoadouts == null)
            return;

        kitLoadouts.remove(loadoutSlot);
        profile.getCustomKits().put(hash, kitLoadouts);
        val config = ProfileManager.getPlayerConfig();
        val path = profile.getName() + ".KitData." + gametype.getName() + "."
                + queuetype.getName() + "." + loadoutSlot;

        config.set(path, null);

        config.save();

        ChatMessages.KIT_DELETED.send(profile.getPlayer());
    }

}
