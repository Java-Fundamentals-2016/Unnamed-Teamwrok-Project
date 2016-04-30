package World;

import Enumerations.EnemyTypes;
import Enumerations.TileType;
import Factories.CreatureFactory;
import Models.Entity;
import Models.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contains the level geometry (grid), a tileset and a list of entity instances. One per game! Methods are self-
 * explanatory or documented if otherwise.
 */
public class Level {

    Player player; // For convenience access; do not render player separately, he will appear in entity list!
    List<Entity> entities;
    ArrayList<Tile> geometry;

    public static byte CURRENT_LEVEL = 1;

    // TODO: add level geometry and populate with entities
    public Level() {
        entities = new ArrayList<>();
        generateGeometry();
        spawnEnemies();
        player = new Player(100, 10, 0, 0, 0);
        setStart();
        entities.add(player);
    }

    public Level(Player player) {
        entities = new ArrayList<>();
        generateGeometry();
        spawnEnemies();
        this.player = player;
        setStart();
        entities.add(this.player);
    }

    private void generateGeometry() {
        DungeonMaker generator = new DungeonMaker();
        geometry = generator.processTiles();
    }

    public Player getPlayer() {
        return player;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public ArrayList<Tile> getGeometry() {
        return geometry;
    }

    // Pick starting position inside the maze and place player there
    private void setStart() {
        Random rnd = new Random();
        while (true) {
            int tile = rnd.nextInt(geometry.size());
            if (geometry.get(tile).getTileType() == TileType.FLOOR) {
                player.setX(geometry.get(tile).getX());
                player.setY(geometry.get(tile).getY());
                break;
            }
        }
    }

    private void spawnEnemies() {
        // Add 7 Skeletons and 13 Rats
        Random rnd = new Random();
        // Get just the floor tiles
        ArrayList<Tile> floor = new ArrayList<>();
        geometry.stream().filter(tile -> tile.getTileType() == TileType.FLOOR).forEach(tile -> floor.add(tile));
        for (int i = 0; i < 7; i++) {
            Tile current = floor.get(rnd.nextInt(floor.size())); // Pick random tile
            entities.add(CreatureFactory.createEnemy(EnemyTypes.SKELETON, current.getX(), current.getY(), rnd.nextDouble() * Math.PI * 2));
            floor.remove(current); // Remove tile from list
        }
        for (int i = 0; i < 13; i++) {
            Tile current = floor.get(rnd.nextInt(floor.size())); // Pick random tile
            entities.add(CreatureFactory.createEnemy(EnemyTypes.GIANT_RAT,current.getX(), current.getY(), rnd.nextDouble() * Math.PI * 2));
            floor.remove(current); // Remove tile from list
        }
    }
}
