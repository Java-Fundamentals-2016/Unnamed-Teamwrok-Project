package Models;

import Enumerations.Abilities;
import Abilities.Ability;
import Abilities.Dash;
import Abilities.MeleeAttack;
import Abilities.Defend;
import Enumerations.EntityState;
import Enumerations.UserOrders;
import Game.ControlState;
import Renderer.Animation;
import Renderer.QuickView;
import World.Coord;
import World.Physics;
import javafx.scene.input.KeyCode;

import java.util.HashMap;

/**
 * Dedicated class to differentiate the Player character from his inanimate parent class and his sibling Enemy
 * creatures. We keep user input information here - keys/button pressed and mouse position. Note Player framerate is
 * considerably higher than everything else, to make controls feel responsive.
 */
public class Player extends Creature {

    // User input record
    private double mouseX;
    private double mouseY;
    private ControlState currentInput;
    public UserOrders movementOrder;
    public UserOrders abilityOrder;
    private double timeSinceLastMove;
    private double timeSinceLastCast;

    private Inventory inventory;

    // Create new player
    public Player(int startHealthPoints,
                  int startAttackPower,
                  int startArmorValue,
                  double x, double y) {
        super(new Animation(15, "Player"),
                x, y, 0.0,
                startHealthPoints, startAttackPower, startArmorValue,
                new HashMap<Abilities, Ability>(),
                0.25, Physics.maxMoveSpeed, Physics.maxAcceleration);
        addAbility(Abilities.ATTACKPRIMARY, new MeleeAttack(this, startAttackPower, 1.0));
        addAbility(Abilities.DASH, new Dash(this, 5, 12));
        addAbility(Abilities.DEFEND, new Defend(this, 25, 2));

        currentInput = new ControlState();
        movementOrder = UserOrders.EMPTY;
        abilityOrder = UserOrders.EMPTY;
        timeSinceLastMove = 0;
        timeSinceLastCast = 0;
    }

    // TODO: Inventory management

    public ControlState getControlState() {
        return currentInput;
    }

    //region Keyboard Properties
    public void addKey(KeyCode kc) {
        currentInput.addKey(kc);
    }

    public void removeKey(KeyCode kc) {
        currentInput.removeKey(kc);
    }
    //endregion ==============================

    //region Mouse Properties
    public void setMouseLeft(boolean mouseLeft) {
        currentInput.setMouseLeft(mouseLeft);
    }

