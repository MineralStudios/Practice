package gg.mineral.practice.inventory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryPlayer;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class PlayerInventory extends CraftInventoryPlayer {
    ConcurrentHashMap<Integer, Predicate<Profile>> dataMap = new ConcurrentHashMap<>();
    CraftPlayer holder;
    boolean fullClear = false;
    PlayerConnection playerConnection;

    public PlayerInventory(org.bukkit.inventory.PlayerInventory playerInventory) {
        super(((CraftInventoryPlayer) playerInventory).getInventory());
        holder = (CraftPlayer) this.getHolder();
        playerConnection = holder.getHandle().playerConnection;
    }

    public void clearHotbar() {
        for (int it = 0; it < 9; it++) {
            setItem(it, null);
        }
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

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() != m) {
                continue;
            }

            if (itemStack.getDurability() != durability) {
                continue;
            }

            i++;
        }

        return i;
    }

    public int getNumber(Material m) {
        int i = 0;

        for (ItemStack itemStack : getContents()) {

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() != m) {
                continue;
            }

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
            } else {

                if (i < 8) {
                    fullClear = true;
                }

                setItem(i, items[i]);
            }
        }

        holder.updateInventory();
    }
}
