package Models;

import Enumerations.InventoryStatus;

public class Consumable extends Loot {
    private int ammo;
    private int duration;

    public Consumable(double x, double y, InventoryStatus inventoryStatus, int cost, int ammo, int duration) {
        super(x, y, inventoryStatus, cost);
        this.ammo = ammo;
        this.duration = duration;
    }
}
