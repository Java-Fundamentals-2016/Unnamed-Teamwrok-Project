package Enumerations;

import java.util.EnumSet;

public enum EntityState {
    IDLE,
    MOVING,
    DEAD,
    STAGGERED, // controls disabled
    CASTUP, // controls disabled, can be interrupted (by staggering effects)
    CASTING, // resolution of effect
    CASTDOWN, // movement disabled, can use abilities
    DAMAGED, // immune to further damage, don't forget to toggle off!
    DESTROYED; // release instance

    // This state prevents the entity from changing it's state voluntarily
    public static final EnumSet<EntityState> BUSY = EnumSet.of(
            DEAD,
            STAGGERED,
            CASTUP,
            CASTING,
            DESTROYED);

    // This state is the inverse of BUSY, it means the entity can change it's state at will
    public static final EnumSet<EntityState> READY = EnumSet.complementOf(BUSY);
}
