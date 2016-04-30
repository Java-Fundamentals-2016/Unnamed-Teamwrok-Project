package Interfaces;

import Models.Entity;
import World.Coord;

public interface IMovable {
    //TODO:
    // Define all movement functions + possible collision and boundary checks
    void accelerate(Coord vector, double time);
    void stop();
    void place(Coord newPosition);
    void place(double newX, double newY);
    boolean hitscan(Entity target);
    Coord getVelocity();
    void setVelocity(Coord newVelocty);

    //boolean move(byte direction);
    //boolean moveUp();
    //boolean moveDown();
    //boolean moveLeft();
    //boolean moveRight();
}
