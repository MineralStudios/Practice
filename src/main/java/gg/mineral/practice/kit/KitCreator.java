package gg.mineral.practice.kit;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.scoreboard.impl.KitCreatorScoreboard;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class KitCreator {
    Profile profile;
    @Getter
    SubmitAction submitAction;

    public void save() {
        profile.getMatchData()
                .setKit(new Kit(profile.getInventory().getContents(), profile.getInventory().getArmorContents()));
        profile.getPlayer().closeInventory();
        ChatMessages.KIT_SAVED.send(profile.getPlayer());
    }

    public void start() {
        profile.setScoreboard(KitCreatorScoreboard.INSTANCE);
        PlayerUtil.teleportNoGlitch(profile.getPlayer(), KitEditorManager.getLocation());
        profile.getInventory().setInventoryClickCancelled(false);
        profile.getInventory().clear();

        for (Player player : profile.getPlayer().getWorld().getPlayers())
            profile.getPlayer().hidePlayer(player, false);

        profile.getPlayer().setGameMode(GameMode.CREATIVE);
    }
}
