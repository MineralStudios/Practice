package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.AnvilMenu
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.impl.ErrorMessages
import java.util.function.Consumer

@ClickCancelled(true)
class ConfigureValueMenu<T> private constructor(
    private val menu: Menu,
    private val value: Consumer<T>,
    private val type: Class<T>
) : AnvilMenu() {
    override fun update() {
        setSlot(1, ItemStacks.APPLY) { interaction: Interaction ->
            val profile = interaction.profile
            val text = text

            if (text == null) {
                profile.message(ErrorMessages.INVALID_NUMBER)
                return@setSlot
            }

            try {
                when (type) {
                    Double::class.java, Double::class.javaPrimitiveType -> value.accept(
                        type.cast(text.replace(" ", "").toDouble())
                    )

                    Float::class.java, Float::class.javaPrimitiveType -> value.accept(
                        type.cast(text.replace(" ", "").toFloat())
                    )

                    Int::class.java, Int::class.javaPrimitiveType -> value.accept(
                        type.cast(
                            text.replace(" ", "").toInt()
                        )
                    )

                    Long::class.java, Long::class.javaPrimitiveType -> value.accept(
                        type.cast(
                            text.replace(" ", "").toLong()
                        )
                    )

                    Short::class.java, Short::class.javaPrimitiveType -> value.accept(
                        type.cast(text.replace(" ", "").toShort())
                    )

                    Byte::class.java, Byte::class.javaPrimitiveType -> value.accept(
                        type.cast(
                            text.replace(" ", "").toByte()
                        )
                    )

                    else -> throw IllegalArgumentException("Unsupported type: " + type.typeName)
                }
            } catch (e: NumberFormatException) {
                profile.message(ErrorMessages.INVALID_NUMBER)
                return@setSlot
            }
            profile.openMenu(menu)
        }

        setSlot(
            0, ItemStacks.CANCEL
        ) { interaction ->
            interaction.profile.openMenu(
                menu
            )
        }
    }

    companion object {
        fun <T> of(menu: Menu, value: Consumer<T>, type: Class<T>): ConfigureValueMenu<T> =
            ConfigureValueMenu(menu, value, type)
    }
}
