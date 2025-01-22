package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.AnvilMenu
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.impl.ErrorMessages
import java.util.function.Consumer
import kotlin.reflect.KClass

@ClickCancelled(true)
class ConfigureValueMenu<T : Number> private constructor(
    private val menu: Menu,
    private val value: Consumer<T>,
    private val kclass: KClass<T>
) : AnvilMenu() {

    override fun update() {
        setSlot(1, ItemStacks.APPLY) { interaction: Interaction ->
            val profile = interaction.profile
            val inputText = text?.trim() ?: run {
                profile.message(ErrorMessages.INVALID_NUMBER)
                return@setSlot
            }

            // Try to parse inputText as whichever Kotlin numeric type we expect.
            val parsed: Number = try {
                when (kclass) {
                    Int::class -> inputText.toInt()
                    Double::class -> inputText.toDouble()
                    Float::class -> inputText.toFloat()
                    Long::class -> inputText.toLong()
                    Short::class -> inputText.toShort()
                    Byte::class -> inputText.toByte()

                    // If you only want to handle these six Kotlin numeric types, throw here:
                    else -> throw IllegalArgumentException(
                        "Unsupported numeric type: ${kclass.simpleName}"
                    )
                }
            } catch (e: NumberFormatException) {
                profile.message(ErrorMessages.INVALID_NUMBER)
                return@setSlot
            }

            // Unsafe cast but fine if the above 'when' matches the correct type:
            @Suppress("UNCHECKED_CAST")
            value.accept(parsed as T)

            profile.openMenu(menu)
        }

        setSlot(0, ItemStacks.CANCEL) { it.profile.openMenu(menu) }
    }

    companion object {
        fun <T : Number> of(
            menu: Menu,
            value: Consumer<T>,
            kclass: KClass<T>
        ): ConfigureValueMenu<T> {
            return ConfigureValueMenu(menu, value, kclass)
        }
    }
}

