package gg.mineral.practice.category;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.CategoryManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.QueuetypeMenuEntry;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.practice.util.items.ItemStacks;
import lombok.Getter;
import lombok.val;

public class Category implements SaveableData, QueuetypeMenuEntry {

    final FileConfiguration config = CategoryManager.getConfig();

    @Getter
    ItemStack displayItem;
    @Getter
    String displayName;
    @Getter
    private final String name;
    @Getter
    GlueList<Gametype> gametypes = new GlueList<>();
    final String path;
    private static final String ENABLED_STRING = ".Enabled";

    public Category(String name) {
        this.name = name;
        this.path = "Category." + getName() + ".";
    }

    @Override
    public boolean isBotsEnabled() {
        for (val gametype : gametypes)
            if (gametype.isBotsEnabled())
                return true;
        return false;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        save();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        save();
    }

    public void addGametype(Gametype gametype) {
        gametypes.add(gametype);
        save();
    }

    public void removeGametype(Gametype gametype) {
        gametypes.remove(gametype);
        save();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Category category)
            return category.getName().equalsIgnoreCase(getName());
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public void save() {
        for (val q : QueuetypeManager.getQueuetypes().values()) {

            if (!q.getMenuEntries().containsKey(this)) {
                config.set(path + q.getName() + ENABLED_STRING, false);
                continue;
            }

            int slot = q.getMenuEntries().getInt(this);

            config.set(path + q.getName() + ENABLED_STRING, true);
            config.set(path + q.getName() + ".Slot", slot);
        }

        config.set(path + "DisplayName", displayName);
        config.set(path + "DisplayItem", displayItem);

        config.save();
    }

    @Override
    public void load() {
        this.displayItem = config.getItemstack(path + "DisplayItem",
                ItemStacks.DEFAULT_CATEGORY_DISPLAY_ITEM);
        this.displayName = config.getString(path + "DisplayName", getName());

        for (val q : QueuetypeManager.getQueuetypes().values())
            if (config.getBoolean(path + q.getName() + ENABLED_STRING, false))
                q.getMenuEntries().put(this, (int) config.getInt(path + q.getName() + ".Slot", 0));

    }

    @Override
    public void setDefaults() {
        this.displayItem = ItemStacks.DEFAULT_CATEGORY_DISPLAY_ITEM;
        this.displayName = getName();

        for (val q : QueuetypeManager.getQueuetypes().values())
            if (config.getBoolean(path + q.getName() + ENABLED_STRING, false))
                q.getMenuEntries().put(this, 0);
    }

    @Override
    public void delete() {
        config.remove("Category." + getName());
        config.save();
    }
}
