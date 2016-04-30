/**
 * This class is deprecated and should be removed
 */

package World;

import Enumerations.TileType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Deprecated, remove from project. /!\ ALSO CONFLICTING NAME /!\
 */
public class Map {

    public static final int mazeWidth = 160;
    public static final int mazeHeight = 120;

    private ArrayList<Dungeon> maze;
    private ArrayList<Tile> levelTiles;

    public Map() {
        this.maze = new ArrayList<Dungeon>();
        dungeonGenerate();
        processTiles();
    }

    public void dungeonGenerate() {
        maze = new ArrayList<>();
        Dungeon root = new Dungeon(2, 2, mazeWidth, mazeHeight); //
        // add root to array and pass it on
        maze.add(root);

        for (int i = 0; i < Level.CURRENT_LEVEL + 1; i++) {
            ArrayList<Dungeon> listCopy = new ArrayList<Dungeon>(maze);
            for (Dungeon rectangle : listCopy) {
                if (rectangle.getLeftChild() != null) continue;
                if (rectangle.split()) { //attempt to split
                    maze.add(rectangle.getLeftChild());
                    maze.add(rectangle.getRightChild());
                }
            }
        }
        maze.get(0).generateDungeon();
        ArrayList<Dungeon> result = new ArrayList<>();
        for (Dungeon dungeon : maze) {
            if (dungeon.getDungeon() == null && dungeon.getHallway() == null) continue;
            result.add(dungeon);
        }
        maze = result;
    }

    public ArrayList<Dungeon> getMaze() {
        return maze;
    }

    public ArrayList<Tile> getLevelTiles() {
        return levelTiles;
    }

    /*
    OBSOLETE
     */
    public void getTiles() {
        levelTiles = new ArrayList<>();
        for (Dungeon dungeon : this.maze) {
            if (dungeon.getDungeon() != null) {
                // Room
                //x firstCol
                int x = dungeon.getDungeon().getX();
                //y firstRow
                int y = dungeon.getDungeon().getY();
                //width lastCol
                int width = dungeon.getDungeon().getWidth();
                //height lastRow
                int height = dungeon.getDungeon().getHeight();


                for (int row = y; row < y + height; row++) {
                    for (int col = x; col < x + width; col++) {
                        if (row == y || row == y + height - 1 || col == x || col == x + width - 1) {
                            levelTiles.add(new Tile(col, row, TileType.WALL));
                        } else {
                            levelTiles.add(new Tile(col, row, TileType.FLOOR));
                        }
                    }
                }
            } else if (dungeon.getHallway() != null) {
                // Hallway

                int x = dungeon.getHallway().getX();
                int y = dungeon.getHallway().getY();
                int width = dungeon.getHallway().getWidth();
                int height = dungeon.getHallway().getHeight();

                for (int row = y; row < y + height; row++) {
                    for (int col = x; col < x + width; col++) {
                        if (row == y || row == y + height - 1 || col == x || col == x + width - 1) {
                            levelTiles.add(new Tile(col, row, TileType.WALL));
                        } else {
                            levelTiles.add(new Tile(col, row, TileType.FLOOR));
                        }
                    }
                }
            }
        }
    }

    public void processTiles() {
        levelTiles = new ArrayList<>(); // initialize

        // temp matrix
        byte[][] levelMatrix = new byte[mazeWidth][mazeHeight];
        // fill matrix with void
        for (int i = 0; i < levelMatrix.length; i++) {
            for (int j = 0; j < levelMatrix[i].length; j++) {
                levelMatrix[i][j] = 0;
            }
        }

        for (Dungeon dungeon : this.maze) {
            if (dungeon.getDungeon() != null) {
                // Room
                //x firstCol
                int x = dungeon.getDungeon().getX();
                //y firstRow
                int y = dungeon.getDungeon().getY();
                //width lastCol
                int width = dungeon.getDungeon().getWidth();
                //height lastRow
                int height = dungeon.getDungeon().getHeight();


                for (int row = y; row < y + height; row++) {
                    for (int col = x; col < x + width; col++) {
                        if (row == y || row == y + height - 1 || col == x || col == x + width - 1) {
                            if (levelMatrix[col][row] != 2) // don't place walls on th floor
                                levelMatrix[col][row] = 1; // wall
                        } else {
                            levelMatrix[col][row] = 2; // floor
                        }
                    }
                }
            } else if (dungeon.getHallway() != null) {
                // Hallway

                int x = dungeon.getHallway().getX();
                int y = dungeon.getHallway().getY();
                int width = dungeon.getHallway().getWidth();
                int height = dungeon.getHallway().getHeight();

                for (int row = y; row < y + height; row++) {
                    for (int col = x; col < x + width; col++) {
                        if (row == y || row == y + height - 1 || col == x || col == x + width - 1) {
                            if (levelMatrix[col][row] != 2) // don't place walls on th floor
                                levelMatrix[col][row] = 1; // wall
                        } else {
                            levelMatrix[col][row] = 2; // floor
                        }
                    }
                }
            }
        }
        // Convert matrix to usable tiles

        for (int i = 0; i < levelMatrix.length; i++) {
            for (int j = 0; j < levelMatrix[i].length; j++) {
                if (levelMatrix[i][j] == 0) continue;
                levelTiles.add(new Tile(i, j, levelMatrix[i][j] == 1 ? TileType.WALL : TileType.FLOOR));
            }
        }
    }
}
