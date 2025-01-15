package gg.mineral.practice.inventory

import gg.mineral.practice.entity.Profile
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

abstract class AnvilMenu : Menu {
    private val dataMap: Int2ObjectOpenHashMap<Consumer<Interaction>> = Int2ObjectOpenHashMap()
    val items: Int2ObjectOpenHashMap<ItemStack> = Int2ObjectOpenHashMap()
    override var closed: Boolean = true
    override val clickCancelled: Boolean
        get() {
            val annotation = javaClass.getAnnotation(ClickCancelled::class.java)
            requireNotNull(annotation) { "ClickCancelled annotation not found on class " + javaClass.simpleName }
            return annotation.value
        }
    protected var viewer: Profile? = null
    var text: String? = null
    private var containerId = 0
    override var inventory: Inventory? = null
        set(value) {
            for (entry in items.int2ObjectEntrySet()) value?.setItem(entry.intKey, entry.value)
            field = value
        }

    fun closeInventory() {
        viewer?.let {
            handleInventoryCloseEvent(it.player)
            setActiveContainerDefault(it.player)
            sendPacketCloseWindow(it.player, containerId)
        }
    }

    override fun shouldUpdate() = true

    private fun getNextContainerId(player: Player) = toNMS(player).nextContainerCounter()

    private fun handleInventoryCloseEvent(player: Player) = CraftEventFactory.handleInventoryCloseEvent(toNMS(player))

    private fun sendPacketOpenWindow(player: Player, containerId: Int) {
        toNMS(player).playerConnection
            .sendPacket(
                PacketPlayOutOpenWindow(
                    containerId, "minecraft:anvil", ChatMessage(Blocks.ANVIL.a() + ".name")
                )
            )
    }

    private fun sendPacketCloseWindow(player: Player, containerId: Int) =
        toNMS(player).playerConnection.sendPacket(PacketPlayOutCloseWindow(containerId))

    private fun setActiveContainerDefault(player: Player) {
        toNMS(player).activeContainer = toNMS(player).defaultContainer
    }

    private fun setActiveContainer(player: Player, container: Container?) {
        toNMS(player).activeContainer = container
    }

    private fun setActiveContainerId(container: Container, containerId: Int) {
        container.windowId = containerId
    }

    private fun addActiveContainerSlotListener(container: Container, player: Player) =
        container.addSlotListener(toNMS(player))

    private fun toBukkitInventory(container: Container) = container.bukkitView.topInventory

    private fun newContainerAnvil(player: Player) = AnvilContainer(toNMS(player))

    private fun toNMS(player: Player) = (player as CraftPlayer).handle

    private inner class AnvilContainer(entityhuman: EntityHuman) :
        ContainerAnvil(entityhuman.inventory, entityhuman.world, BlockPosition(0, 0, 0), entityhuman) {
        override fun a(human: EntityHuman) = true

        override fun b(entityhuman: EntityHuman) {}

        override fun e() {}

        override fun a(string: String) {
            text = string
        }

        override fun a(iinventory: IInventory) {}

        override fun addSlotListener(icrafting: ICrafting) {}
    }

    override fun setSlot(slot: Int, itemStack: ItemStack) {
        if (slot < 0) return
        items.put(slot, itemStack)
    }

    override fun setSlot(slot: Int, itemStack: ItemStack, consumer: Consumer<Interaction>) {
        dataMap.put(slot, consumer)
        setSlot(slot, itemStack)
    }

    override fun add(itemStack: ItemStack) = setSlot(findUnusedSlot(), itemStack)

    private fun findUnusedSlot(): Int {
        for (index in 0..3) if (items[index] == null) return index
        return -1
    }

    override fun add(itemStack: ItemStack, consumer: Consumer<Interaction>) =
        setSlot(findUnusedSlot(), itemStack, consumer)

    abstract override fun update()

    override fun onClose() {}

    override fun getItemBySlot(slot: Int): ItemStack = items[slot]

    override fun getItemByType(material: Material): ItemStack? {
        for (itemStack in items.values) if (itemStack.type == material) return itemStack
        return null
    }

    override fun contains(itemStack: ItemStack) = items.containsValue(itemStack)

    override fun open(viewer: Profile) {
        this.closed = false
        this.viewer = viewer

        this.update()

        this.handleInventoryCloseEvent(viewer.player)
        this.setActiveContainerDefault(viewer.player)

        val container = newContainerAnvil(viewer.player)

        this.inventory = toBukkitInventory(container)

        this.containerId = getNextContainerId(viewer.player)
        this.sendPacketOpenWindow(viewer.player, containerId)
        this.setActiveContainer(viewer.player, container)
        this.setActiveContainerId(container, containerId)
        this.addActiveContainerSlotListener(container, viewer.player)
        viewer.openMenu = this
        viewer.player.updateInventory()
    }

    override fun reload() {
        viewer?.let { open(it) }
    }

    override fun setContents(contents: Array<ItemStack?>) {
        for (i in contents.indices) contents[i]?.let { setSlot(i, it) }
    }

    override fun getTask(slot: Int): Consumer<Interaction> = dataMap[slot]

    override fun clear() {
        dataMap.clear()
        items.clear()
    }
}
