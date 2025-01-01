package gg.mineral.practice.inventory;

import gg.mineral.practice.entity.Profile;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface Menu {

    void setSlot(int slot, ItemStack item);

    void setSlot(int slot, ItemStack item, Consumer<Interaction> d);

    void add(ItemStack item);

    void add(ItemStack item, Consumer<Interaction> d);

    void update();

    boolean shouldUpdate();

    void onClose();

    ItemStack getItemBySlot(int slot);

    ItemStack getItemByType(Material m);

    boolean contains(ItemStack item);

    void open(Profile viewer);

    void reload();

    void setContents(ItemStack[] contents);

    Consumer<Interaction> getTask(int slot);

    void clear();

    boolean isClosed();

    void setClosed(boolean b);

    boolean isClickCancelled();

    Inventory getInventory();
}
