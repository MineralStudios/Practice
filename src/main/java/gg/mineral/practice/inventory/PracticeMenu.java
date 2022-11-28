package gg.mineral.practice.inventory;

import java.util.Map;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.MathUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class PracticeMenu {
	String title;
	boolean clickCancelled = false;
	protected Profile viewer;
	Int2ObjectOpenHashMap<Predicate<Profile>> dataMap = new Int2ObjectOpenHashMap<>();
	int size = 9;
	protected Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();

	public PracticeMenu(String title) {
		this.title = title;
	}

	public PracticeMenu(PracticeMenu menu) {
		this.title = menu.getTitle();
		this.clickCancelled = menu.getClickCancelled();
		this.dataMap = menu.dataMap;
		this.items = menu.getItems();
		this.size = menu.size;
	}

	public void setSlot(int slot, ItemStack item) {

		if (item == null || slot < 0) {
			return;
		}

		items.put(slot, item);

		if (slot > size - 1) {
			size = slot + 1;
		}
	}

	public void setSlot(int slot, ItemStack item, Predicate<Profile> d) {
		dataMap.put(slot, d);
		setSlot(slot, item);
	}

	public void setSlot(int slot, ItemStack item, Runnable d) {
		dataMap.put(slot, p -> {
			d.run();
			return true;
		});
		setSlot(slot, item);
	}

	public void add(ItemStack item) {
		setSlot(findUnusedSlot(), item);
	}

	public void add(ItemStack item, Predicate<Profile> d) {
		int slot = findUnusedSlot();
		dataMap.put(slot, d);
		setSlot(slot, item);
	}

	public void add(ItemStack item, Runnable d) {
		int slot = findUnusedSlot();
		dataMap.put(slot, p -> {
			d.run();
			return true;
		});
		setSlot(slot, item);
	}

	public boolean update() {
		return false;
	}

	public Int2ObjectOpenHashMap<ItemStack> getItems() {
		return items;
	}

	public ItemStack getItemBySlot(int slot) {
		return items.get(slot);
	}

	public ItemStack getItemByType(Material m) {
		for (ItemStack i : items.values()) {
			if (i.getType() == m) {
				return i;
			}
		}
		return null;
	}

	public boolean contains(ItemStack item) {
		return items.containsValue(item);
	}

	private Integer findUnusedSlot() {
		for (int i = 0; i <= items.keySet().size(); i++) {
			if (items.get(i) == null) {
				return i;
			}
		}

		return -1;
	}

	public void setClickCancelled(boolean clickCancelled) {
		this.clickCancelled = clickCancelled;
	}

	public boolean getClickCancelled() {
		return clickCancelled;
	}

	public String getTitle() {
		return title;
	}

	protected Inventory toInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(player, Math.max(MathUtil.roundUp(size, 9), 9), title.toString());

		for (Map.Entry<Integer, ItemStack> e : items.entrySet()) {
			inventory.setItem(e.getKey(), e.getValue());
		}

		return inventory;
	}

	protected Inventory inv;
	protected boolean needsUpdate = true;

	public void open(Profile player) {
		viewer = player;

		if (needsUpdate) {
			needsUpdate = update();
		}

		if (needsUpdate || inv == null) {
			inv = toInventory(player.bukkit());
		}

		player.bukkit().openInventory(inv);
		player.setOpenMenu(this);
	}

	public void setContents(ItemStack[] contents) {
		for (int i = 0; i < contents.length; i++) {
			setSlot(i, contents[i]);
		}
	}

	public Predicate<Profile> getTask(int i) {
		return dataMap.get(i);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void clear() {
		items.clear();
	}
}