    public void setMouseRight(boolean mouseRight) {
        currentInput.setMouseRight(mouseRight);
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public double[] getMouse() {
        return new double[]{mouseX, mouseY};
    }

    public void updateMouse(double x, double y) {
        mouseX = x;
        mouseY = y;
    }
    //endregion ==============================

    public void handleInput(double elapsed) {
        timeSinceLastCast += elapsed;
        timeSinceLastMove += elapsed;
        // Don't let the player move if he's stunned
        if (hasState(EntityState.STAGGERED)) return;

        // Face mouse cursor, if not committed to an animation
        if (isReady()) userFaceCursor();

        // User can't control the character if it's velocity is greater than Physics.maxMoveSpeed
        // This has the positive side effect of disabling player controls during knockback
        if (getVelocity().getMagnitude() > Physics.maxMoveSpeed) return;

        // Mouse
        if (currentInput.isMouseLeft()) { // Attack
            // TODO: attack chaining (if Attack ability is in the correct state, add the next ability to the queue)
            timeSinceLastCast = 0;
            abilityOrder = UserOrders.ATTACK;
        }
        if (currentInput.isMouseRight()) { // Defend
            timeSinceLastCast = 0;
            abilityOrder = UserOrders.DEFEND;
        } else {
            if (abilities.get(Abilities.DEFEND).isActive())
                unUseAbility(Abilities.DEFEND);
        }

        // Keyboard
        if (currentInput.pressed(KeyCode.SPACE)) { // Dash
            timeSinceLastCast = 0;
            abilityOrder = UserOrders.DASH;
        }
        // Movement
        double modifier = Physics.playerAcceleration + Physics.friction; // we add friction so we can have a net positive
        boolean previous = movementOrder != UserOrders.EMPTY; // We need this to prevent resetting time every tick
        if (currentInput.pressed(KeyCode.W)) movementOrder = UserOrders.MOVE_NORTH; // go up
        if (currentInput.pressed(KeyCode.S)) movementOrder = UserOrders.MOVE_SOUTH; // go down
        if (currentInput.pressed(KeyCode.A)) movementOrder = UserOrders.MOVE_WEST; // go left
        if (currentInput.pressed(KeyCode.D)) movementOrder = UserOrders.MOVE_EAST; // go right

        if (currentInput.pressed(KeyCode.W) && currentInput.pressed(KeyCode.A))
            movementOrder = UserOrders.MOVE_NORTHWEST;
        if (currentInput.pressed(KeyCode.W) && currentInput.pressed(KeyCode.D))
            movementOrder = UserOrders.MOVE_NORTHEAST;
        if (currentInput.pressed(KeyCode.S) && currentInput.pressed(KeyCode.A))
            movementOrder = UserOrders.MOVE_SOUTHWEST;
        if (currentInput.pressed(KeyCode.S) && currentInput.pressed(KeyCode.D))
            movementOrder = UserOrders.MOVE_SOUTHEAST;
        if (movementOrder != UserOrders.EMPTY && !previous) timeSinceLastMove = 0; // If a key was pressed, reset time

        processOrders(elapsed); // Carry out
    }

    //region User Orders

    /**
     * Check current order and carry it out, then remove it from queue
     */
    private void processOrders(double elapsed) {
        // Movement
        if (timeSinceLastMove <= 0.3) {
            if (processMovement(elapsed))
                movementOrder = UserOrders.EMPTY;
        } else { // don't carry out if enough time has passed
            movementOrder = UserOrders.EMPTY;
        }

        // Abilities
        if (timeSinceLastCast <= 0.3) {
            if (processAbilities(elapsed))
                abilityOrder = UserOrders.EMPTY;
        } else { // don't carry out if enough time has passed
            abilityOrder = UserOrders.EMPTY;
        }
    }

    private boolean processMovement(double elapsed) {
        if (!canMove()) return false;
        double modifier = Physics.playerAcceleration + Physics.friction; // we add friction so we can have a net positive
        switch (movementOrder) {
            case MOVE_EAST:
                accelerate(new Coord(modifier, 0.0), elapsed);
                break;
            case MOVE_NORTHEAST:
                accelerate(new Coord(0.71 * modifier, -0.71 * modifier), elapsed);
                break;
            case MOVE_NORTH:
                accelerate(new Coord(0.0, -modifier), elapsed);
                break;
            case MOVE_NORTHWEST:
                accelerate(new Coord(-0.71 * modifier, -0.71 * modifier), elapsed);
                break;
            case MOVE_WEST:
                accelerate(new Coord(-modifier, 0.0), elapsed);
                break;
            case MOVE_SOUTHWEST:
                accelerate(new Coord(-0.71 * modifier, 0.71 * modifier), elapsed);
                break;
            case MOVE_SOUTH:
                accelerate(new Coord(0.0, modifier), elapsed);
                break;
            case MOVE_SOUTHEAST:
                accelerate(new Coord(0.71 * modifier, 0.71 * modifier), elapsed);
                break;
        }
        if (movementOrder == UserOrders.EMPTY) getState().remove(EntityState.MOVING);
        else getState().add(EntityState.MOVING);
        return true;
    }

    private boolean processAbilities(double elapsed) {
        switch (abilityOrder) {
            case ATTACK:
                return useAbility(Abilities.ATTACKPRIMARY);
            case DASH:
                if (hasState(EntityState.CASTDOWN)) { // allow movement to cancel animation
                    getState().remove(EntityState.CASTDOWN);
                    processMovement(elapsed);
                }
                return useAbility(Abilities.DASH);
            case DEFEND:
                return useAbility(Abilities.DEFEND);
        }
        return true;
    }

    private void userFaceCursor() {
        double offsetX = QuickView.toWorldX(mouseX) - getX();
        double offsetY = QuickView.toWorldY(mouseY) - getY();
        double dir = Math.atan2(offsetY, offsetX);
        setDirection(dir);
    }
    //endregion ==============================

}
