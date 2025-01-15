package gg.mineral.practice.bukkit.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier
import org.bukkit.event.player.PlayerEvent

/**
 * Stores data for damage events
 */
open class PlayerDamageEvent(private val entityDamageEvent: EntityDamageEvent) :
    PlayerEvent(entityDamageEvent.entity as Player), Cancellable {
    override fun isCancelled() = entityDamageEvent.isCancelled

    override fun setCancelled(cancel: Boolean) {
        entityDamageEvent.isCancelled = cancel
    }

    /**
     * Gets the original damage for the specified modifier, as defined at this
     * event's construction.
     *
     * @param type the modifier
     * @return the original damage
     * @throws IllegalArgumentException if type is null
     */
    @Throws(IllegalArgumentException::class)
    fun getOriginalDamage(type: DamageModifier?) = entityDamageEvent.getOriginalDamage(type)

    /**
     * Sets the damage for the specified modifier.
     *
     * @param type   the damage modifier
     * @param damage the scalar value of the damage's modifier
     * @see .getFinalDamage
     * @throws IllegalArgumentException      if type is null
     * @throws UnsupportedOperationException if the caller does not support
     * the particular DamageModifier, or to
     * rephrase, when [                                       ][.isApplicable] returns
     * false
     */
    @Throws(IllegalArgumentException::class, UnsupportedOperationException::class)
    fun setDamage(type: DamageModifier?, damage: Double) = entityDamageEvent.setDamage(type, damage)

    /**
     * Gets the damage change for some modifier
     *
     * @param type the damage modifier
     * @return The raw amount of damage caused by the event
     * @throws IllegalArgumentException if type is null
     * @see DamageModifier.BASE
     */
    @Throws(IllegalArgumentException::class)
    fun getDamage(type: DamageModifier) = entityDamageEvent.getDamage(type)

    /**
     * This checks to see if a particular modifier is valid for this event's
     * caller, such that, [.setDamage] will not
     * throw an [UnsupportedOperationException].
     *
     *
     * [DamageModifier.BASE] is always applicable.
     *
     * @param type the modifier
     * @return true if the modifier is supported by the caller, false otherwise
     * @throws IllegalArgumentException if type is null
     */
    @Throws(IllegalArgumentException::class)
    fun isApplicable(type: DamageModifier) = entityDamageEvent.isApplicable(type)

    var damage: Double
        /**
         * Gets the raw amount of damage caused by the event
         *
         * @return The raw amount of damage caused by the event
         * @see DamageModifier.BASE
         */
        get() = getDamage(DamageModifier.BASE)
        /**
         * Sets the raw amount of damage caused by the event.
         *
         *
         * For compatibility this also recalculates the modifiers and scales
         * them by the difference between the modifier for the previous damage
         * value and the new one.
         *
         * @param damage The raw amount of damage caused by the event
         */
        set(damage) {
            entityDamageEvent.damage = damage
        }

    val finalDamage: Double
        /**
         * Gets the amount of damage caused by the event after all damage
         * reduction is applied.
         *
         * @return the amount of damage caused by the event
         */
        get() = entityDamageEvent.finalDamage

    val cause: DamageCause
        /**
         * Gets the cause of the damage.
         *
         * @return A DamageCause value detailing the cause of the damage.
         */
        get() = entityDamageEvent.cause

    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}