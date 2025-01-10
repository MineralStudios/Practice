package gg.mineral.practice.gametype;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.category.Category;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.entity.ProfileData;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.*;
import gg.mineral.practice.queue.QueuetypeMenuEntry;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.practice.util.collection.LeaderboardMap;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Gametype implements SaveableData, QueuetypeMenuEntry {
    final FileConfiguration config = GametypeManager.getConfig();

    @Getter
    boolean regeneration, deadlyWater, griefing, build, looting, damage, hunger, boxing, event, botsEnabled;
    @Setter
    @Getter
    boolean inCategory;
    @Getter
    ItemStack displayItem;
    @Getter
    String displayName, categoryName;
    @Getter
    final String name;
    @Getter
    int noDamageTicks, pearlCooldown, buildLimit;
    @Getter
    ByteSet arenas = new ByteOpenHashSet();
    @Getter
    byte eventArenaId;
    @Getter
    Kit kit;
    String path;
    @Setter
    @Getter
    LeaderboardMap leaderboardMap = new LeaderboardMap();
    Object2IntOpenHashMap<ProfileData> eloMap = new Object2IntOpenHashMap<>();
    @Getter
    private final byte id;

    public Gametype(String name, byte id) {
        this.name = name;
        this.id = id;
        this.path = "Gametype." + getName() + ".";
    }

    public int getElo(ProfileData profile) {
        val elo = eloMap.getInt(profile);

        if (profile.getUuid() == null)
            return elo == 0
                    ? EloManager.get(this, profile.getName())
                    .whenComplete((value, ex) -> eloMap.put(profile, (int) value))
                    .join()
                    : elo;

        return elo == 0
                ? EloManager.get(this, profile.getUuid()).whenComplete((value, ex) -> eloMap.put(profile, (int) value))
                .join()
                : elo;
    }

    public CompletableFuture<Object2IntOpenHashMap<UUID>> getEloMap(ProfileData... profiles) {
        return CompletableFuture.supplyAsync(() -> {
            val map = new Object2IntOpenHashMap<UUID>();

            Map<UUID, CompletableFuture<Integer>> futures = null;

            for (val profile : profiles) {
                val elo = eloMap.getInt(profile);

                if (elo != 0)
                    map.put(profile.getUuid(), elo);

                if (futures == null)
                    futures = new Object2ObjectOpenHashMap<>();

                futures.put(profile.getUuid(), EloManager.get(this, profile.getUuid())
                        .whenComplete((value, ex) -> eloMap.put(profile, (int) value)));
            }

            if (futures != null)
                for (val entry : futures.entrySet())
                    map.put(entry.getKey(), (int) entry.getValue().join());

            return map;
        });
    }

    public Object2IntOpenHashMap<ProfileData> getEloCache() {
        return eloMap;
    }

    public void setElo(int elo, ProfileData profile) {
        this.eloMap.put(profile, elo);

        EloManager.update(profile, getName(), elo);
    }

    public void setRegeneration(boolean regeneration) {
        this.regeneration = regeneration;
        save();
    }

    public void addToCategory(Category c) {
        this.inCategory = true;
        this.categoryName = c.getName();
        c.addGametype(this);
        save();
    }

    public void removeFromCategory(Category c) {
        this.inCategory = false;
        c.removeGametype(this);
        save();
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        save();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        save();
    }

    public void setNoDamageTicks(int i) {
        this.noDamageTicks = i;
        save();
    }

    public void setDeadlyWater(boolean deadlyWater) {
        this.deadlyWater = deadlyWater;
        save();
    }

    public void setGriefing(boolean griefing) {
        this.griefing = griefing;
        save();
    }

    public void setBuild(boolean build) {
        this.build = build;
        save();
    }

    public void setLooting(boolean looting) {
        this.looting = looting;
        save();
    }

    public void setDamage(boolean damage) {
        this.damage = damage;
        save();
    }

    public void setHunger(boolean hunger) {
        this.hunger = hunger;
        save();
    }

    public void setBoxing(boolean boxing) {
        this.boxing = boxing;
        save();
    }

    public void setBotsEnabled(boolean bots) {
        this.botsEnabled = bots;
        save();
    }

    public void setBuildLimit(int buildLimit) {
        this.buildLimit = buildLimit;
        save();
    }

    public void setPearlCooldown(int pearlCooldown) {
        this.pearlCooldown = pearlCooldown;
        save();
    }

    public void enableArena(Arena arena, boolean enabled) {
        if (enabled)
            arenas.add(arena.getId());
        else
            arenas.remove(arena.getId());

        save();
    }

    public void setEventArena(byte arenaId) {
        this.eventArenaId = arenaId;
        save();
    }

    public void setEvent(boolean b) {
        event = b;
        save();
    }

    public void setKit(Kit kit) {
        this.kit = kit;
        save();
    }

    public boolean equals(Gametype gametype) {
        return gametype.getName().equalsIgnoreCase(getName());
    }

    public void updatePlayerLeaderboard(Profile p, int elo, int oldElo) {
        if (oldElo == 1000) {
            leaderboardMap.putOrReplace(p.getName(), elo, oldElo);
            return;
        }

        leaderboardMap.replace(p.getName(), elo, oldElo);
    }

    public List<String> getLeaderboardLore() {
        val lore = new GlueList<String>();

        for (val entry : leaderboardMap.getEntries())
            lore.add(CC.SECONDARY + entry.getKey() + ": " + CC.WHITE + entry.getValue());

        if (lore.isEmpty())
            lore.add(CC.ACCENT + "No Data");

        return lore;
    }

    @Override
    public void save() {
        config.set(path + "Regen", regeneration);
        config.set(path + "Event", event);
        config.set(path + "Bots", botsEnabled);
        val eventArena = ArenaManager.getArenas().get(eventArenaId);
        if (eventArena != null)
            config.set(path + "EventArena", eventArena.getName());

        config.set(path + "PearlCooldown", pearlCooldown);
        config.set(path + "Hunger", hunger);
        config.set(path + "Boxing", boxing);
        config.set(path + "Damage", damage);
        config.set(path + "Looting", looting);
        config.set(path + "Build", build);
        config.set(path + "Griefing", griefing);
        config.set(path + "DeadlyWater", deadlyWater);
        config.set(path + "NoDamageTicks", noDamageTicks);
        config.set(path + "BuildLimit", buildLimit);
        config.set(path + "DisplayName", displayName);
        config.set(path + "DisplayItem", displayItem);
        config.set(path + "InCategory", inCategory);

        if (inCategory)
            config.set(path + "Category", categoryName);

        for (val q : QueuetypeManager.getQueuetypes().values()) {
            val containsGametype = q.getMenuEntries().containsKey(this);
            config.set(path + q.getName() + ".Enabled", containsGametype);

            if (containsGametype) {
                config.set(path + q.getName() + ".Slot", q.getMenuEntries().getInt(this));

                for (byte arenaId : q.getArenas()) {
                    Arena arena = ArenaManager.getArenas().get(arenaId);
                    config.set(path + "Arenas." + arena.getName(), getArenas().intStream()
                            .filter(id -> ArenaManager.getArenas().get((byte) id).getName()
                                    .equalsIgnoreCase(arena.getName()))
                            .findFirst().isPresent());
                }
            }
        }

        val contents = kit.getContents();
        val armourContents = kit.getArmourContents();

        for (int i = 0; i < contents.length; i++) {
            val item = contents[i];

            if (item == null) {
                config.set(path + "Kit.Contents." + i, "empty");
                continue;
            }

            config.set(path + "Kit.Contents." + i, item);

        }

        for (int x = 0; x < armourContents.length; x++) {
            val armour = armourContents[x];

            if (armour == null) {
                config.set(path + "Kit.Armour." + x, "empty");
                continue;
            }

            config.set(path + "Kit.Armour." + x, armour);
        }
        config.save();
    }

    @Override
    public void load() {
        this.regeneration = config.getBoolean(path + "Regen", true);
        this.displayItem = config.getItemstack(path + "DisplayItem", ItemStacks.DEFAULT_GAMETYPE_DISPLAY_ITEM);
        this.displayName = config.getString(path + "DisplayName", getName());
        this.noDamageTicks = config.getInt(path + "NoDamageTicks", 20);
        this.buildLimit = config.getInt(path + "BuildLimit", 16);
        this.deadlyWater = config.getBoolean(path + "DeadlyWater", false);
        this.griefing = config.getBoolean(path + "Griefing", false);
        this.build = config.getBoolean(path + "Build", false);
        this.looting = config.getBoolean(path + "Looting", false);
        this.damage = config.getBoolean(path + "Damage", true);
        this.hunger = config.getBoolean(path + "Hunger", true);
        this.boxing = config.getBoolean(path + "Boxing", false);
        this.inCategory = config.getBoolean(path + "InCategory", false);
        this.event = config.getBoolean(path + "Event", false);
        this.botsEnabled = config.getBoolean(path + "Bots", false);
        Arena eventArena = ArenaManager.getArenaByName(config.getString(path + "EventArena", ""));
        this.eventArenaId = eventArena == null ? -1 : eventArena.getId();

        if (inCategory) {
            this.categoryName = config.getString(path + "Category", null);

            if (categoryName != null) {
                val category = CategoryManager.getCategoryByName(categoryName);

                if (category != null)
                    category.addGametype(this);
            }
        }

        this.pearlCooldown = config.getInt(path + "PearlCooldown", 10);

        for (val q : QueuetypeManager.getQueuetypes().values()) {
            if (!config.getBoolean(path + q.getName() + ".Enabled", false))
                continue;

            q.addMenuEntry(this, config.getInt(path + q.getName() + ".Slot", 0));
        }

        for (val a : ArenaManager.getArenas().values())
            if (config.getBoolean(path + "Arenas." + a.getName(), false))
                arenas.add(a.getId());

        var cs = config.getConfigurationSection(path + "Kit.Armour");

        val armour = new GlueList<ItemStack>();

        if (cs != null) {
            for (val key : cs.getKeys(false)) {
                val o = cs.get(key);
                if (o instanceof ItemStack)
                    armour.add((ItemStack) o);
                else
                    armour.add(ItemStacks.AIR);
            }
        }

        cs = config.getConfigurationSection(path + "Kit.Contents");

        val items = new GlueList<ItemStack>();

        if (cs != null) {
            for (val key : cs.getKeys(false)) {
                val o = cs.get(key);
                if (o instanceof ItemStack)
                    items.add((ItemStack) o);
                else
                    items.add(ItemStacks.AIR);
            }
        }

        this.kit = new Kit(this.getName(), items.toArray(new ItemStack[0]), armour.toArray(new ItemStack[0]));
    }

    @Override
    public void setDefaults() {
        this.regeneration = true;
        this.displayItem = ItemStacks.DEFAULT_GAMETYPE_DISPLAY_ITEM;
        this.displayName = getName();
        this.noDamageTicks = 20;
        this.buildLimit = 16;
        this.deadlyWater = false;
        this.griefing = false;
        this.build = false;
        this.looting = false;
        this.damage = true;
        this.hunger = true;
        this.boxing = false;
        this.inCategory = false;
        this.event = false;
        this.botsEnabled = false;
        this.eventArenaId = 0;
        this.pearlCooldown = 10;

        this.kit = new Kit(this.getName(), new ItemStack[0], new ItemStack[0]);
        this.leaderboardMap = new LeaderboardMap();
    }

    @Override
    public void delete() {
        config.remove("Gametype." + getName());
        config.save();
    }
}
