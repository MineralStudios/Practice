package gg.mineral.practice.queue;

import org.bukkit.inventory.ItemStack;

public interface QueuetypeMenuEntry {
    default void setSlot(Queuetype queuetype, int slot) {
        queuetype.getMenuEntries().put(this, slot);
        save();
    }

    default void addToQueuetype(Queuetype queuetype, int slot) {
        queuetype.addMenuEntry(this, slot);
        save();
    }

    default void removeFromQueuetype(Queuetype queuetype) {
        queuetype.getMenuEntries().removeInt(this);
        save();
    }

    void save();

    boolean isBotsEnabled();

    ItemStack getDisplayItem();

    String getDisplayName();
}
