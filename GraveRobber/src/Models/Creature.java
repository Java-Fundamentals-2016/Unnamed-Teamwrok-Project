package Models;

import Abilities.Ability;
import Abilities.Defend;
import Enumerations.*;
import Game.Main;
import Interfaces.IMovable;
import Renderer.Animation;
import World.Coord;
import World.Physics;
import World.Tile;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * Parent class for everything that is affected by physics (movement, collisions). Maybe not the most apt name, since
 * inanimate boxes and barriers also fit here
 */
abstract public class Creature extends Entity implements IMovable {
    // Stats
    private int healthPoints;
    private int attackPower;
    private int armorValue;
    HashMap<Abilities, Ability> abilities;
    double immuneTime = 0; // time to next damage instance

    // Physical characteristics
    private double maxSpeed;
    private double maxAcceleration;
    private Coord velocity; // current velocity vector

    public Creature(Animation animation,
                    double x, double y,
                    double direction, int healthPoints,
                    int attackPower, int armorValue,
                    HashMap<Abilities, Ability> abilities,
                    double radius, double maxSpeed,
                    double maxAcceleration) {
        super(animation, x, y, direction, radius);
        this.healthPoints = healthPoints;
        this.attackPower = attackPower;
        this.armorValue = armorValue;
        this.abilities = abilities;
        this.maxSpeed = maxSpeed;
        this.maxAcceleration = maxAcceleration;
        velocity = new Coord(0.0, 0.0);
    }

