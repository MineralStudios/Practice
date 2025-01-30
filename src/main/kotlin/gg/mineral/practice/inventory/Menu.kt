package gg.mineral.practice.inventory

import gg.mineral.practice.entity.Profile
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

interface Menu {

    var closed: Boolean

    val clickCancelled: Boolean

    val inventory: Inventory?

    fun setSlot(slot: Int, itemStack: ItemStack)

    fun setSlot(slot: Int, itemStack: ItemStack, consumer: Consumer<Interaction>)

    fun add(itemStack: ItemStack)

    fun add(itemStack: ItemStack, consumer: Consumer<Interaction>)

    fun update()

    fun shouldUpdate(): Boolean

    fun onClose()

    fun getItemBySlot(slot: Int): ItemStack?

    fun getItemByType(material: Material): ItemStack?

    fun contains(itemStack: ItemStack): Boolean

    fun open(viewer: Profile)

    fun reload()

    fun setContents(contents: Array<ItemStack?>)

    fun getTask(slot: Int): Consumer<Interaction>?

    fun clear()
}
