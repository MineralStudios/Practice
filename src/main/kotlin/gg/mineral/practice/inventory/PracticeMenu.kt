package gg.mineral.practice.inventory

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.math.MathUtil
import gg.mineral.practice.util.messages.CC
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer
import kotlin.math.max

abstract class PracticeMenu(menu: PracticeMenu? = null) : Menu {
    protected lateinit var viewer: Profile
    protected var openPage: Page? = null
    val pageMap: Int2ObjectOpenHashMap<Page> by lazy { menu?.pageMap ?: Int2ObjectOpenHashMap() }
    override val inventory: Inventory?
        get() = openPage?.inv
    override var closed: Boolean = true

    abstract val title: String
    override val clickCancelled: Boolean
        get() {
            val annotation = javaClass.getAnnotation(ClickCancelled::class.java)
            requireNotNull(annotation) { "ClickCancelled annotation not found on class " + javaClass.simpleName }
            return annotation.value
        }

    override fun setSlot(slot: Int, itemStack: ItemStack) {
        val pageSize = if (pageMap.size > 1) 45 else 54
        val firstPage = slot < pageSize
        val pageNumber = if (firstPage) 0 else slot / pageSize

        val page = pageMap.computeIfAbsent(
            pageNumber,
            Int2ObjectFunction { num -> Page(num) })

        page.setSlot(if (firstPage) slot else if (pageMap.size > 1) slot % 45 else slot % pageSize, itemStack)
    }

    override fun setSlot(slot: Int, itemStack: ItemStack, consumer: Consumer<Interaction>) {
        val pageSize = if (pageMap.size > 1) 45 else 54
        val firstPage = slot < pageSize
        val pageNumber = if (firstPage) 0 else slot / pageSize

        val page = pageMap.computeIfAbsent(
            pageNumber,
            Int2ObjectFunction { num -> Page(num) })

        page.setSlot(if (firstPage) slot else if (pageMap.size > 1) slot % 45 else slot % pageSize, itemStack, consumer)
    }

    fun removeSlot(slot: Int) {
        val pageSize = if (pageMap.size > 1) 45 else 54
        val firstPage = slot < pageSize
        val slotOnPage = if (firstPage) slot else slot % pageSize
        val pageNumber = if (firstPage) 0 else slot / pageSize

        val page = pageMap[pageNumber] ?: return

        page.removeSlot(slotOnPage)
    }

    fun addAfter(afterSlot: Int, item: ItemStack, d: Consumer<Interaction>) {
        findUnusedPage().let { it.setSlot(it.findUnusedSlot(afterSlot), item, d) }
    }


    // TODO: ensure it doesn't go onto another page
    fun addOnNextRow(slot: Int, item: ItemStack, d: Consumer<Interaction>): Int {
        val page = findUnusedPage()
        val slotOnRow = ((page.findLastUsedSlot() / 9) + 1) * 9 + slot
        page.setSlot(slotOnRow, item, d)
        return slotOnRow
    }

    fun addOnNextRow(slot: Int, item: ItemStack): Int {
        val page = findUnusedPage()
        val slotOnRow = ((page.findLastUsedSlot() / 9) + 1) * 9 + slot
        page.setSlot(slotOnRow, item)
        return slotOnRow
    }

    fun addOnRow(rowSlot: Int, slot: Int, item: ItemStack, d: Consumer<Interaction>) {
        val page = findUnusedPage()

        val rowStart = (rowSlot / 9) * 9

        val slotOnRow = rowStart + slot

        page.setSlot(slotOnRow, item, d)
    }

    override fun add(itemStack: ItemStack) {
        findUnusedPage().let { it.setSlot(it.findUnusedSlot(), itemStack) }
    }

    override fun add(itemStack: ItemStack, consumer: Consumer<Interaction>) {
        findUnusedPage().let { it.setSlot(it.findUnusedSlot(), itemStack, consumer) }
    }

    abstract override fun update()

    abstract override fun shouldUpdate(): Boolean

    override fun onClose() {}

    override fun getItemBySlot(slot: Int): ItemStack? {
        val pageSize = if (pageMap.size > 1) 45 else 54
        val firstPage = slot < pageSize
        val slotOnPage = if (firstPage) slot else slot % pageSize
        val pageNumber = if (firstPage) 0 else slot / pageSize

        val page = pageMap[pageNumber] ?: return null

        return page.getItemBySlot(slotOnPage)
    }

    override fun getItemByType(material: Material): ItemStack? {
        for (page in pageMap.values) page.getItemByType(material)?.let { return it }
        return null
    }

    override fun contains(itemStack: ItemStack): Boolean {
        for (page in pageMap.values) if (page.contains(itemStack)) return true
        return false
    }

