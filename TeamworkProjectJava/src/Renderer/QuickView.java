package Renderer;

import Enumerations.GameState;
import Game.Main;
import World.Dungeon;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.w3c.dom.css.Rect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * For testing only. Here be dragons, proceed at own risk.
 */
public class QuickView {
    // Grid size
    static public int gridSize = 5;
    static public double cameraWidth = 160;
    static public double cameraHeight = 120;
    static public double cameraX = 79;
    static public double cameraY = 59;
    // Camera controls
    static public void adjustRes(int size) {
        gridSize = size;
        cameraWidth = Main.horizontalRes / size;
        cameraHeight = Main.verticalRes / size;
    }

    static public void moveCamera(double x, double y) {
        cameraX = x;
        cameraY = y;
    }

    // Display world
    static public void drawGrid(GraphicsContext gc) {
        // Line properties
        gc.save();
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);

        int startingX = (int) Math.floor(cameraX - cameraWidth);
        if (startingX < 0) startingX = 0;
        int startingY = (int) Math.floor(cameraY - cameraHeight);
        if (startingY < 0) startingY = 0;
        int endingX = startingX + (int) cameraWidth * 2;
        int endingY = startingY + (int) cameraHeight * 2;

        for (int i = startingY; i < endingY; i++) { // Rows
            for (int j = startingX; j < endingX; j++) { // Columns
                // Adjust rect so the coordinates are at it's center
                double[] pos = {
                        toCanvasX(j - 0.5),
                        toCanvasY(i - 0.5)
                };
                gc.strokeRect(pos[0], pos[1], gridSize, gridSize);
            }
        }
        gc.restore();
    }

    static public void setBlock(GraphicsContext gc, int x, int y, int type) {
        /**
         * Type:
         * 0 - empty (void)
         * 1 - white (walls)
         * 2 - grey (floor)
         */
        switch (type) {
            case 0:
                gc.setFill(Color.BLACK);
                break;
            case 1:
                gc.setFill(Color.WHITE);
                break;
            case 2:
                gc.setFill(Color.DARKORANGE);
                break;
        }
        gc.fillRect(toCanvasX(x + 0.5), toCanvasY(y + 0.5), gridSize, gridSize);
    }

    static public void renderDungeon(GraphicsContext gc, ArrayList<Dungeon> list, boolean finalize) {
        for (Dungeon dungeon : list) {
            if (finalize) {
                if (dungeon.getDungeon() != null) {
                    // Room
                    int x1 = dungeon.getDungeon().getX();
                    int y1 = dungeon.getDungeon().getY();
                    int x2 = dungeon.getDungeon().getWidth() + x1;
                    int y2 = dungeon.getDungeon().getHeight() + y1;
                    for (int i = x1; i < x2; i++) {
                        for (int j = y1; j < y2; j++) {
                            setBlock(gc, i, j, 1);
                        }
                    }
                    /*
                    for (int i = x1; i < x2; i++) {
                        setBlock(gc, i, y1, 1);
                        setBlock(gc, i, y2 - 1, 1);
                    }
                    for (int j = y1; j < y2; j++) {
                        setBlock(gc, x1, j, 1);
                        setBlock(gc, x2 - 1, j, 1);
                    }
                    */
                } else {
                    // Hallway
                    if (dungeon.getHallway() != null) {
                        int hx1 = dungeon.getHallway().getX();
                        int hy1 = dungeon.getHallway().getY();
                        int hx2 = dungeon.getHallway().getWidth() + hx1;
                        int hy2 = dungeon.getHallway().getHeight() + hy1;
                        for (int i = hx1; i < hx2; i++) {
                            setBlock(gc, i, hy1, 1);
                            setBlock(gc, i, hy2 - 1, 1);
                        }
                        for (int j = hy1; j < hy2; j++) {
                            setBlock(gc, hx1, j, 1);
                            setBlock(gc, hx2 - 1, j, 1);
                        }
                    }
                }
            } else {
                // Boundary
                int bx1 = dungeon.getX();
                int by1 = dungeon.getY();
                int bx2 = dungeon.getWidth() + bx1;
                int by2 = dungeon.getHeight() + by1;
                for (int i = bx1; i < bx2; i++) {
                    setBlock(gc, i, by1, 2);
                    setBlock(gc, i, by2 - 1, 2);
                }
                for (int j = by1; j < by2; j++) {
                    setBlock(gc, bx1, j, 2);
                    setBlock(gc, bx2 - 1, j, 2);
                }
            }
        }
    }

    // Display entities and effects
    static public void renderSprite(int selector, double x, double y, double dir, double radius) {
        GraphicsContext gc = Main.game.getGc();
        // temp constants
        double size = radius * 2 * gridSize;
        // Translate direction indicator
        x = toCanvasX(x);
        y = toCanvasY(y);
        double dirX = x + size * 0.6 * Math.cos(dir);
        double dirY = y + size * 0.6 * Math.sin(dir);

        gc.setStroke(Color.WHITE); // pick color based on type
        switch (selector) {
            case 0:
                gc.setFill(Color.GREY);
                break;
            case 1:
                gc.setFill(Color.GREEN);
                break;
            case 2:
                gc.setFill(Color.LIGHTPINK);
                break;
            case 3:
                gc.setFill(Color.RED);
                break;
            case 4:
                gc.setFill(Color.BLACK);
                break;
        }
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);
        gc.strokeLine(x, y, dirX, dirY);

        /**
         * Debug info
         */
        if (false) {
            String debug = String.format("%.2f, %.2f", x, y);
            debug += String.format("%n%.2f", dir);
            gc.setFill(Color.WHITE);
            gc.fillText(debug, x + gridSize / 2, y);
        }
    }

    static public void renderDot(double x, double y) {
        GraphicsContext gc = Main.game.getGc();
        // temp constants
        double size = 15;
        gc.setFill(Color.GREY);
        if (Main.game.getControlState().isMouseLeft()) {
            gc.setFill(Color.RED);
            size = 25;
        }
        gc.fillOval(x - size / 2, y - size / 2, size, size);
    }

    static public void renderArrow(double x, double y, double dir) {
        // Canvas coordinates
        GraphicsContext gc = Main.game.getGc();
        double size = 5;
        gc.setFill(Color.WHITE);
        if (Main.game.getControlState().isMouseLeft()) {
            gc.setFill(Color.RED);
            size = 6;
        }
        double[] aX = {
                size * 1 * Math.cos(dir),
                size * 2 * Math.cos(dir + Math.PI / 2),
                size * 2.82 * Math.cos(dir + Math.PI * 3 / 4),
                size * 1 * Math.cos(dir + Math.PI),
                size * 2.82 * Math.cos(dir + Math.PI * 5 / 4),
                size * 2 * Math.cos(dir + Math.PI * 3 / 2)
        };
        double[] aY = {
                size * 1 * Math.sin(dir),
                size * 2 * Math.sin(dir + Math.PI / 2),
                size * 2.82 * Math.sin(dir + Math.PI * 3 / 4),
                size * 1 * Math.sin(dir + Math.PI),
                size * 2.82 * Math.sin(dir + Math.PI * 5 / 4),
                size * 2 * Math.sin(dir + Math.PI * 3 / 2)
        };
        for (int i = 0; i < 6; i++) {
            aX[i] += x;
            aY[i] += y;
        }
        gc.fillPolygon(aX, aY, 6);
    }

    static public void renderSword(double x, double y, double dir, double progress) {
        GraphicsContext gc = Main.game.getGc();
        // Convert position to pixels
        double length = 0.75;
        double px1 = 0.0;
        double py1 = 0.375;
        double px2 = length;
        double py2 = 0.375;
        double angle = 0.0;
        if (progress > 0 && progress <= 1) { // raise sword
            Main.debugInfo += String.format("%n%.2f", progress);
            length = Math.abs(0.25 - progress * 0.5);
            px1 = 0.375;
            py1 = 0.0;
            px2 = px1 + length;
            py2 = 0.0;
            if (progress <= 0.5) angle = -Math.PI / 4 - (Math.PI / 2) * (progress * 2);
            else angle = -3 * Math.PI / 4 + (Math.PI / 2) * (progress - 0.5) * 2;
        } else if (progress > 1 && progress <= 2) { // swing in front
            progress -= 1;
            Main.debugInfo += String.format("%n%.2f", progress);
            length = 0.25 + progress * 0.5;
            px1 = 0.375;
            py1 = 0.0;
            px2 = px1 + length;
            py2 = 0.0;
            angle = -Math.PI / 4 + (Math.PI / 2) * progress;
        } else if (progress > 2) { // recover, sheath sword
            progress -= 2;
            Main.debugInfo += String.format("%n%.2f", progress);
            length = 0.75 - progress * 0.5;
            px1 = 0.375 - progress * 0.375;
            py1 = progress * 0.375;
            px2 = px1 + length;
            py2 = progress * 0.375;
            angle = Math.PI / 4 - (Math.PI * 3 / 8) * progress;
        }

        double[] point1 = rotateXY(px1, py1, dir - angle);
        double[] point2 = rotateXY(px2, py2, dir - angle);
        point1[0] = toCanvasX(x + point1[0]);
        point1[1] = toCanvasY(y + point1[1]);
        point2[0] = toCanvasX(x + point2[0]);
        point2[1] = toCanvasY(y + point2[1]);

        gc.save();
        gc.setStroke(Color.GREY);
        gc.setLineWidth(4.0);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeLine(point1[0], point1[1], point2[0], point2[1]);
        gc.restore();
    }

    static public void renderSwipe(double x, double y, double dir, double progress) {
        GraphicsContext gc = Main.game.getGc();

        double range = progress;

        gc.save();
        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(1 + progress * 2);
        gc.strokeArc(toCanvasX(x - range),
                toCanvasY(y - range),
                range * 2 * gridSize,
                range * 2 * gridSize,
                Math.toDegrees(-dir - Math.PI / 4),
                90 * progress,
                ArcType.OPEN);
        gc.strokeArc(toCanvasX(x - range * 0.75),
                toCanvasY(y - range * 0.75),
                range * 1.5 * gridSize,
                range * 1.5 * gridSize,
                Math.toDegrees(-dir - Math.PI / 4),
                90 * progress,
                ArcType.OPEN);
        gc.restore();
    }

    static public void renderClaws(double x, double y, double dir, double progress) {
        GraphicsContext gc = Main.game.getGc();
        gc.save();
        gc.setStroke(Color.RED);
        gc.setLineWidth(3);

        double range = 0.45;

        if (progress <= 1) { // prepare
            double p1x = 0.25 + progress * 0.2;
            double p2x = 0.25 + progress * 0.2;
            double[] p1 = rotateXY(p1x, 0, dir + Math.PI / 6);
            double[] p2 = rotateXY(p2x, 0, dir - Math.PI / 6);
            gc.strokeLine(toCanvasX(x), toCanvasY(y), toCanvasX(x + p1[0]), toCanvasY(y + p1[1]));
            gc.strokeLine(toCanvasX(x), toCanvasY(y), toCanvasX(x + p2[0]), toCanvasY(y + p2[1]));
        } else if (progress <= 2) { // strike
            progress -= 1;

            // Arms
            gc.setStroke(Color.GREY);
            double p1x = 0.45;
            double p2x = 0.45;
            double[] p1 = rotateXY(p1x, 0, dir + Math.PI / 6);
            double[] p2 = rotateXY(p2x, 0, dir - Math.PI / 6);
            gc.strokeLine(toCanvasX(x), toCanvasY(y), toCanvasX(x + p1[0]), toCanvasY(y + p1[1]));
            gc.strokeLine(toCanvasX(x), toCanvasY(y), toCanvasX(x + p2[0]), toCanvasY(y + p2[1]));

            // Swipe
            gc.setStroke(Color.WHITESMOKE);
            if (progress <= 0.5) {
                progress *= 2;
                gc.strokeArc(toCanvasX(x - range),
                        toCanvasY(y - range),
                        range * 2 * gridSize,
                        range * 2 * gridSize,
                        Math.toDegrees(- dir - Math.PI / 8),
                        45 * progress,
                        ArcType.OPEN);
                gc.strokeArc(toCanvasX(x - range * 0.85),
                        toCanvasY(y - range * 0.85),
                        range * 1.7 * gridSize,
                        range * 1.7 * gridSize,
                        Math.toDegrees(- dir - Math.PI / 8),
                        45 * progress,
                        ArcType.OPEN);
            } else {
                progress = (progress - 0.5) * 2;
                gc.strokeArc(toCanvasX(x - range),
                        toCanvasY(y - range),
                        range * 2 * gridSize,
                        range * 2 * gridSize,
                        Math.toDegrees(- dir + Math.PI / 8),
                        - 45 * progress,
                        ArcType.OPEN);
                gc.strokeArc(toCanvasX(x - range * 0.85),
                        toCanvasY(y - range * 0.85),
                        range * 1.7 * gridSize,
                        range * 1.7 * gridSize,
                        Math.toDegrees(- dir + Math.PI / 8),
                        - 45 * progress,
                        ArcType.OPEN);
            }
        } else { // recover
            progress -= 2;
            gc.setStroke(Color.GREY);
            double p1x = 0.45 - progress * 0.2;
            double p2x = 0.45 - progress * 0.2;
            double[] p1 = rotateXY(p1x, 0, dir + Math.PI / 6);
            double[] p2 = rotateXY(p2x, 0, dir - Math.PI / 6);
            gc.strokeLine(toCanvasX(x), toCanvasY(y), toCanvasX(x + p1[0]), toCanvasY(y + p1[1]));
            gc.strokeLine(toCanvasX(x), toCanvasY(y), toCanvasX(x + p2[0]), toCanvasY(y + p2[1]));
        }

        gc.restore();
    }

    public static void renderShield(double x, double y, double direction, int position) {
        GraphicsContext gc = Main.game.getGc();

        gc.save();
        gc.setStroke(Color.BROWN);
        gc.setLineWidth(4);
        double[] p1;
        double[] p2;
        if (position == 0) { // lowered
            p1 = rotateXY(0.35, 0.2, direction - Math.PI / 3);
            p2 = rotateXY(0.35, -0.2, direction - Math.PI / 3);
        } else { // raised
            p1 = rotateXY(0.4, 0.3, direction);
            p2 = rotateXY(0.4, -0.3, direction);
        }
        gc.strokeLine(toCanvasX(x + p1[0]), toCanvasY(y + p1[1]), toCanvasX(x + p2[0]), toCanvasY(y + p2[1]));
        gc.restore();
    }

    // Coordinate transforms
    static public double[] toCanvas(double x, double y) {
        double[] result = {x * gridSize, y * gridSize};
        return result;
    }

    static public double toCanvasX(double x) {
        return (x - (cameraX - cameraWidth / 2)) * gridSize;
    }

    static public double toCanvasY(double y) {
        return (y - (cameraY - cameraHeight / 2)) * gridSize;
    }

    static public double toWorldX(double x) {
        return (x / gridSize) + (cameraX - cameraWidth / 2);
    }

    static public double toWorldY(double y) {
        return (y / gridSize) + (cameraY - cameraHeight / 2);
    }

    static public double[] rotateXY(double x, double y, double angle) {
        double x1 = x * Math.cos(angle) - y * Math.sin(angle);
        double y1 = x * Math.sin(angle) + y * Math.cos(angle);
        double[] result = {x1, y1};
        return result;
    }

    static public void renderBlock(int x, int y, int type) {
        GraphicsContext gc = Main.game.getGc();
        /**
         * Type:
         * 0 - empty (void)
         * 1 - white (walls)
         * 2 - grey (floor)
         */
        switch (type) {
            case 0:
                gc.setFill(Color.BLACK);
                break;
            case 1:
                gc.setFill(Color.WHITE);
                break;
            case 2:
                gc.setFill(Color.DARKORANGE);
                break;
        }
        gc.fillRect(toCanvasX(x + 0.5), toCanvasY(y + 0.5), gridSize, gridSize);
    }

    // TODO add method for rendering bitmaps using javafx.Image -> WritableImage -> PixelWriter
}
