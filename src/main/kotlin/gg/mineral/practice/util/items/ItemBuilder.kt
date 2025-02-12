package gg.mineral.practice.util.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemBuilder(item: ItemStack) {
    private val material: Material = item.type
    private var durability: Int
    private var amount: Int
    private var name: String? = null
    private var lore: List<String>? = null

    init {
        this.durability = item.durability.toInt()
        this.amount = item.amount
    }

    constructor(material: Material) : this(ItemStack(material))

    fun amount(amount: Int): ItemBuilder {
        this.amount = amount
        return this
    }

    fun durability(durability: Int): ItemBuilder {
        this.durability = durability
        return this
    }

    fun name(name: String?): ItemBuilder {
        this.name = name
        return this
    }

    fun lore(vararg lore: String): ItemBuilder {
        this.lore = listOf(*lore)
        return this
    }

    fun build(): ItemStack {
        val newItemStack = ItemStack(material, amount, durability.toShort())
        val meta = newItemStack.itemMeta
        meta.displayName = name
        meta.lore = lore
        newItemStack.setItemMeta(meta)
        return newItemStack
    }

    companion object {
        fun from(item: ItemStack): ItemBuilder {
            return ItemBuilder(item)
        }
    }
}
