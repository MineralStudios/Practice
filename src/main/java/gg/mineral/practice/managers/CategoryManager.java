package gg.mineral.practice.managers;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.category.Category;
import lombok.Getter;
import lombok.val;

public class CategoryManager {
    @Getter
    final static FileConfiguration config = new FileConfiguration("category.yml", "plugins/Practice");
    @Getter
    static GlueList<Category> categories = new GlueList<>();

    public static void registerCategory(Category category) {
        categories.add(category);
    }

    public static void remove(Category category) {
        categories.remove(category);
        category.delete();

        for (val queuetype : QueuetypeManager.getQueuetypes().values())
            queuetype.getMenuEntries().removeInt(category);
    }

    public boolean contains(Category category) {
        for (val c : categories)
            if (c.equals(category))
                return true;

        return false;
    }

    public static Category getCategoryByName(String string) {
        for (val category : categories)
            if (category.getName().equalsIgnoreCase(string))
                return category;

        return null;
    }

    public void save() {

        for (val category : getCategories())
            category.save();

        config.save();

    }

    public static void load() {
        val configSection = getConfig().getConfigurationSection("Category.");

        if (configSection == null) {
            setDefaults();
            return;
        }

        for (val key : configSection.getKeys(false)) {

            if (key == null)
                continue;

            val category = new Category(key);

            category.load();

            registerCategory(category);
        }
    }

    public static void setDefaults() {
        val category = new Category("Default");
        category.setDefaults();
        registerCategory(category);
    }
}
