package AI;

import Enumerations.AIState;
import Enumerations.Abilities;
import Enumerations.EntityState;
import Game.Main;
import Models.Creature;
import Models.Enemy;
import World.Coord;

import java.util.Random;

/**
 * Default attacking behaviour for melee enemies. Since the range is set at initialization, this will actually work for
 * ranged creatures just as well. If the player is in range, face them and attempt to use our primary attack. The
 * ability itself (ATTACKPRIMARY) takes care of it's readiness and cooldown, so we just want this to be triggering as
 * much as possible.
 */
public class Aggression extends Behaviour {

    private double range; // attack reach

    public Aggression(Creature owner, double range) {
        super(owner, 10); // high priority, we pretty much always chose to attack the player in range
        this.range = range;
    }

    /**
     * If the player is within the range of our weapon, we try to swing at them. Called every update.
     * @param time Seconds since last udate
     * @return True when triggered, keeps returning true as long as player is within range, allows other AI to process
     * as soon as the player walks away, even if attack is still resolving - ability state and animation priority will
     * take care of us not moving/doing anything else before we're done attacking first. This actually allows for lower
     * priority abilities to be chained right after (like for instance, lunge the player as soon as our first attack is
     * done, if he's moved away, or fall back to dodge incoming damage).
     */
    @Override
    public boolean think(double time) {
        if (Main.game.getPlayer().getState().contains(EntityState.DEAD)) return false; // don't bother if he's down
        if (Coord.subtract(Game.Main.game.getPlayer().getPos(), owner.getPos()).getMagnitude() <= range) {
            // face player
            owner.turnTo(Game.Main.game.getPlayer().getX(), Game.Main.game.getPlayer().getY());
            // attempt attack (wont do anything if creature has no primary attack, or if it's processing/cooling down)
            owner.useAbility(Abilities.ATTACKPRIMARY);
            return true;
        }
        return false;
    }

}
