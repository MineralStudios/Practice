package gg.mineral.practice.inventory;

import java.util.Map;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.math.MathUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class PracticeMenu {
	@Setter
	@Getter
	String title;
	@Setter
	@Getter
	Boolean clickCancelled = false;
	protected Profile viewer;
	Page openPage;
	Int2ObjectOpenHashMap<Page> pageMap = new Int2ObjectOpenHashMap<>();

	public PracticeMenu(String title) {
		this.title = title;
	}

	public PracticeMenu(PracticeMenu menu) {
		this.title = menu.getTitle();
		this.clickCancelled = menu.getClickCancelled();
		this.pageMap = menu.pageMap;
	}

	public void setSlot(int slot, ItemStack item) {
		boolean firstPage = slot < 45;
		int slotOnPage = firstPage ? slot : slot % 45;
		int pageNumber = firstPage ? 0 : slot / 45;

		Page page = pageMap.get(pageNumber);

		if (page == null) {
			pageMap.put(pageNumber, page = new Page(pageNumber));
		}

		page.setSlot(slotOnPage, item);
	}

	public void setSlot(int slot, ItemStack item, Predicate<Profile> d) {
		boolean firstPage = slot < 45;
		int slotOnPage = firstPage ? slot : slot % 45;
		int pageNumber = firstPage ? 0 : slot / 45;

		Page page = pageMap.get(pageNumber);

		if (page == null) {
			pageMap.put(pageNumber, page = new Page(pageNumber));
		}

		page.setSlot(slotOnPage, item, d);
	}

	public void setSlot(int slot, ItemStack item, Runnable d) {
		setSlot(slot, item, p -> {
			d.run();
			return true;
		});
	}

	public void add(ItemStack item) {
		Page page = findUnusedPage();
		page.add(item);
	}

	public void add(ItemStack item, Predicate<Profile> d) {
		Page page = findUnusedPage();
		page.add(item, d);
	}

	public void add(ItemStack item, Runnable d) {
		add(item, p -> {
			d.run();
			return true;
		});
	}

	public boolean update() {
		return false;
	}

	public ItemStack getItemBySlot(int slot) {
		boolean firstPage = slot < 45;
		int slotOnPage = firstPage ? slot : slot % 45;
		int pageNumber = firstPage ? 0 : slot / 45;

		Page page = pageMap.get(pageNumber);

		if (page == null) {
			return null;
		}

		return page.getItemBySlot(slotOnPage);
	}

	public ItemStack getItemByType(Material m) {
		for (Page page : pageMap.values()) {
			ItemStack i = page.getItemByType(m);

			if (i == null) {
				continue;
			}

			return i;
		}

		return null;
	}

	public boolean contains(ItemStack item) {
		for (Page page : pageMap.values()) {
			if (page.contains(item)) {
				return true;
			}
		}

		return false;
	}

	private Page findUnusedPage() {
		for (Page page : pageMap.values()) {
			if (page.full()) {
				continue;
			}

			return page;
		}

		int pageNumber = pageMap.size();
		Page page = new Page(pageNumber);
		pageMap.put(pageNumber, page);
		return page;
	}

	protected Inventory inv;
	protected boolean needsUpdate = true;

	public void open(Profile viewer, int pageNumber) {
		this.viewer = viewer;

		if (needsUpdate) {
			needsUpdate = update();
		}

		Page page = pageMap.get(pageNumber);

		if (page == null) {
			pageMap.put(pageNumber, page = new Page(pageNumber));
		}

		page.open(viewer, needsUpdate);

		openPage = page;

		viewer.setOpenMenu(this);
	}

	public void open(Profile viewer) {
		open(viewer, 0);
	}

	public void setContents(ItemStack[] contents) {
		for (int i = 0; i < contents.length; i++) {
			setSlot(i, contents[i]);
		}
	}

	public Predicate<Profile> getTask(int slot) {
		if (openPage == null) {
			return null;
		}

		return openPage.getTask(slot);
	}

	public void clear() {
		for (Page page : pageMap.values()) {
			page.clear();
		}
	}

	public class Page {
		Int2ObjectOpenHashMap<Predicate<Profile>> dataMap = new Int2ObjectOpenHashMap<>();
		Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();
		int size = 9, pageNumber;

		public Page(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public Page(Page page) {
			this.dataMap = page.dataMap;
			this.items = page.items;
			this.size = page.size;
			this.pageNumber = page.pageNumber;
		}

		public Predicate<Profile> getTask(int i) {
			return dataMap.get(i);
		}

		public void clear() {
			dataMap.clear();
			items.clear();
		}

		public boolean full() {
			return size >= 45 && items.size() >= 45;
		}

		public void addNavigationBar() {
			for (int i = 45; i <= 53; i++) {
				switch (i) {
					case 48:
						setSlot(i, ItemStacks.PREVIOUS_PAGE, p -> {
							PracticeMenu.this.open(p, Math.max(0, pageNumber - 1));
							return true;
						});
						break;
					case 50:
						setSlot(i, ItemStacks.NEXT_PAGE, p -> {
							PracticeMenu.this.open(p, Math.min(pageNumber + 1, pageMap.size() - 1));
							return true;
						});
						break;
					default:
						setSlot(i, ItemStacks.BLACK_STAINED_GLASS);

				}
			}
		}

		protected Inventory toInventory(Player player) {
			Inventory inventory = Bukkit.createInventory(player, Math.max(MathUtil.roundUp(size, 9), 9),
					title.toString());

			for (Map.Entry<Integer, ItemStack> e : items.entrySet()) {
				inventory.setItem(e.getKey(), e.getValue());
			}

			return inventory;
		}

		protected Inventory inv;
		protected boolean needsUpdate = true;

		public void open(Profile profile, boolean updated) {

			if (pageMap.size() > 1) {
				addNavigationBar();
			}

			if (updated || inv == null) {
				inv = toInventory(profile.getPlayer());
			}

			profile.getPlayer().openInventory(inv);
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
			setSlot(slot, item, p -> {
				d.run();
				return true;
			});
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
			add(item, p -> {
				d.run();
				return true;
			});
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
			for (int i = 0; i <= size; i++) {
				if (items.get(i) == null) {
					return i;
				}
			}

			return -1;
		}

	}
}
