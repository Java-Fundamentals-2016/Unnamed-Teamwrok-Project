package Abilities;

import Enumerations.AbilityState;
import Enumerations.EntityState;
import Interfaces.IAbility;
import Models.Creature;

/**
 * Parent class for all player and enemy abilities. Implements basic functionality shared by all derived classes as well
 * as some methods that keep track of owner state and prevent overlap, for subclasses that don't have complicated
 * structure.
 */
abstract public class Ability implements IAbility {

    double remaining; // time to next use, seconds
    double cooldown; // time between uses, seconds
    AbilityState state; // state
    protected final Creature owner; // reference to creature

    /**
     * Bare bones constructor
     * @param owner Reference to Creature
     * @param cooldown Time between uses, in seconds
     */
    public Ability (Creature owner, double cooldown) {
        this.owner = owner; // passed by reference, we don't want a copy
        this.cooldown = cooldown;
        reset();
    }

    // Properties and shorthands
    public AbilityState getState() {
        return state;
    }

    public boolean isReady() {
        return state == AbilityState.READY;
    }

    public boolean isCancelable() {
        return state == AbilityState.RECOVER;
    }

    public boolean isCooling() {
        return state == AbilityState.COOLING;
    }

    /**
     * Place ability in cooldown.
     * Separate from use(), so we can force an ability to resolve, if necessary.
     */
    @Override
    public void spend() {
        state = AbilityState.COOLING;
        remaining = cooldown;
    }

    /**
     * Some abilities do not have casting time or resolution point - those wont need an implementation of update(), so
     * we have to make sure they are cooled down in the general case
     * @param time Seconds since last update
     */
    @Override
    public void update(double time) {
        if (isCooling()) cool(time);
        if ((state == AbilityState.INIT ||state == AbilityState.RESOLVE) &&
                !vrfyState()) { // make sure owner is not interrupted
            // if ability was initiated, put it in cool down mode, cancel further resolution
            state = AbilityState.COOLING;
        }
    }

    @Override
    public void cool(double time) {
        if (isReady()) return;
        remaining -= time;
        if (remaining <=0) reset();
    }

    @Override
    public void reset() {
        remaining = 0;
        state = AbilityState.READY;
    }

    public void unUse() { // support for toggleable abilities
        state = AbilityState.READY;
    }

    public boolean isActive() {
        return state == AbilityState.ACTIVE;
    }

    public double getCooldown() {
        return cooldown - remaining;
    }

    /**
     * Check if entity has not cancelled the ability, for instance, by getting staggered or destroyed. This check MUST
     * occur in all abilities that have casting time and resolution point - if entity state has changed and ability is
     * not ready, set it to cooling, so further updates are cancelled.
     * @return Result of check, true when still committed
     */
    boolean vrfyState() {
        if (!owner.getState().contains(EntityState.CASTUP) && !owner.getState().contains(EntityState.CASTING)) {
            // entity state has changed from external source
            return false;
        }
        return true;
    }

}
