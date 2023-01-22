package gg.mineral.practice.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.NumberConversions;

/**
 * Stores data for damage events
 */
public class PlayerDamageEvent extends PlayerEvent implements Cancellable {
    final EntityDamageEvent entityDamageEvent;

    public PlayerDamageEvent(EntityDamageEvent entityDamageEvent) {
        super((Player) entityDamageEvent.getEntity());
        this.entityDamageEvent = entityDamageEvent;
    }

    public boolean isCancelled() {
        return entityDamageEvent.isCancelled();
    }

    public void setCancelled(boolean cancel) {
        entityDamageEvent.setCancelled(cancel);
    }

    /**
     * Gets the original damage for the specified modifier, as defined at this
     * event's construction.
     *
     * @param type the modifier
     * @return the original damage
     * @throws IllegalArgumentException if type is null
     */
    public double getOriginalDamage(DamageModifier type) throws IllegalArgumentException {
        return entityDamageEvent.getOriginalDamage(type);
    }

    /**
     * Sets the damage for the specified modifier.
     *
     * @param type   the damage modifier
     * @param damage the scalar value of the damage's modifier
     * @see #getFinalDamage()
     * @throws IllegalArgumentException      if type is null
     * @throws UnsupportedOperationException if the caller does not support
     *                                       the particular DamageModifier, or to
     *                                       rephrase, when {@link
     *                                       #isApplicable(DamageModifier)} returns
     *                                       false
     */
    public void setDamage(DamageModifier type, double damage)
            throws IllegalArgumentException, UnsupportedOperationException {
        entityDamageEvent.setDamage(type, damage);
    }

    /**
     * Gets the damage change for some modifier
     *
     * @param type the damage modifier
     * @return The raw amount of damage caused by the event
     * @throws IllegalArgumentException if type is null
     * @see DamageModifier#BASE
     */
    public double getDamage(DamageModifier type) throws IllegalArgumentException {
        return entityDamageEvent.getDamage(type);
    }

    /**
     * This checks to see if a particular modifier is valid for this event's
     * caller, such that, {@link #setDamage(DamageModifier, double)} will not
     * throw an {@link UnsupportedOperationException}.
     * <p>
     * {@link DamageModifier#BASE} is always applicable.
     *
     * @param type the modifier
     * @return true if the modifier is supported by the caller, false otherwise
     * @throws IllegalArgumentException if type is null
     */
    public boolean isApplicable(DamageModifier type) throws IllegalArgumentException {
        return entityDamageEvent.isApplicable(type);
    }

    /**
     * Gets the raw amount of damage caused by the event
     *
     * @return The raw amount of damage caused by the event
     * @see DamageModifier#BASE
     */
    public double getDamage() {
        return getDamage(DamageModifier.BASE);
    }

    /**
     * Gets the amount of damage caused by the event after all damage
     * reduction is applied.
     *
     * @return the amount of damage caused by the event
     */
    public final double getFinalDamage() {
        return entityDamageEvent.getFinalDamage();
    }

    /**
     * This method exists for legacy reasons to provide backwards
     * compatibility. It will not exist at runtime and should not be used
     * under any circumstances.
     * 
     * @return the (rounded) damage
     */
    @Deprecated
    public int _INVALID_getDamage() {
        return NumberConversions.ceil(getDamage());
    }

    /**
     * Sets the raw amount of damage caused by the event.
     * <p>
     * For compatibility this also recalculates the modifiers and scales
     * them by the difference between the modifier for the previous damage
     * value and the new one.
     *
     * @param damage The raw amount of damage caused by the event
     */
    public void setDamage(double damage) {
        entityDamageEvent.setDamage(damage);
    }

    /**
     * This method exists for legacy reasons to provide backwards
     * compatibility. It will not exist at runtime and should not be used
     * under any circumstances.
     * 
     * @param damage the new damage value
     */
    @Deprecated
    public void _INVALID_setDamage(int damage) {
        setDamage(damage);
    }

    /**
     * Gets the cause of the damage.
     *
     * @return A DamageCause value detailing the cause of the damage.
     */
    public DamageCause getCause() {
        return entityDamageEvent.getCause();
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}