package Models;

import Enumerations.InventoryStatus;
import Renderer.Animation;

abstract public class Loot extends Entity {
        private InventoryStatus inventoryStatus;
        private int Cost;

    public Loot(double x, double y, InventoryStatus inventoryStatus, int cost) {
        super(new Animation(0.0, "ITEM"), x, y, 0.0, 0.25);
        this.inventoryStatus = inventoryStatus;
        Cost = cost;
        super.setX(x);
        super.setY(y);
    }
}
