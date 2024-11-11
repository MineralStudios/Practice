package gg.mineral.practice.inventory;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.math.MathUtil;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

@NoArgsConstructor
public abstract class PracticeMenu implements Menu {
	protected Profile viewer;
	@Getter
	@Nullable
	Page openPage;
	Int2ObjectOpenHashMap<Page> pageMap = new Int2ObjectOpenHashMap<>();
	@Setter
	@Getter
	boolean closed = true;

	public PracticeMenu(PracticeMenu menu) {
		this.pageMap = menu.pageMap;
	}

	public abstract String getTitle();

	@Override
	public boolean isClickCancelled() {
		val annotation = getClass().getAnnotation(ClickCancelled.class);

		if (annotation == null)
			throw new IllegalArgumentException(
					"ClickCancelled annotation not found on class " + getClass().getSimpleName());

		return annotation.value();
	}

	public void setSlot(int slot, ItemStack item) {
		int pageSize = pageMap.size() > 1 ? 45 : 54;
		boolean firstPage = slot < pageSize;
		int slotOnPage = firstPage ? slot : slot % pageSize;
		int pageNumber = firstPage ? 0 : slot / pageSize;

		val page = pageMap.computeIfAbsent(pageNumber, Page::new);

		if (pageMap.size() > 1)
			slotOnPage = slot % 45;

		page.setSlot(slotOnPage, item);
	}

	public void setSlot(int slot, ItemStack item, Consumer<Interaction> d) {
		int pageSize = pageMap.size() > 1 ? 45 : 54;
		boolean firstPage = slot < pageSize;
		int slotOnPage = firstPage ? slot : slot % pageSize;
		int pageNumber = firstPage ? 0 : slot / pageSize;

		val page = pageMap.computeIfAbsent(pageNumber, Page::new);
		if (pageMap.size() > 1)
			slotOnPage = slot % 45;

		page.setSlot(slotOnPage, item, d);
	}

	public void removeSlot(int slot) {
		int pageSize = pageMap.size() > 1 ? 45 : 54;
		boolean firstPage = slot < pageSize;
		int slotOnPage = firstPage ? slot : slot % pageSize;
		int pageNumber = firstPage ? 0 : slot / pageSize;

		val page = pageMap.get(pageNumber);

		if (page == null)
			return;

		page.removeSlot(slotOnPage);
	}

	public void add(ItemStack item) {
		val page = findUnusedPage();
		int slot = page.findUnusedSlot();
		page.setSlot(slot, item);
	}

	public void add(ItemStack item, Consumer<Interaction> d) {
		val page = findUnusedPage();
		int slot = page.findUnusedSlot();
		page.setSlot(slot, item, d);
	}

	public abstract void update();

	public abstract boolean shouldUpdate();

	public void onClose() {
	}

	@Nullable
	public ItemStack getItemBySlot(int slot) {
		int pageSize = pageMap.size() > 1 ? 45 : 54;
		boolean firstPage = slot < pageSize;
		int slotOnPage = firstPage ? slot : slot % pageSize;
		int pageNumber = firstPage ? 0 : slot / pageSize;

		val page = pageMap.get(pageNumber);

		if (page == null)
			return null;

		return page.getItemBySlot(slotOnPage);
	}

	@Nullable
	public ItemStack getItemByType(Material m) {
		for (val page : pageMap.values()) {
			val i = page.getItemByType(m);

			if (i == null)
				continue;

			return i;
		}

		return null;
	}

	public boolean contains(ItemStack item) {
		for (val page : pageMap.values())
			if (page.contains(item))
				return true;

		return false;
	}

	private Page findUnusedPage() {
		for (val page : pageMap.values())
			if (!page.full())
				return page;

		int pageNumber = pageMap.size();
		return pageMap.put(pageNumber, new Page(pageNumber));
	}

	protected boolean needsUpdate = true;

	public void open(Profile viewer, int pageNumber) {
		closed = false;
		this.viewer = viewer;

		boolean hadUpdate = false;

		if (needsUpdate || shouldUpdate()) {
			update();
			needsUpdate = false;
			hadUpdate = true;
		}

		val page = pageMap.computeIfAbsent(pageNumber, Page::new);

		page.open(viewer, hadUpdate);

		openPage = page;

		viewer.setOpenMenu(this);
	}

