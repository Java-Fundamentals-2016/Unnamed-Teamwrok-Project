package Interfaces;

public interface IBehaviour {
    void start();
    void pause();
    void stop();
    void reset();
    boolean update(double time);
    boolean think(double time);
}
