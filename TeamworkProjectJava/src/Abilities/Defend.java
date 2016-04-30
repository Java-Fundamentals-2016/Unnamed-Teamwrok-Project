package Abilities;

import Enumerations.Abilities;
import Enumerations.AbilityState;
import Enumerations.AnimationState;
import Enumerations.EntityState;
import Models.Creature;

public class Defend extends Ability {

    private double rechargeRate; // HP per second
    private double health;
    private double currentHealth;

    public Defend(Creature owner, double health, double rechargeRate) {
        super(owner, 0); // raising the shield has no cooldown, it's health is what matters
        this.health = health;
        currentHealth = health;
        this.rechargeRate = rechargeRate;
    }

    @Override
    public void use() {
        state = AbilityState.ACTIVE;
        owner.changeAnimation(Abilities.DEFEND.getName());
    }

    @Override
    public void update(double elapsed) {
        if (currentHealth < health && !owner.hasState(EntityState.DAMAGED)) {
            currentHealth += elapsed  * rechargeRate;
            if (currentHealth > health) currentHealth = health;
        }
    }

    public double getMaxHealth() {
        return health;
    }

    public double getHealth() {
        return currentHealth;
    }

    public double takeDamage(double damage) {
        currentHealth -= damage;
        if (currentHealth < 0) {
            double remainingDamage = currentHealth * -1;
            currentHealth = 0;
            return remainingDamage;
        }
        return 0;
    }

}
