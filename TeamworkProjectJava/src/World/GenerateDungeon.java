/**
 * This class is deprecated and should be removed
 */

package World;

import Renderer.QuickView;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Deprecated, remove from project
 */
public class GenerateDungeon {

    private static Random rnd = new Random();
    public static ArrayList<Dungeon>  map = new ArrayList<>();

    public ArrayList<Dungeon> getMap() {
        return map;
    }

    public static ArrayList<Dungeon> sampleMake() {
        ArrayList<Dungeon> rectangles = new ArrayList<>(); // flat rectangle store to help pick a random one
        Dungeon root = new Dungeon(0, 0, 160, 120); //
        // add root to array and pass it on
        rectangles.add(root);
        return rectangles;
    }

    /**
     * This method creates dungeons iteratively and outputs them to the grid
     */
    public static void sampleStep(ArrayList<Dungeon> rectangles) {
        ArrayList<Dungeon> listCopy = new ArrayList<>(rectangles);
        for (Dungeon rectangle : listCopy) {
            if (rectangle.getLeftChild() != null) continue;
            if (rectangle.split()) { //attempt to split
                rectangles.add(rectangle.getLeftChild());
                rectangles.add(rectangle.getRightChild());
            }
        }
    }

    /**
     * @param list ArrayList of Dungeon to be trimmed to leaves only
     * @return The trimmed list
     */
    public static ArrayList<Dungeon> filterTree(ArrayList<Dungeon> list) {
        ArrayList<Dungeon> result = new ArrayList<>();
        for (Dungeon dungeon : list) {
            if (dungeon.getDungeon() == null && dungeon.getHallway() == null) continue;
            result.add(dungeon);
        }
        return result;
    }

    public static ArrayList<Dungeon> generateLevel(){
        map = GenerateDungeon.sampleMake();
        for (int i = 0; i < Level.CURRENT_LEVEL + 1; i++) {
            GenerateDungeon.sampleStep(map);
        }
        map.get(0).generateDungeon();
        GenerateDungeon.filterTree(map);
        return map;
    }

}