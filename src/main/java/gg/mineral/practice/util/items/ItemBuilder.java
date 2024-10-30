package gg.mineral.practice.util.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.val;

public class ItemBuilder {
    private Material material;
    private int durability, amount;
    private String name;
    private List<String> lore = null;

    public ItemBuilder(ItemStack item) {
        this.material = item.getType();
        this.durability = item.getDurability();
        this.amount = item.getAmount();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public static ItemBuilder from(ItemStack item) {
        return new ItemBuilder(item);
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder durability(int durability) {
        this.durability = durability;
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder lore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemStack build() {
        val newItemStack = new ItemStack(material, amount, (short) durability);
        val meta = newItemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        newItemStack.setItemMeta(meta);
        return newItemStack;
    }
}
