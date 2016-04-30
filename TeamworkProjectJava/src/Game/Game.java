package Game;

import Enumerations.Abilities;
import Enumerations.EntityState;
import Enumerations.GameState;
import Models.Creature;
import Models.Entity;
import Models.Player;
import Renderer.QuickView;
import World.Coord;
import World.Level;
import World.Physics;
import World.Tile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * This object will contain all instances required for a complete game to be initialized, updated and displayed:
 * JavaFX scene
 * A display interface (GraphicsContext of a Canvas node)
 * A control schema and handlers
 * The level with all of it's constituents
 * Other (yet to be implemented)
 * <p>
 * All other packages call to this object for reference and interfaces (for instance, rendering methods get their
 * context from here)
 */
public class Game {
    private GraphicsContext gc;
    private Level level;
    private ControlState controlState; // This should be a reference to the corresponding field from Player
    private long timeLast;      // System time in nanoseconds when last update occurred
    private double elapsed;     // Time elapsed since last update in seconds
    private GameState gameState;

    public Game(GraphicsContext gc, long timeStart) {
        this.gc = gc;
        timeLast = timeStart;
        elapsed = 0;
        gameState = GameState.MENU;
        makeLevel();
        controlState = level.getPlayer().getControlState();
    }

    /**
     * Initialize level; the coordinates for the player here don't matter, since a random starting location is chosen
     * by the Level itself, after the maze is generated
     */
    public void makeLevel() {
        level = new Level(new Player(100, 10, 2, 10, 10));
    }
    // TODO: this object can be streamed to a file for a complete game state save and load function

    // Shorthands for the most commonly used objects
    public GraphicsContext getGc() {
        return gc;
    }

    public Level getLevel() {
        return level;
    }

    // This will bypass some things that might turn out to be important later on
    public Player getPlayer() {
        return level.getPlayer();
    }

    // Timekeeping
    public long getTime() {
        return timeLast;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public double getElapsed() {
        return elapsed;
    }

    /**
     * Calculate length of previous frame in seconds and store it internally for all other time dependent functions.
     * @param timeNew Current processor time in nanoseconds. We convert that to seconds and forward to all entities for
     *                convenience
     */
    public void passTime(long timeNew) {
        elapsed = (timeNew - timeLast) / 1000000000.0;
        if (elapsed > 1.0) elapsed = 1.0;
        timeLast = timeNew;
    }

    /**
     * Update everything, called every frame. We only apply physics to the living ones and ignore the destroyed ones
     * altogether. Filters are in place to only update entities a certain distance from where the camera is looking.
     */
    public void update() {
        ArrayList<Entity> markedForDeletion = new ArrayList<>(); // prevent ConcurrentModificationException
        // Limit to Creature instances that are within a predetermined range (one screen width in each direction from the camera)
        level.getEntities().stream().filter(entity -> entity instanceof Creature)
                .filter(entity -> entity.getX() > getPlayer().getX() - Physics.activeRange &&
                        entity.getX() < getPlayer().getX() + Physics.activeRange &&
                        entity.getY() > getPlayer().getY() - Physics.activeRange &&
                        entity.getY() < getPlayer().getY() + Physics.activeRange)
                .forEach(entity -> {
                    if (entity.getState().contains(EntityState.DESTROYED)) { // release dead entries
                        markedForDeletion.add(entity);
                        return;
                    }
                    entity.animate(elapsed); // process animation first, since we need to know if state has changed (note this wont output, only update!)
                    if (entity.hasState(EntityState.DEAD)) return; // don't update if it's dead (not destroyed yet!)
                    Creature current = (Creature) entity;
                    current.update(elapsed);
                });
        // Release all marked entities
        for (Entity entity : markedForDeletion) {
            level.getEntities().remove(entity);
        }
    }

    /**
     * Output everything that's within range of the camera. Each entity takes care of it's own rendering, we just
     * propagate the call to everyone.
     */
    public void render() {
        // Level tiles first, so they appear bellow everything else
        level.getGeometry().stream()
                .filter(tile -> tile.getX() > getPlayer().getX() - Physics.activeRange &&
                        tile.getX() < getPlayer().getX() + Physics.activeRange &&
                        tile.getY() > getPlayer().getY() - Physics.activeRange &&
                        tile.getY() < getPlayer().getY() + Physics.activeRange)
                .forEach(Tile::render);
        // Now entities. Note dead creatures still render according to their creation index, so there might be some
        // unwanted overlap
        level.getEntities().stream()
                .filter(entity -> entity.getX() > getPlayer().getX() - Physics.activeRange &&
                        entity.getX() < getPlayer().getX() + Physics.activeRange &&
                        entity.getY() > getPlayer().getY() - Physics.activeRange &&
                        entity.getY() < getPlayer().getY() + Physics.activeRange)
                .forEach(Entity::render);
    }

    public ControlState getControlState() {
        return controlState;
    }

    /**
     * Control handler. We call this every frame to see what the user is doing and command the player character
     * accordingly.
     */
    public void handleInput() {
        // Forward call to Player
        getPlayer().handleInput(elapsed);
    }

}
