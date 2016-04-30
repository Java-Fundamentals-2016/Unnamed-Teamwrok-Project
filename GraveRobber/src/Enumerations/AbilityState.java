package Enumerations;

public enum AbilityState {
    READY, // nothing going on
    INIT, // wind up
    RESOLVE, // resolution of effect
    RECOVER, // wind down, can be cancelled or chained
    COOLING, // in cool down
    ACTIVE // for abilities that are toggled on/off
}
