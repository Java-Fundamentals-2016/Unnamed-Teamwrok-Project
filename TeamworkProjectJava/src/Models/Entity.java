package Models;

import Enumerations.AnimationState;
import Enumerations.EntityState;
import Renderer.Animation;
import World.Coord;

import java.util.EnumSet;

abstract public class Entity {

    private static int lastID = 0; // all entities know the last assigned ID
    private int ID; // ID
    private EnumSet<EntityState> state;
    private Coord position;
    private double direction;
    private double radius; // used for collision detection
    private Animation animation;   // TODO: don't forget to initialize this in a constructor instead
    // TODO: Add methods to initialize a sprite and output it to a display interface /!\ depends on direction

    public Entity(Animation animation, double x, double y, double direction, double radius) {
        ID = ++lastID; // increment then assign

        this.animation = animation;
        position = new Coord(x, y);
        this.direction =  direction;
        this.radius = radius;
        state = EnumSet.of(EntityState.IDLE); // all entities idle at creation, modify using set method
    }

    public int getID() {
        return ID;
    }

    // Position and orientation properties
    public Coord getPos() {
        return position;
    }
    public void setPos(double x, double y) {
        position.setPos(x, y);
    }
    public void setPos(Coord position) {
        this.position.setX(position.getX());
        this.position.setY(position.getY());
    }

    public double getX() {
        return position.getX();
    }
    public void setX(double x) {
        position.setX(x);
    }

    public double getY() {
        return position.getY();
    }
    public void setY(double y) {
        position.setY(y);
    }

    public double getDirection() {
        return direction;
    }
    public void setDirection(double direction) {
        this.direction = direction;
    }

    // Face particular coordinate (maybe we should overload this to work with entity ID)
    public void turnTo(double x, double y) {
        direction = Coord.angleBetween(this.getPos(), new Coord(x, y));
    }

    public double getRadius() {
        return radius;
    }

    // State sets
    public void setState(EnumSet state) {
        this.state = state;
    }
    public EnumSet<EntityState> getState() {
        return state;
    }

    public boolean hasState(EntityState state) {
        return this.state.contains(state);
    }
    // Shorthands
    public boolean isAlive() {
        return (!state.contains(EntityState.DESTROYED));
    }

    public boolean isReady() {
        // Check against a list of all unchangeable states
        // We ignore RECOVER, since we can cancel that animation to block, dodge, chain attacks, etc.
        if (state.contains(EntityState.DEAD) ||
                state.contains(EntityState.STAGGERED) ||
                state.contains(EntityState.CASTUP) ||
                state.contains(EntityState.CASTING) ||
                state.contains(EntityState.DESTROYED)) return false;
        return true;
    }

    // If nothing's going on, go to idle
    public void resetState() {
        if (state.isEmpty()) state = EnumSet.of(EntityState.IDLE);
    }

    // Display
    public Animation getAnimation() {
        return animation;
    }
    public AnimationState getAnimationState() {
        return animation.getState();
    }

    // TODO: Output sprite to display interface /!\ depends on direction
    public void render()
    {
        if (animation == null || !isAlive()) return;

        animation.output(this, position.getX(), position.getY(), direction);
    }

    public void animate(double time) {
        animation.advance(time);
    }

    public void changeAnimation(String newSequence) {
        if (newSequence.equals("Idle")) {
            animation.setState(AnimationState.IDLE);
        } else if (newSequence.equals("Primary Attack")) {
            animation.setState(AnimationState.ATTACKUP);
        } else if (newSequence.equals("Defend")) {
            animation.setState(AnimationState.DEFEND);
        }
        // TODO: list of animation sequences can be a map of names or abilities and corresponding frames
        // TODO: check if named sequence exists? maybe check in Animation class
    }

}

