package Models;

import AI.Behaviour;
import AI.Gank;
import AI.Roam;
import Enumerations.AIState;
import Enumerations.Abilities;
import Enumerations.EntityState;
import Renderer.Animation;
import World.Coord;
import Abilities.Ability;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Dedicated class that has a few extra things compared to it's inanimate parent, mainly AI.
 */
public class Enemy extends Creature {
    LootTable lootTable;
    ArrayList<Behaviour> brain;

    public Enemy(Animation animation, double x,
                 double y, double direction,
                 int healthPoints, int attackPower,
                 int armorValue, HashMap<Abilities, Ability> abilities, double radius,
                 double maxSpeed, double maxAcceleration) {
        super(animation, x, y, direction, healthPoints, attackPower, armorValue, abilities, radius, maxSpeed, maxAcceleration);
        this.brain = new ArrayList<>();
    }

    /**
     * Add a new behaviour to the Enemy. Note the list sorted by priority each time.
     * @param newBrain A unique Behaviour. This can't be shared, since it contains a reference to it's owner! OR, it can
     *                 be shared, or even point to another Enemy, for hilarious results.
     */
    public void addBrain(Behaviour newBrain) {
        brain.add(newBrain);
        newBrain.start();
        brain.sort((b1, b2) -> Integer.compare(b2.getWeight(), b1.getWeight()));
    }

    public AIState getThought(int index) {
        return brain.get(index).getState();
    }

    /**
     * Consult each Behaviour and act accordingly. Once an action is taken, the rest of the possible actions are
     * ignored, hence sorting them by priority earlier.
     * @param time Seconds since last update
     */
    void processBehaviour(double time) {
        // using stream is overkill, since each creature wont have more than a handful of behaviours to cycle trough and
        // having the option to break the loop is better, considering each individual brain might have complex logic
        if (!hasState(EntityState.IDLE)) return;
        for (Behaviour behaviour : brain) {
            if (behaviour.update(time)) break; // break the loop as soon as one brain makes a decision
        }
    }

    // TODO: add methods to initialize loot table
}
