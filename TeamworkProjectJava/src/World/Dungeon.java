package World;

import java.util.Random;

public class Dungeon {

    public Dungeon() {
    }

    private static int MIN_SIZE = 5;
    private static Random rnd = new Random();

    private int x, y, width, height;
    private Dungeon leftChild;
    private Dungeon rightChild;

    /**
     * Properties
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Dungeon getRightChild() {
        return rightChild;
    }

    public void setRightChild(Dungeon rightChild) {
        this.rightChild = rightChild;
    }

    public Dungeon getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Dungeon leftChild) {
        this.leftChild = leftChild;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Dungeon dungeon;

    /**
     * Constructor
     */
    public Dungeon(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Attempts to split the room in two, if not already split and if MIN_SIZE has not been reached
     */
    public boolean split() {
        if (leftChild != null) //if already split, bail out
            return false;

        /**
         * direction of split - true for horizontal (top and bottom nodes), false for vertical (left and right nodes)
         */
        boolean direction = rnd.nextBoolean();
        int max = (direction ? height : width) - MIN_SIZE; //maximum height/width we can split off
        if (max <= MIN_SIZE) // area too small to split, bail out
            return false;
        int newSize = rnd.nextInt(max); // generate split point
        if (newSize < MIN_SIZE)  // adjust split point so there's at least MIN_SIZE in both partitions
            newSize = MIN_SIZE;
        if (direction) { //populate child areas
            leftChild = new Dungeon(x, y, width, newSize);
            rightChild = new Dungeon(x, y + newSize, width, height - newSize);
        } else {
            leftChild = new Dungeon(x, y, newSize, height);
            rightChild = new Dungeon(x + newSize, y, width - newSize, height);
        }
        return true; //split successful
    }

    /**
     * Recursively generate all leafs
     */
    public void generateDungeon() {
        if (leftChild != null) { //if current are has child areas, propagate the call
            leftChild.generateDungeon();
            rightChild.generateDungeon();
        } else { // if leaf node, create a dungeon within the minimum size constraints
            int dungeonTop = (height - MIN_SIZE <= 0) ? 0 : rnd.nextInt(height - MIN_SIZE);
            int dungeonLeft = (width - MIN_SIZE <= 0) ? 0 : rnd.nextInt(width - MIN_SIZE);
            int dungeonHeight = Math.max(rnd.nextInt(height - dungeonTop), MIN_SIZE);
            int dungeonWidth = Math.max(rnd.nextInt(width - dungeonLeft), MIN_SIZE);
            dungeon = new Dungeon(x + dungeonLeft, y + dungeonTop, dungeonWidth, dungeonHeight);
        }
    }
}