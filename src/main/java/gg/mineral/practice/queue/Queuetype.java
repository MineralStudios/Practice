package gg.mineral.practice.queue;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.ProfileData;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.EloManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.practice.util.collection.LeaderboardMap;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import lombok.Getter;

public class Queuetype implements SaveableData {
	final FileConfiguration config = QueuetypeManager.getConfig();

	@Getter
	ItemStack displayItem;
	@Getter
	String name;
	@Getter
	String displayName;
	@Getter
	int slotNumber;
	@Getter
	boolean ranked, community, unranked, botsEnabled;
	final String path;
	@Getter
	KnockbackProfile knockback = null;
	@Getter
	Object2IntOpenHashMap<Gametype> gametypes = new Object2IntOpenHashMap<>();
	@Getter
	ByteSet arenas = new ByteOpenHashSet();
	@Getter
	Object2IntOpenHashMap<Catagory> catagories = new Object2IntOpenHashMap<>();
	@Getter
	private final byte id;
	private long arenaIndex = 0;

	public Queuetype(String name, byte id) {
		this.name = name;
		this.id = id;
		this.path = "Queue." + getName() + ".";
	}

	ByteSet arenaQueue = new ByteOpenHashSet();

	public Gametype randomGametype() {
		List<Gametype> list = new GlueList<>(gametypes.keySet());
		Random rand = new Random();
		return list.get(rand.nextInt(list.size()));
	}

	public Gametype randomGametypeWithBotsEnabled() {
		List<Gametype> list = new GlueList<>(gametypes.keySet()).stream().filter(g -> g.isBotsEnabled())
				.collect(Collectors.toList());
		Random rand = new Random();
		return list.get(rand.nextInt(list.size()));
	}

	// Helper method to filter available arenas based on a Gametype
	public ByteSet filterArenasByGametype(Gametype g) {
		ByteSet filteredArenas = new ByteOpenHashSet(this.arenas);
		filteredArenas
				.retainAll(g.getArenas());
		return filteredArenas;
	}

	public int getGlobalElo(ProfileData profile) {

		ObjectSet<Gametype> set = getGametypes().keySet();

		int sum = 0;

		for (Gametype gametype : set)
			sum += gametype.getElo(profile);

		return Math.round(sum / set.size());
	}

	public List<String> getGlobalLeaderboardLore() {
		List<String> lore = new GlueList<>();

		LeaderboardMap leaderboardMap = EloManager.getGlobalEloLeaderboard(this);

		lore.add(CC.WHITE + "The " + CC.SECONDARY + "global" + CC.WHITE + " elo leaderboard.");
		lore.add(" ");

		for (gg.mineral.practice.util.collection.LeaderboardMap.Entry entry : leaderboardMap.getEntries())
			lore.add(CC.SECONDARY + entry.getKey() + ": " + CC.WHITE + entry.getValue());

		if (lore.size() <= 2)
			lore.add(CC.ACCENT + "No Data");

		return lore;
	}

	@Nullable
	public byte nextArenaId(Gametype g) {
		// Return early if there are no arenas
		if (arenas.isEmpty() || g.getArenas().isEmpty())
			return -1;

		arenaQueue.removeIf(arenaId -> !g.getArenas().contains(arenaId));

		if (arenaQueue.isEmpty())
			arenaQueue.addAll(filterArenasByGametype(g));

		if (arenaQueue.isEmpty())
			return -1;

		ByteIterator iterator = arenaQueue.iterator();

		byte nextArenaId = iterator.nextByte();
		iterator.remove();

		return nextArenaId;
	}

	public byte nextArenaId(MatchData matchData, Gametype g) {

		// If there are no enabled arenas in the MatchData, revert to the other method
		if (matchData.getEnabledArenas().isEmpty())
			return nextArenaId(g);

		// Filter arenas based on Gametype and MatchData
		ByteSet filteredArenas = filterArenasByGametype(g);

		ByteIterator iterator = filteredArenas.iterator();
		while (iterator.hasNext()) {
			byte arenaId = iterator.nextByte();
			if (!matchData.getEnabledArenas().get(arenaId))
				iterator.remove();
		}

		if (filteredArenas.isEmpty())
			return nextArenaId(g);

		// Select a random arena from the filtered list
		int randomIndex = (int) (arenaIndex++ % filteredArenas.size());
		byte selectedArenaId = filteredArenas.toArray(new byte[0])[randomIndex];

		// Remove the selected arena from the main queue to avoid repetition
		arenaQueue.remove(selectedArenaId);

		return selectedArenaId;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		save();
	}

	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
		save();
	}

	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
		save();
	}

	public void setRanked(boolean ranked) {
		this.ranked = ranked;
		save();
	}

	public void setBotsEnabled(boolean bots) {
		this.botsEnabled = bots;
		save();
	}

	public void setCommunity(boolean community) {
		this.community = community;
		save();
	}

	public void setUnranked(boolean unranked) {
		this.unranked = unranked;
		save();
	}

	public void setKnockback(KnockbackProfile knockback) {
		this.knockback = knockback;
		save();
	}

	public void enableArena(Arena arena, boolean enabled) {
		if (enabled) {
			arenas.add(arena.getId());
		} else {
			arenas.remove(arena.getId());
			arenaQueue.remove(arena.getId());
		}
		save();
	}

	public boolean equals(Queuetype q) {
		return q.getName().equalsIgnoreCase(getName());
	}

	public void addCatagory(Catagory catagory, int slot) {
		catagories.put(catagory, slot);
		save();
	}

	public void addGametype(Gametype gametype, int slot) {
		gametypes.put(gametype, slot);
		save();
	}

	@Override
	public void save() {
		config.set(path + "DisplayName", displayName);
		config.set(path + "Elo", ranked);
		config.set(path + "Community", community);
		config.set(path + "Unranked", unranked);
		config.set(path + "Slot", slotNumber);
		config.set(path + "DisplayItem", displayItem);
		config.set(path + "Bots", botsEnabled);

		if (knockback != null)
			config.set(path + "Knockback", knockback.getName());

		for (Arena arena : ArenaManager.getArenas().values())
			config.set(path + "Arenas." + arena.getName(), getArenas().intStream()
					.filter(id -> id == arena.getId()).findFirst().isPresent());

		config.save();
	}

	@Override
	public void load() {
		this.displayItem = config.getItemstack(path + "DisplayItem",
				ItemStacks.DEFAULT_QUEUETYPE_DISPLAY_ITEM);
		this.displayName = config.getString(path + "DisplayName", getName());
		this.slotNumber = config.getInt(path + "Slot", 8);
		this.ranked = config.getBoolean(path + "Elo", false);
		this.community = config.getBoolean(path + "Community", false);
		this.unranked = config.getBoolean(path + "Unranked", false);
		this.botsEnabled = config.getBoolean(path + "Bots", false);
		String kbprofile = config.getString(path + "Knockback", "");

		if (!kbprofile.isEmpty()) {
			this.knockback = KnockbackProfileList.getKnockbackProfileByName(kbprofile);
		}

		for (Arena a : ArenaManager.getArenas().values())
			if (config.getBoolean(path + "Arenas." + a.getName(), false))
				arenas.add(a.getId());

	}

	@Override
	public void setDefaults() {
		this.displayItem = ItemStacks.DEFAULT_QUEUETYPE_DISPLAY_ITEM;
		this.displayName = getName();
		this.slotNumber = 8;
		this.ranked = false;
		this.botsEnabled = false;
	}

	@Override
	public void delete() {
		config.remove("Queue." + getName());
		config.save();
	}
}
