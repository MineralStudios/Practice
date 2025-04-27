package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ErrorMessages
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@ClickCancelled(true)
class AddItemsMenu : PracticeMenu() {
    override fun update() {
        val maxAmountMap = Object2IntOpenHashMap<String>()

        viewer.kitEditor?.let {

            for (`is` in it.gametype.kit.contents) {
                if (`is` == null || !LIMITED.contains(`is`.type.id)) continue

                val string: String = `is`.type.toString() + ":" + `is`.durability
                val maxAmount: Int = maxAmountMap.getInt(string)
                maxAmountMap.put(string, maxAmount + `is`.amount)
            }

            for (`is` in it.gametype.kit.contents) {
                if (`is` == null) continue

                val type = `is`.type

                if (contains(`is`)) continue

                if (INCLUDED.contains(type.id)) {
                    val iterator = INCLUDED.iterator()
                    while (iterator.hasNext()) {
                        val material = iterator.nextInt()
                        val item = ItemStack(material, 64)
                        add(item) { interaction: Interaction ->
                            interaction.profile.player?.itemOnCursor =
                                item
                        }
                    }

                    continue
                }

                if (LIMITED.contains(type.id)) {
                    add(`is`) {
                        val string: String = `is`.type.toString() + ":" + `is`.durability
                        val maxAmount: Int = maxAmountMap.getInt(string)

                        if (viewer.inventory.getNumberAndAmount(`is`.type, `is`.durability) >= maxAmount) {
                            viewer.message(ErrorMessages.ITEM_LIMIT)
                            return@add
                        }
                        viewer.player?.itemOnCursor = `is`
                    }
                    continue
                }

                add(`is`) {
                    viewer.player?.itemOnCursor =
                        `is`
                }
            }
        }
    }

    override val title: String
        get() = CC.BLUE + "Add Items"

    override fun shouldUpdate() = false

    companion object {
        private val LIMITED: IntSet = IntOpenHashSet.of(
            Material.DIAMOND_HELMET.id,
            Material.DIAMOND_CHESTPLATE.id,
            Material.DIAMOND_LEGGINGS.id, Material.DIAMOND_BOOTS.id, Material.MUSHROOM_SOUP.id,
            Material.POTION.id,
            Material.GOLDEN_APPLE.id, Material.ENDER_PEARL.id, Material.WATER_BUCKET.id,
            Material.LAVA_BUCKET.id, Material.ARROW.id
        )

        private val INCLUDED: IntSet = IntOpenHashSet.of(
            Material.COOKED_BEEF.id,
            Material.GOLDEN_CARROT.id,
            Material.GRILLED_PORK.id
        )
    }
}
