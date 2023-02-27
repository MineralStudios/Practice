package gg.mineral.practice.inventory;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;

public interface Menu {

    public void setSlot(int slot, ItemStack item);

    public void setSlot(int slot, ItemStack item, Consumer<Interaction> d);

    public void setSlot(int slot, ItemStack item, Runnable d);

    public void add(ItemStack item);

    public void add(ItemStack item, Consumer<Interaction> d);

    public void add(ItemStack item, Runnable d);

    public boolean update();

    public void onClose();

    public ItemStack getItemBySlot(int slot);

    public ItemStack getItemByType(Material m);

    public boolean contains(ItemStack item);

    public void open(Profile viewer);

    public void reload();

    public void setContents(ItemStack[] contents);

    public Consumer<Interaction> getTask(int slot);

    public void clear();

    public boolean isClosed();

    public void setClosed(boolean b);

    public boolean getClickCancelled();

    public Inventory getInventory();
}
