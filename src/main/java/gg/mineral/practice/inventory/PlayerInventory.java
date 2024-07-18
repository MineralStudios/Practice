package gg.mineral.practice.inventory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.QueueManagerMenu;
import gg.mineral.practice.inventory.menus.SelectGametypeMenu;
import gg.mineral.practice.inventory.menus.SelectModeMenu;
import gg.mineral.practice.inventory.menus.SelectQueuetypeMenu;
import gg.mineral.practice.managers.KitEditorManager;
import gg.mineral.practice.managers.LeaderboardManager;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.PlayerSettingsManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.managers.SpectateManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class PlayerInventory extends CraftInventoryPlayer {
    ConcurrentHashMap<Integer, Predicate<Profile>> dataMap = new ConcurrentHashMap<>();
    Profile holder;
    boolean fullClear = false;
    @Getter
    @Setter
    boolean inventoryClickCancelled = false;
    PlayerConnection playerConnection;

    public PlayerInventory(Profile holder) {
        super(((CraftInventoryPlayer) holder.getPlayer().getInventory()).getInventory());
        this.holder = holder;
        playerConnection = holder.getPlayer().getHandle().playerConnection;
    }

    public void clearHotbar() {
        for (int it = 0; it < 9; it++)
            setItem(it, null);
    }

    @Override
    public void setItem(int slot, ItemStack i) {
        try {
            dataMap.remove(slot);
            super.setItem(slot, i);
        } catch (Exception e) {

        }
    }

    public void setItem(int slot, ItemStack i, Predicate<Profile> d) {
        try {
            dataMap.put(slot, d);
            super.setItem(slot, i);
        } catch (Exception e) {

        }
    }

    public void setItem(int slot, ItemStack i, Runnable d) {
        try {
            dataMap.put(slot, p -> {
                d.run();
                return true;
            });
            super.setItem(slot, i);
        } catch (Exception e) {

        }
    }

    public Predicate<Profile> getTask(int i) {
        return dataMap.get(i);
    }

    public int getNumber(Material m, short durability) {
        int i = 0;

        for (ItemStack itemStack : getContents()) {

            if (itemStack == null)
                continue;

            if (itemStack.getType() != m)
                continue;

            if (itemStack.getDurability() != durability)
                continue;

            i++;
        }

        return i;
    }

    public int getNumberAndAmount(Material m, short durability) {
        int i = 0;

        for (ItemStack itemStack : getContents()) {

            if (itemStack == null)
                continue;

            if (itemStack.getType() != m)
                continue;

            if (itemStack.getDurability() != durability)
                continue;

            i += itemStack.getAmount();
        }

        return i;
    }

    public int getNumber(Material m) {
        int i = 0;

        for (ItemStack itemStack : getContents()) {

            if (itemStack == null)
                continue;

            if (itemStack.getType() != m)
                continue;

            i++;
        }

        return i;
    }

    @Override
    public void clear() {
        dataMap.clear();

        if (!fullClear) {
            clearHotbar();
            return;
        }

        fullClear = false;
        super.clear();
        setHelmet(null);
        setChestplate(null);
        setLeggings(null);
        setBoots(null);
    }

    public void setContents(ItemStack[] items) {
        net.minecraft.server.v1_8_R3.ItemStack[] mcItems = this.getInventory().getContents();
        for (int i = 0; i < mcItems.length; ++i) {
            if (i >= items.length) {
                setItem(i, null);
                continue;
            }

            if (i < 8)
                fullClear = true;

            setItem(i, items[i]);
        }
    }

    public void setInventoryToFollow() {
        setInventoryClickCancelled(true);
        clear();
        setItem(0, ItemStacks.STOP_FOLLOWING, (Runnable) holder.getSpectateHandler()::stopFollowing);
    }

    public void setInventoryForTournament() {
        setInventoryClickCancelled(true);
        clear();
        setItem(0, ItemStacks.WAIT_TO_LEAVE,
                (Runnable) () -> holder.message(ErrorMessages.CAN_NOT_LEAVE_YET));

        new BukkitRunnable() {
            @Override
            public void run() {
                setItem(0, ItemStacks.LEAVE_TOURNAMENT, (Runnable) holder::removeFromTournament);
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 20);
    }

    public void setInventoryForEvent() {
        setInventoryClickCancelled(true);
        clear();
        setItem(0, ItemStacks.WAIT_TO_LEAVE,
                (Runnable) () -> holder.message(ErrorMessages.CAN_NOT_LEAVE_YET));

        new BukkitRunnable() {
            @Override
            public void run() {
                setItem(0, ItemStacks.LEAVE_EVENT, (Runnable) holder::removeFromEvent);
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 20);
    }

    public void setInventoryForParty() {
        setInventoryClickCancelled(true);
        clear();
        setItem(8, ItemStacks.WAIT_TO_LEAVE, (Runnable) () -> holder.message(ErrorMessages.CAN_NOT_LEAVE_YET));

        new BukkitRunnable() {
            @Override
            public void run() {
                setItem(8, ItemStacks.LEAVE_PARTY, p -> p.getPlayer().performCommand("p leave"));
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 20);

        setItem(7, ItemStacks.LIST_PLAYERS, p -> p.getPlayer().performCommand("p list"));
        setItem(3, ItemStacks.OPEN_PARTY, p -> p.getPlayer().performCommand("p open"));
        setItem(4, ItemStacks.DUEL, p -> p.getPlayer().performCommand("duel"));
        setItem(5, ItemStacks.PARTY_SPLIT, p -> {
            p.openMenu(new SelectModeMenu(SubmitAction.P_SPLIT));
            return true;
        });

        GlueList<Queuetype> list = QueuetypeManager.getQueuetypes();

        for (int i = 0; i < list.size(); i++) {
            Queuetype queuetype = list.get(i);

            if (!queuetype.isUnranked())
                continue;

            ItemStack item = new ItemBuilder(queuetype.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + queuetype.getDisplayName())
                    .lore(CC.ACCENT + "Right click to queue.")
                    .build();
            setItem(queuetype.getSlotNumber(), item,
                    p -> {
                        if (!p.getParty().getPartyLeader().equals(p)) {
                            p.message(ErrorMessages.PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER);
                            return true;
                        }

                        if (p.getParty().getPartyMembers().size() != 2) {
                            p.message(ErrorMessages.PARTY_NOT_CORRECT_SIZE);
                            return true;
                        }

                        p.openMenu(new SelectGametypeMenu(queuetype, SelectGametypeMenu.Type.UNRANKED));
                        return true;
                    });
        }
    }

    public void setInventoryForLobby() {
        if (holder.getPlayerStatus() == PlayerStatus.QUEUEING) {
            return;
        }

        if (holder.getPlayerStatus() == PlayerStatus.FIGHTING && !holder.getMatch().isEnded()) {
            return;
        }

        setInventoryClickCancelled(true);
        clear();

        GlueList<Queuetype> list = QueuetypeManager.getQueuetypes();

        for (int i = 0; i < list.size(); i++) {
            Queuetype queuetype = list.get(i);

            ItemStack item = new ItemBuilder(queuetype.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + queuetype.getDisplayName())
                    .lore(CC.ACCENT + "Right click to queue.").build();
            setItem(queuetype.getSlotNumber(), item,
                    p -> {

                        if (queuetype.isCommunity()) {
                            p.message(ErrorMessages.COMING_SOON);
                            return true;
                        }

                        p.openMenu(new SelectGametypeMenu(queuetype,
                                queuetype.isUnranked() ? SelectGametypeMenu.Type.UNRANKED
                                        : SelectGametypeMenu.Type.QUEUE));
                        return true;
                    });
        }

        if (KitEditorManager.getEnabled()) {
            ItemStack editor = new ItemBuilder(KitEditorManager.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + KitEditorManager.getDisplayName())
                    .lore(CC.ACCENT + "Right click to edit a kit.")
                    .build();
            setItem(KitEditorManager.getSlot(), editor,
                    p -> {
                        p.openMenu(new SelectQueuetypeMenu(SelectGametypeMenu.Type.KIT_EDITOR));
                        return true;
                    });
        }

        if (PartyManager.getEnabled()) {
            ItemStack parties = new ItemBuilder(PartyManager.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + PartyManager.getDisplayName())
                    .lore(CC.ACCENT + "Right click to create a party.")
                    .build();
            setItem(PartyManager.getSlot(), parties,
                    p -> p.getPlayer().performCommand("p create"));
        }

        if (PlayerSettingsManager.getEnabled()) {
            ItemStack settings = new ItemBuilder(PlayerSettingsManager.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + PlayerSettingsManager.getDisplayName())
                    .lore(CC.ACCENT + "Right click to open settings.")
                    .build();
            setItem(PlayerSettingsManager.getSlot(), settings,
                    p -> p.getPlayer().performCommand("settings"));
        }

        if (SpectateManager.getEnabled()) {
            ItemStack spectate = new ItemBuilder(SpectateManager.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + SpectateManager.getDisplayName())
                    .lore(CC.ACCENT + "Right click to spectate.")
                    .build();
            setItem(SpectateManager.getSlot(), spectate,
                    p -> p.getPlayer().performCommand("spectate"));
        }

        if (LeaderboardManager.getEnabled()) {
            ItemStack leaderboard = new ItemBuilder(LeaderboardManager.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + LeaderboardManager.getDisplayName())
                    .lore(CC.ACCENT + "Right click to view.")
                    .build();
            setItem(LeaderboardManager.getSlot(), leaderboard,
                    p -> p.getPlayer().performCommand("leaderboard"));
        }
    }

    public void setInventoryForQueue() {
        setInventoryClickCancelled(true);
        clear();

        setItem(0, ItemStacks.LEAVE_QUEUE,
                () -> {
                    holder.removeFromQueue();
                    if (holder.isInParty())
                        setInventoryForParty();
                    else
                        setInventoryForLobby();
                });

        setItem(4, ItemStacks.QUEUE_MANAGER,
                () -> holder.openMenu(new QueueManagerMenu()));

        setItem(8, ItemStacks.QUEUE,
                () -> holder.openMenu(new SelectQueuetypeMenu(SelectGametypeMenu.Type.QUEUE)));
    }

    public void setInventoryForSpectating() {
        setInventoryClickCancelled(true);
        clear();
        setItem(0, ItemStacks.STOP_SPECTATING, (Runnable) holder.getSpectateHandler()::stopSpectating);
    }
}
