package World;

/**
 * Definition of a standard vector in 2D Euclidean space. Includes a few static methods for vector calc. Also doubles as
 * point location. Most methods are self explanatory or documented if otherwise.
 */
public class Coord {
    double x;
    double y;

    public Coord() {
        x = 0.0;
        y = 0.0;
    }
    public Coord(double inX, double inY) {
        x = inX;
        y = inY;
    }
    public Coord(double[] pos) {
        x = pos[0];
        y = pos[1];
    }

    public double getX() {
        return x;
    }
    public void setX(double inX) {
        x = inX;
    }

    public double getY() {
        return y;
    }
    public void setY(double inY) {
        y = inY;
    }

    public void setPos(double inX, double inY) {
        x = inX;
        y = inY;
    }
    public void setPos(double[] pos) {
        x = pos[0];
        y = pos[1];
    }
    public double[] getPos() {
        double[] result = {x, y};
        return result;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }
    public void setMagnitude(double magnitude) {
        double current = getMagnitude();
        if (current == 0) return;
        double scalar = magnitude / current;
        x *= scalar;
        y *= scalar;
    }

    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    public double getDirection() {
        double dir = Math.atan2(y, x);
        return dir;
    }

    public void setDirection(double dir) {
        double magnitude = getMagnitude();
        x = magnitude * Math.cos(dir);
        y = magnitude * Math.sin(dir);
    }

    // This will modify the vector!
    public void doSubtract(Coord vector) {
        x -= vector.getX();
        y -= vector.getY();
    }

    // This will modify the vector!
    public void doAdd(Coord vector) {
        x += vector.getX();
        y += vector.getY();
    }

    // As parameters: coordinates, map to check against
    public boolean verifyBounds (/* Overloads for x, y; Coord */) {
        // Implement map boundary check
        return true;
    }

    // Static methods for vector math; these don't modify the object (duh, they're static)
    public static Coord add(Coord vector1, Coord vector2) {
        double x = vector1.getX() + vector2.getX();
        double y = vector1.getY() + vector2.getY();
        return new Coord(x, y);
    }

    public static Coord subtract(Coord vector1, Coord vector2) {
        double x = vector1.getX() - vector2.getX();
        double y = vector1.getY() - vector2.getY();
        return new Coord(x, y);
    }

    public static double angleBetween(Coord vector1, Coord vector2) {
        return subtract(vector2, vector1).getDirection();
    }

    /**
     * Calculates angle between two points and a line projected from the first point at given angle
     * @param vector1 First point
     * @param vector2 Second point
     * @param angle Angle of line projected from first point
     * @return Inner angle of the triangle
     */
    public static double innerAngle(Coord vector1, Coord vector2, double angle) {
        if (angle < 0) angle += 2 * Math.PI;
        double angle2 = angleBetween(vector1, vector2);
        if (angle2 < 0) angle2 += 2 * Math.PI;
        double inner = Math.abs(angle - angle2);
        if (inner > Math.PI) inner -= 2 * Math.PI;
        return Math.abs(inner);
    }
}
