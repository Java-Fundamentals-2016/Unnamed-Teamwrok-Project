package AI;

import Enumerations.AIState;
import Models.Creature;
import World.Coord;
import World.Physics;

import java.util.Random;

/**
 * Random wandering about the maze, not keeping track of where the creature started. Passive.
 */
public class Roam extends Behaviour {

    private double nextUpdate; // when do we need to move again
    private double frequency; // how often movement occurs, actions per second
    private double progress; // how much is left of the operation, count down to zero
    private Random rnd;

    public Roam(Creature owner, double frequency) {
        super(owner, 1); // very low priority, enemies roam only if they have nothing to do
        this.frequency = frequency;
        progress = 0;
        rnd = new Random();
        prepareNext();
    }

    /**
     * Main processing hub, called every update. We keep track of time and decide what action to take and how often,
     * depending on frequency. Actions are either look around or move forward a short distance, 50% for both to occur.
     * @param time Seconds since last update
     * @return True if acton was taken, false if still thinking about it (when returning true, AI with lower weight wont
     * be processed!)
     */
    @Override
    public boolean think(double time) {
        if (state == AIState.PROCESSING) {
            progress -= time;
            if (progress <= 0) { // if action is complete, go back to idle
                progress = 0;
                state = AIState.THINKING;
                reset();
                return false;
            }
            act(time); // else, carry on with what was started (walking, in this case)
            return true;
        } else if (elapsedTime > nextUpdate) { // if not already processing action, wait for trigger (time)
            // Decide whether to look around or to move forward
            if (rnd.nextInt(2) == 1) { // move
                state = AIState.PROCESSING;
                progress = 0.25 + rnd.nextDouble(); // 0.25-1.25 seconds of movement
            } else { // look around
                owner.setDirection(rnd.nextDouble() * Math.PI * 2);
                prepareNext();
                reset();
            }
            return true;
        }
        return false;
    }

    private void act(double time) {
        // move forward
        owner.moveForward(time);
    }

    /**
     * Calculate the next event, depending on frequency. The listed times are for the default frequency of 0.3 APS,
     * which is rather docile. If we want a frantic enemy, bump this value up significantly.
     */
    private void prepareNext() {
        nextUpdate = 3 + rnd.nextDouble() / this.frequency * 0.4; // 3-5 seconds
    }

}
