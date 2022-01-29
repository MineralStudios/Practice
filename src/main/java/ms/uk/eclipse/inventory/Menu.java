package ms.uk.eclipse.inventory;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import ms.uk.eclipse.core.utils.message.Message;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.util.MathUtil;

public class Menu {
	Message title;
	boolean clickCancelled = false;
	protected Profile viewer;
	Int2ObjectOpenHashMap<Object> dataMap = new Int2ObjectOpenHashMap<>();
	int size = 9;
	protected Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();

	public Menu(Message title) {
		this.title = title;
	}

	public Menu(Menu menu) {
		this.title = menu.getTitle();
		this.clickCancelled = menu.getClickCancelled();
		this.dataMap = menu.getDataMap();
		this.items = menu.getItems();
		this.size = menu.getSize();
	}

	private int getSize() {
		return size;
	}

	private Int2ObjectOpenHashMap<Object> getDataMap() {
		return dataMap;
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

	public void setSlot(int slot, ItemStack item, Object d) {
		dataMap.put(slot, d);
		setSlot(slot, item);
	}

	public void add(ItemStack item) {
		setSlot(findUnusedSlot(), item);
	}

	public void add(ItemStack item, Object d) {
		int slot = findUnusedSlot();
		dataMap.put(slot, d);
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

	public Message getTitle() {
		return title;
	}

	private Inventory toInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(player, Math.max(MathUtil.roundUp(size, 9), 9), title.toString());

		for (Map.Entry<Integer, ItemStack> e : items.entrySet()) {
			inventory.setItem(e.getKey(), e.getValue());
		}

		return inventory;
	}

	Inventory inv;

	public void open(Profile player) {
		viewer = player;
		boolean updated = update() || inv == null;

		if (updated) {
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

	public Object getTask(int i) {
		return dataMap.get(i);
	}

	public void setTitle(Message title) {
		this.title = title;
	}

	public void clear() {
		items.clear();
	}
}
