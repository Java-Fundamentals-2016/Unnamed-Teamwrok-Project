package Models;

import Enumerations.InventoryStatus;

public class Item extends Loot {
    public Item(double x, double y, InventoryStatus inventoryStatus, int cost) {
        super(x, y, inventoryStatus, cost);
    }
}
