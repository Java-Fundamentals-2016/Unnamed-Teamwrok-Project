package AI;

import Enumerations.AIState;
import Enumerations.EntityState;
import Game.Main;
import Models.Creature;
import World.Coord;

import java.util.Random;

/**
 * Causes affected creatures to gang up on the player, if within range. Passive (for now). Note that currently there is
 * no keeping track of aggro, if player moves outside the range, pursuit wil cease. To prevent interrupting other
 * behaviours, this wont process if we've arrived a certain distance from the player (still close enough to impair his
 * movement).
 */
public class Gank extends Behaviour {

    private double nextUpdate; // when do we need to move again, seconds
    private double range; // aggro range
    private double progress; // how much is left of the operation, count down to zero seconds
    private Random rnd;

    public Gank(Creature owner, double range) {
        super(owner, 5); // average priority, still some space bellow it for secondary actions
        this.range = range;
        progress = 0;
        rnd = new Random();
        prepareNext();
    }

    /**
     * If player is dead or outside our aggro range, we do nothing. Otherwise, face his general direction and move
     * forward in short bursts of random length (actually makes timing an attack harder, so evil). Called every update.
     * @param time Seconds since last update
     * @return True when triggered or processing (moving forward) (lower priority AI wont process!)
     */
    @Override
    public boolean think(double time) {
        if (Main.game.getPlayer().getState().contains(EntityState.DEAD)) return false;
        double distToPlayer = Coord.subtract(Game.Main.game.getPlayer().getPos(), owner.getPos()).getMagnitude();
        if (distToPlayer <= range && distToPlayer >= 0.5) {
            // face player
            owner.turnTo(Game.Main.game.getPlayer().getX(), Game.Main.game.getPlayer().getY());
            if (state == AIState.PROCESSING) {
                progress -= time;
                if (progress <= 0) { // if action is complete, go back to idle
                    progress = 0;
                    state = AIState.THINKING;
                    reset();
                    return false;
                }
                act(time); // else, Keep Walking (tm)
                return true;
            } else if (elapsedTime > nextUpdate) { // if not already processing action, wait for trigger (time)
                state = AIState.PROCESSING;
                progress = 0.25 + rnd.nextDouble() / 2; // 0.25-0.75 seconds of movement
                prepareNext();
                return true;
            }
        }
        return false;
    }

    private void act(double time) {
        // move forward
        owner.moveForward(time);
    }

    /**
     * Decide when next burst of movement occurs. The listed intervals are always the same, no way to change them with
     * a Property or otherwise.
     */
    private void prepareNext() {
        nextUpdate = rnd.nextDouble() / 3; // 0.0-0.33 seconds
    }

}