    private fun findUnusedPage(): Page {
        for (page in pageMap.values) if (!page.full()) return page

        val pageNumber: Int = pageMap.size
        return pageMap.computeIfAbsent(
            pageNumber,
            Int2ObjectFunction { p -> Page(p) })
    }

    protected var needsUpdate: Boolean = true

    open fun open(viewer: Profile, pageNumber: Int) {
        this.closed = false
        this.viewer = viewer

        val hadUpdate = if (needsUpdate || shouldUpdate()) {
            update()
            needsUpdate = false
            true
        } else false

        val page = pageMap.computeIfAbsent(
            pageNumber,
            Int2ObjectFunction { num -> Page(num) })

        page.open(viewer, hadUpdate)

        openPage = page

        viewer.openMenu = this
    }

    override fun open(viewer: Profile) = open(viewer, 0)

    override fun reload() {
        needsUpdate = true
        viewer.let { openPage?.let { openPage -> open(it, openPage.pageNumber) } }
    }

    override fun setContents(contents: Array<ItemStack?>) {
        for (i in contents.indices) contents[i]?.let { setSlot(i, it) }
    }

    override fun getTask(slot: Int) = openPage?.getTask(slot)

    override fun clear() {
        for (page in pageMap.values) page.clear()
    }

    open inner class Page(val pageNumber: Int, page: Page? = null) {
        private val dataMap: Int2ObjectOpenHashMap<Consumer<Interaction>> by lazy {
            page?.dataMap ?: Int2ObjectOpenHashMap()
        }
        val items: Int2ObjectOpenHashMap<ItemStack> by lazy { page?.items ?: Int2ObjectOpenHashMap() }
        private var size = 9
        var inv: Inventory? = null

        init {
            this.size = page?.size ?: 9
        }

        fun getTask(slot: Int): Consumer<Interaction>? = dataMap[slot]

        fun clear() {
            dataMap.clear()
            items.clear()
        }

        fun full(): Boolean {
            val pageSize = if (pageMap.size > 1) 45 else 54
            return size >= pageSize && items.size >= pageSize
        }

        private fun addNavigationBar() {
            for (i in 45..53) {
                when (i) {
                    48 -> setSlot(
                        i, ItemStacks.PREVIOUS_PAGE.lore(
                            CC.WHITE + "Current page:",
                            CC.GOLD + pageNumber,
                            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to go to the previous page."
                        ).build()
                    ) { interaction: Interaction ->
                        if (pageNumber <= 0) return@setSlot
                        this@PracticeMenu.open(interaction.profile, pageNumber - 1)
                    }

                    50 -> setSlot(
                        i, ItemStacks.NEXT_PAGE.lore(
                            CC.WHITE + "Current page:",
                            CC.GOLD + pageNumber,
                            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to go to the next page."
                        ).build()
                    ) { interaction: Interaction ->
                        if (pageNumber >= pageMap.size - 1) return@setSlot
                        this@PracticeMenu.open(interaction.profile, pageNumber + 1)
                    }

                    else -> setSlot(i, ItemStacks.BLACK_STAINED_GLASS)
                }
            }
        }

        private fun toInventory(player: Player): Inventory {
            val inventory = Bukkit.createInventory(
                player, max(MathUtil.roundUp(size, 9), 9),
                title
            )

            for (entry in items.int2ObjectEntrySet()) inventory.setItem(entry.intKey, entry.value)
            return inventory
        }

        fun open(profile: Profile, updated: Boolean) {
            if (pageMap.size > 1) addNavigationBar()
            if (updated || inv == null) inv = toInventory(profile.player)
            profile.player.openInventory(inv)
        }

        fun setSlot(slot: Int, item: ItemStack) {
            if (slot < 0) return
            items.put(slot, item)
            if (slot > size - 1) size = slot + 1
        }

        fun setSlot(slot: Int, item: ItemStack, d: Consumer<Interaction>) {
            dataMap.put(slot, d)
            setSlot(slot, item)
        }

        fun setSlot(slot: Int, item: ItemStack, d: Runnable) = setSlot(slot, item) { _ -> d.run() }

        fun getItemBySlot(slot: Int): ItemStack? = items[slot]

        fun getItemByType(m: Material): ItemStack? {
            for (i in items.values) if (i.type == m) return i
            return null
        }

        fun contains(item: ItemStack) = items.containsValue(item)

        fun findUnusedSlot(start: Int = 0): Int {
            for (i in start..size) if (items[i] == null) return i
            return -1
        }

        fun findLastUsedSlot(): Int {
            for (i in size downTo 0) if (items[i] != null) return i
            return -1
        }

        fun removeSlot(slot: Int) {
            items.remove(slot)
            dataMap.remove(slot)
        }
    }
}
