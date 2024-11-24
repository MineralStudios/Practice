package gg.mineral.practice.kit;

import org.bukkit.GameMode;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.scoreboard.impl.KitCreatorScoreboard;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@AllArgsConstructor
public class KitCreator {
    Profile profile;
    @Getter
    SubmitAction submitAction;

    public void save() {
        profile.getDuelSettings()
                .setKit(new Kit(profile.getInventory().getContents(), profile.getInventory().getArmorContents()));
        profile.getPlayer().closeInventory();
        ChatMessages.KIT_SAVED.send(profile.getPlayer());
    }

    public void start() {
        profile.setPlayerStatus(PlayerStatus.KIT_CREATOR);
        profile.setScoreboard(KitCreatorScoreboard.INSTANCE);
        PlayerUtil.teleportNoGlitch(profile, KitEditorManager.getLocation());
        profile.getInventory().setInventoryClickCancelled(false);
        profile.getInventory().clear();

        for (val player : profile.getPlayer().getWorld().getPlayers())
            profile.removeFromView(player.getUniqueId());

        profile.setGameMode(GameMode.CREATIVE);
    }
}
