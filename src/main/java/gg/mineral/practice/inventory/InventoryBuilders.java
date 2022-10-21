package gg.mineral.practice.inventory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gg.mineral.api.inventory.Interaction;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.api.inventory.MineralInventory;
import gg.mineral.api.inventory.MineralInventoryAPI;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.gametype.QueuetypeElement;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.IntValue;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class InventoryBuilders {
    public static InventoryBuilder<Gametype> ADD_ITEMS = new InventoryBuilder<Gametype>() {
        List<Material> EXCLUDED = Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.MUSHROOM_SOUP, Material.POTION,
                Material.GOLDEN_APPLE, Material.ENDER_PEARL, Material.WATER_BUCKET, Material.LAVA_BUCKET);

        List<Material> INCLUDED = Arrays.asList(Material.COOKED_BEEF, Material.GOLDEN_CARROT, Material.GRILLED_PORK);

        final String TITLE = CC.BLUE + "Add Items";

        @Override
        public MineralInventory build(Gametype gametype) {
            MineralInventory menu = MineralInventoryAPI.createMenu(TITLE);

            for (ItemStack is : gametype.getKit().getContents()) {

                if (is == null) {
                    continue;
                }

                Material material = is.getType();

                if (EXCLUDED.contains(material)) {
                    continue;
                }

                if (menu.contains(itemstack -> itemstack.equals(is))) {
                    continue;
                }

                Predicate<Interaction> interactionPredicate = interaction -> {
                    MineralInventoryAPI.getOrCreatePlayerInventory(interaction.getPlayer()).add(is);
                    return true;
                };

                if (INCLUDED.contains(material)) {
                    for (Material m : INCLUDED) {
                        menu.add(new ItemStack(m, 64), interactionPredicate);
                    }
                    continue;
                }

                menu.add(is, interactionPredicate);
            }

            return menu;
        }

    };

    public static InventoryBuilder<List<QueuetypeElement>> LEADERBOARD_MENU = new InventoryBuilder<List<QueuetypeElement>>() {
        final String TITLE = CC.BLUE + "Leaderboards";

        @Override
        public MineralInventory build(List<QueuetypeElement> queuetypeElememts) {

            MineralInventory menu = MineralInventoryAPI.createMenu(TITLE);

            for (QueuetypeElement queuetypeElement : queuetypeElememts) {

                boolean isGametype = queuetypeElement instanceof Gametype;

                if (isGametype) {
                    Gametype gametype = (Gametype) queuetypeElement;

                    if (gametype.isInCatagory()) {
                        continue;
                    }
                }

                ItemStack item = new ItemBuilder(queuetypeElement.getDisplayItem())
                        .name(queuetypeElement.getDisplayName()).build();
                ItemMeta meta = item.getItemMeta();
                meta.setLore(queuetypeElement.getLeaderboardLore());
                item.setItemMeta(meta);

                if (isGametype) {
                    menu.add(item);
                    continue;
                }

                menu.add(item, interaction -> {
                    Catagory catagory = (Catagory) queuetypeElement;
                    menu.clear();
                    LEADERBOARD_MENU.build(new GlueList<>(catagory.getGametypes())).open(interaction.getPlayer());
                    return true;
                });

            }

            return menu;
        }

    };

    public static InventoryBuilder<String> ELO_MENU = new InventoryBuilder<String>() {

        @Override
        public MineralInventory build(String playerName) {

            MineralInventory menu = MineralInventoryAPI.createMenu(CC.BLUE + playerName);

            Profile profile = PlayerManager.get(p -> p.getName().equalsIgnoreCase(playerName));

            boolean offline = profile == null;

            for (Gametype gametype : GametypeManager.list()) {
                ItemStack item;

                try {
                    item = new ItemBuilder(gametype.getDisplayItem())
                            .name(gametype.getDisplayName())
                            .lore(CC.ACCENT + playerName + "'s Elo: "
                                    + (offline ? PlayerManager.getOfflinePlayerElo(playerName, gametype)
                                            : gametype.getElo(profile)))
                            .build();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }

                menu.add(item);
            }

            return menu;
        }

    };

    public static InventoryBuilder<MineralInventory> SETTINGS_MENU = new InventoryBuilder<MineralInventory>() {
        final String TITLE = CC.BLUE + "Settings";

        @Override
        public MineralInventory build(MineralInventory currentMenu) {

            MineralInventory menu = currentMenu == null ? MineralInventoryAPI.createMenu(TITLE)
                    : currentMenu;

            menu.add(ItemStacks.TOGGLE_PLAYER_VISIBILITY,
                    interaction -> {
                        interaction.getPlayer().performCommand("toggleplayervisibility");
                        return true;
                    });

            menu.add(ItemStacks.TOGGLE_DUEL_REQUESTS,
                    interaction -> {
                        interaction.getPlayer().performCommand("toggleduelrequests");
                        return true;
                    });

            return menu;
        }

    };

    public static InventoryBuilder<IntValue<Profile>> MODIFY_VALUE_MENU = new InventoryBuilder<IntValue<Profile>>() {
        final String TITLE = CC.BLUE + "Modify Value";

        @Override
        public MineralInventory build(IntValue<Profile> intValue) {

            MineralInventory menu = MineralInventoryAPI.createMenu(TITLE);

            menu.whenOpened(interaction -> {
                Profile profile = PlayerManager.get(p -> p.getUUID().equals(interaction.getPlayer().getUniqueId()));
                ItemStack currentValue = new ItemBuilder(Material.STONE_SWORD)
                        .name("Current Value: " + intValue.get(profile))
                        .lore(CC.ACCENT + "Click To Apply Changes").build();

                menu.set(4, 0, currentValue, interaction1 -> {
                    menu.open(interaction1.getPlayer());
                    return true;
                });

                return false;
            });

            menu.set(2, 0, ItemStacks.SUBTRACT_1, interaction -> {
                Profile profile = PlayerManager.get(p -> p.getUUID().equals(interaction.getPlayer().getUniqueId()));

                if (intValue.get(profile) >= 1) {
                    intValue.decrement(profile);
                }

                menu.open(interaction.getPlayer());
                return true;
            });

            menu.set(6, 0, ItemStacks.SUBTRACT_1, interaction -> {
                Profile profile = PlayerManager.get(p -> p.getUUID().equals(interaction.getPlayer().getUniqueId()));
                intValue.increment(profile);
                menu.open(interaction.getPlayer());
                return true;
            });

            return menu;
        }

    };
}