    //region Properties
    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int value) {
        this.healthPoints = value;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int value) {
        this.attackPower = value;
    }

    public int getArmorValue() {
        return armorValue;
    }

    public void setArmorValue(int value) {
        this.armorValue = value;
    }

    public double getCooldown(Abilities ability) {
        if (abilities.containsKey(ability))
            return abilities.get(ability).getCooldown();
        else return 0.0;
    }
    //endregion ==============================

    //region Movement
    // Implementation of IMovable, everything to do with motion
    @Override
    public void accelerate(Coord vector, double time) {
        vector.scale(time);
        velocity.doAdd(vector);
        if (velocity.getMagnitude() > Physics.maxVelocity) {
            velocity.setMagnitude(Physics.maxVelocity);
        }
    }

    /**
     * Unconditional teleport to target location, disregarding physics and timing!
     */
    @Override
    public void stop() {
        velocity = new Coord(0, 0);
    }

    @Override
    public void place(Coord newPosition) {
        super.setPos(newPosition);
    }

    @Override
    public void place(double newX, double newY) {
        super.setPos(newX, newY);
    }

    @Override
    public Coord getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Coord newVelocity) {
        velocity = newVelocity;
    }

    /**
     * Since many operations require the creature to just move where it's looking, this method takes care of all vector
     * calc and just adds the needed acceleration in the proper direction
     *
     * @param time seconds since last update
     */
    public void moveForward(double time) {
        // TODO: extend checks for staggering effects that prevent voluntary movement
        if (velocity.getMagnitude() > Physics.maxVelocity) return; // this checks for sudden movements (like knockback)
        Coord vector = new Coord(maxAcceleration + Physics.friction, 0.0);
        vector.setDirection(getDirection());
        accelerate(vector, time);
        if (velocity.getMagnitude() > maxSpeed) velocity.setMagnitude(maxSpeed); // make sure we're not going too fast
    }

    public boolean canMove() {
        // if player is busy, don't let him move
        if (hasState(EntityState.CASTUP) ||
                hasState(EntityState.CASTING) ||
                hasState(EntityState.CASTDOWN)) return false;
        return true;
    }
    //endregion ==============================

    //region Collision detection and resolution

    /**
     * Check and resolve collision with other entities. Resolution method used is Projection (we calculate penetration
     * depth and move each entity half that distance away from each other, instantly).
     *
     * @param target Entity to check against. Both entities will be moved!
     * @return False if no intersection, true if detected (and resolved)
     */
    // TODO second parameter to tell whether to resolve, so we can use this to check for item pickups without bumping them around
    @Override
    public boolean hitscan(Entity target) {
        Coord dist = new Coord(getX(), getY());
        dist.doSubtract(target.getPos());
        double penetration = dist.getMagnitude() - (getRadius() + target.getRadius());
        if (penetration < 0.0) {
            // collision; resolve via projection (entities placed apart, no vector modification)
            Main.debugInfo += String.format("%ncollision");
            dist.setMagnitude(penetration / 2); // separation vector
            getPos().doSubtract(dist);
            dist.scale(-1); // push target entity in opposite direction
            target.getPos().doSubtract(dist);
            // TODO: modify each entity's velocity vector, so they aren't moving towards each other
            return true;
        }
        return false;
    }

    /**
     * Check and resolve collisions with level geometry. Resolution method used is Projection (we calculate penetration
     * depth and move entity that distance away from intersecting boundary, instantly).
     *
     * @param tile Level Tile to check against
     * @return False if no intersection, true if detected (and resolved)
     */
    private boolean vrfyBounds(Tile tile) {
        double tileHalf = 0.5; // this may have to change, if we start checking irregular boundaries
        double distX = Math.abs(tile.getX() - getX());
        double distY = Math.abs(tile.getY() - getY());
        if (distX > tileHalf + getRadius() || distY > tileHalf + getRadius()) return false;
        if (distX <= tileHalf + getRadius() && distY <= tileHalf) {
            double penetration = 0;
            if (tile.getX() - getX() < 0) { // right side
                penetration = distX - (tileHalf + getRadius());
            } else { // left side
                penetration = (tileHalf + getRadius()) - distX;
            }
            setX(getX() - penetration); // resolve
            return true;
        }
        if (distX <= tileHalf && distY <= tileHalf + getRadius()) {
            double penetration = 0;
            if (tile.getY() - getY() < 0) { // bottom side
                penetration = distY - (tileHalf + getRadius());
            } else { // top side
                penetration = (tileHalf + getRadius()) - distY;
            }
            setY(getY() - penetration); // resolve
            return true;
        }

        double corner = Math.pow(distX - tileHalf, 2) + Math.pow(distY - tileHalf, 2);
        if (corner <= Math.pow(getRadius(), 2)) {
            /* We ignore corner collisions for now, only report detection
            Coord penetration = Coord.subtract(new Coord(tile.getX(), tile.getY()), getPos());
            penetration.setMagnitude(0.6 + getRadius() - penetration.getMagnitude());
            getPos().doSubtract(penetration);
            */
            return true;
        }

        return false;
    }
    //endregion ==============================

    /**
     * Update the creature, depending on time elapsed since last update. Process behaviour, if entity has an AI
     * attached, look for collisions with other objects, move physically, cool down all used abilites, etc.
     *
     * @param time Seconds since last update
     */
    public void update(double time) {
        // Keep track of damage instances
        if (immuneTime > 0) immuneTime -= time;
        if (getState().contains(EntityState.DAMAGED) && immuneTime <= 0) {
            getState().remove(EntityState.DAMAGED);
            getState().remove(EntityState.STAGGERED);
            immuneTime = 0;
        }
        // Process behaviour
        if (this instanceof Enemy) {
            ((Enemy) this).processBehaviour(time);
        }

        // If the object is moving, update vector with friction and project new position, based on time and velocity
        if (velocity.getMagnitude() != 0) Physics.decelerate(velocity, time);
        double newX = super.getX() + velocity.getX() * time;
        double newY = super.getY() + velocity.getY() * time;
        super.setX(newX);
        super.setY(newY);

        // Detect collisions
        // Creatures
        Main.game.getLevel().getEntities().stream()
                .filter(entity -> !entity.hasState(EntityState.DEAD)) // don't collide with corpses
                .filter(entity -> Math.abs(entity.getX() - getX()) < Physics.activeRange / 2 && Math.abs(entity.getY() - getY()) < Physics.activeRange / 2)
                .filter(entity -> entity instanceof Creature) // get just the creatures
                .filter(entity -> !entity.equals(this)) // can't collide with self
                .forEach(entity -> ((Creature) entity).hitscan(this)); // resolution currently included in detection, can be filtered further

        // Walls
        Main.game.getLevel().getGeometry().stream()
                .filter(tile -> tile.getTileType() == TileType.WALL) //just the walls
                .filter(tile -> Math.abs(tile.getX() - getX()) < Physics.activeRange && Math.abs(tile.getY() - getY()) < Physics.activeRange)
                .forEach(this::vrfyBounds);

        // Update used abilities (they cool themselves down, if needed)
        abilities.entrySet().stream().forEach(entry -> entry.getValue().update(time));
        resetState(); // make sure we have something here
    }

    //region Abilities
    public Ability getAbility(Abilities ability) {
        if (abilities.containsKey(ability))
            return abilities.get(ability);
        else return null;
    }

    /**
     * Add an ability to the list
     *
     * @param name    A name from the dedicated enumeration
     * @param ability An instance of the ability. Do not attach the same reference to two different creatures, since
     *                cooldown and effects are linked back to the creature
     */
    public void addAbility(Abilities name, Ability ability) {
        abilities.put(name, ability);
    }

    /**
     * Attempt to use ability. This will make sure we're ready and the ability is present and ready (not in use, not
     * cooling down) and will also cancel everything else the Creature is doing. If we want to force-trigger an ability,
     * we can bypass these checks for hilarious results.
     *
     * @param ability Name of ability to be activated
     * @return True if ability was activated successfully
     */
    public boolean useAbility(Abilities ability) {
        if (!isReady()) return false;
        if (abilities.containsKey(ability)) {
            if (!abilities.get(ability).isReady()) return false;
            stopAbilities(); // cancel all ongoing abilities
            cancelAnimation();
            getState().remove(EntityState.CASTDOWN);
            resetState();
            abilities.get(ability).use();
            return true;
        }
        return false;
    }

    public void unUseAbility(Abilities ability) {
        if (abilities.containsKey(ability)) {
            cancelAnimation();
            resetState();
            abilities.get(ability).unUse();
        }
    }

    // Cancel all abilities that are processing and put them in cooldown
    public void stopAbilities() {
        abilities.entrySet().stream()
                .filter(entry -> entry.getValue().getState() == AbilityState.INIT ||
                        entry.getValue().getState() == AbilityState.RECOVER)
                .forEach(entry -> entry.getValue().spend());
    }
    //endregion ==============================

    public void cancelAnimation() {
        getAnimation().setState(AnimationState.IDLE);
    }

    //region Damage and external effects
    // Stun the creature
    public void stagger() {
        stopAbilities();
        cancelAnimation();
        setState(EnumSet.of(EntityState.STAGGERED));
    }

    /**
     * Process damage from different sources and forward to resolver
     */
    public void takeDamage(double damage) {
        resolveDamage(damage, DamageType.GENERIC, null);
    }

    public void takeDamage(double damage, DamageType type) {
        resolveDamage(damage, type, null);
    }

    public void takeDamage(double damage, Coord source) {
        resolveDamage(damage, DamageType.GENERIC, source);
    }

    public void takeDamage(double damage, DamageType type, Coord source) {
        resolveDamage(damage, type, source);
    }

    /**
     * Process incoming damage
     *
     * @param damage Amount of damage received
     * @param type   Type of source (weapon, world, spell, etc.)
     * @param source Location of damage source
     */
    public void resolveDamage(double damage, DamageType type, Coord source) {
        // TODO: armor calculation
        // Knockback effect, if hit with a weapon from a known source (so we knock back away from source)
        if ((type == DamageType.WEAPONMELEE || type == DamageType.WEAPONRANGED) && source != null) {
            double refsize = 0.25; // temp scalar
            double knockback = 2 - 2 * (getRadius() - 0.5 * refsize) / (1.5 * refsize);
            if (knockback > 2) knockback = 2; // Limit to prevent small enemies from flying across the map
            if (knockback > 0) { // Apply knockback
                Coord kickvector = new Coord(knockback * 5, 0.0);
                kickvector.setDirection(Coord.angleBetween(source, getPos()));
                stop();
                accelerate(kickvector, 1.0);
                // TODO: disable movement for a short period
            } // don't do anything if entity is not affected
        }
        // TODO: stagger enemies if damaged more than 30% in 2 seconds
        if (damage > 0) { // prevent negative damage from healing
            if (getState().contains(EntityState.DAMAGED)) return; // prevent instances from resolving more than once
            if (this instanceof Player) {
                Defend shield = (Defend) abilities.get(Abilities.DEFEND);
                // redirect damage to shield
                if (shield.isActive() && shield.getHealth() > 0 &&
                        Coord.innerAngle(getPos(), source, getDirection()) < Math.PI / 4) {
                    damage = shield.takeDamage(damage);
                    if (damage > 0) stagger();
                } else {
                    stagger(); // player always gets staggered
                }
                immuneTime = 0.5;
            } else {
                immuneTime = 0.1; // Enemies have a much shorter invinciframe
            }
            getState().add(EntityState.DAMAGED); // prevent instances from resolving more than once

            healthPoints -= (int) damage;

            // todo add dying animation
            if (healthPoints <= 0) {
                stopAbilities();
                cancelAnimation();
                setState(EnumSet.of(EntityState.DEAD));
            }
        }
    }
    //endregion ==============================

}
