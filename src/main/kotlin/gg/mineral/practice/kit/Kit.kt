package gg.mineral.practice.kit

import org.bukkit.inventory.ItemStack

class Kit(
    val name: String = "Custom",
    var contents: Array<ItemStack?> = arrayOfNulls(36),
    var armourContents: Array<ItemStack?> = arrayOfNulls(4)
) {
    constructor(kit: Kit) : this(kit.name, kit.contents, kit.armourContents)

    constructor(
        contents: Array<ItemStack?> = arrayOfNulls(36),
        armourContents: Array<ItemStack?> = arrayOfNulls(4)
    ) : this("Custom", contents, armourContents)
}