	public void open(Profile viewer) {
		open(viewer, 0);
	}

	public void reload() {
		needsUpdate = true;
		open(viewer, openPage.pageNumber);
	}

	public void setContents(ItemStack[] contents) {
		for (int i = 0; i < contents.length; i++)
			setSlot(i, contents[i]);
	}

	@Nullable
	public Consumer<Interaction> getTask(int slot) {
		if (openPage == null)
			return null;

		return openPage.getTask(slot);
	}

	public void clear() {
		for (val page : pageMap.values())
			page.clear();
	}

	@RequiredArgsConstructor
	public class Page {
		Int2ObjectOpenHashMap<Consumer<Interaction>> dataMap = new Int2ObjectOpenHashMap<>();
		Int2ObjectOpenHashMap<ItemStack> items = new Int2ObjectOpenHashMap<>();
		private int size = 9;
		private final int pageNumber;

		public Page(Page page) {
			this.dataMap = page.dataMap;
			this.items = page.items;
			this.size = page.size;
			this.pageNumber = page.pageNumber;
		}

		@Nullable
		public Consumer<Interaction> getTask(int i) {
			return dataMap.get(i);
		}

		public void clear() {
			dataMap.clear();
			items.clear();
		}

		public boolean full() {
			int pageSize = pageMap.size() > 1 ? 45 : 54;
			return size >= pageSize && items.size() >= pageSize;
		}

		public void addNavigationBar() {
			for (int i = 45; i <= 53; i++) {
				switch (i) {
					case 48:
						setSlot(i, ItemStacks.PREVIOUS_PAGE.lore(
								CC.WHITE + "Current page:",
								CC.GOLD + pageNumber,
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to go to the previous page.").build(),
								interaction -> {
									val p = interaction.getProfile();
									if (pageNumber <= 0)
										return;

									PracticeMenu.this.open(p, pageNumber - 1);
								});
						break;
					case 50:
						setSlot(i, ItemStacks.NEXT_PAGE.lore(
								CC.WHITE + "Current page:",
								CC.GOLD + pageNumber,
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to go to the next page.").build(),
								interaction -> {
									val p = interaction.getProfile();
									if (pageNumber >= pageMap.size() - 1)
										return;

									PracticeMenu.this.open(p, pageNumber + 1);
								});
						break;
					default:
						setSlot(i, ItemStacks.BLACK_STAINED_GLASS);

				}
			}
		}

		protected Inventory toInventory(Player player) {
			val inventory = Bukkit.createInventory(player, Math.max(MathUtil.roundUp(size, 9), 9),
					getTitle());

			for (val e : items.int2ObjectEntrySet())
				inventory.setItem(e.getIntKey(), e.getValue());

			return inventory;
		}

		@Nullable
		protected Inventory inv;

		public void open(Profile profile, boolean updated) {
			if (pageMap.size() > 1)
				addNavigationBar();

			if (updated || inv == null)
				inv = toInventory(profile.getPlayer());
			profile.getPlayer().openInventory(inv);
		}

		public void setSlot(int slot, ItemStack item) {

			if (item == null || slot < 0)
				return;

			items.put(slot, item);

			if (slot > size - 1)
				size = slot + 1;
		}

		public void setSlot(int slot, ItemStack item, Consumer<Interaction> d) {
			dataMap.put(slot, d);
			setSlot(slot, item);
		}

		public void setSlot(int slot, ItemStack item, Runnable d) {
			setSlot(slot, item, p -> d.run());
		}

		@Nullable
		public ItemStack getItemBySlot(int slot) {
			return items.get(slot);
		}

		@Nullable
		public ItemStack getItemByType(Material m) {
			for (val i : items.values())
				if (i.getType() == m)
					return i;

			return null;
		}

		public boolean contains(ItemStack item) {
			return items.containsValue(item);
		}

		public int findUnusedSlot() {
			for (int i = 0; i <= size; i++)
				if (items.get(i) == null)
					return i;

			return -1;
		}

		public void removeSlot(int slot) {
			items.remove(slot);
			dataMap.remove(slot);
		}
	}

	@Override
	public Inventory getInventory() {
		return openPage == null ? null : openPage.inv;
	}
}
