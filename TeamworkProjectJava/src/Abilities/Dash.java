package Abilities;

import Models.Creature;

/**
 * Quickly move a short distance in the direction of movement. If not moving, wait for some movement.
 */
public class Dash extends Ability {

    private double force;

    public Dash(Creature owner, double cooldown, double force) {
        super(owner, cooldown);
        this.force = force;
    }

    @Override
    public void use() {
        spend();
        if (owner.getVelocity().getMagnitude() == 0) {
            owner.moveForward(1.0); // if not moving, dash forward
        }
        owner.getVelocity().setMagnitude(force); // will this work?? apparently yes
    }

}
