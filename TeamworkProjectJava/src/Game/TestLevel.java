package Game;

import Enumerations.TileType;
import World.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import Renderer.QuickView;

import java.io.File;
import java.util.ArrayList;

/**
 * Test level geometry generation from here. Zoom level is set to very low, so the entire map is visible.
 */
public class TestLevel  extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Setup scene and nodes
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        root.getChildren().add(canvas);

        // Draw grid
        QuickView.gridSize = 5;
        QuickView.drawGrid(gc);

        // Register event handler for key presses
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                // Advance generation
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, 800, 600);
                if (ke.getCode() == KeyCode.SPACE) {
                    Level.CURRENT_LEVEL++;
                    DungeonMaker.mazeHeight *= 2;
                    DungeonMaker.mazeWidth *= 2;
                }
                if (ke.getCode() == KeyCode.ENTER) {
                    // Initialize generator, make rooms within boundaries
                    DungeonMaker map = new DungeonMaker();
                    // Render dungeon
                    map.getLevelTiles().stream().forEach(tile -> {
                        QuickView.setBlock(gc,
                                tile.getX(),
                                tile.getY(),
                                tile.getTileType() == TileType.WALL ? 1 : 2);
                    });
                }
                QuickView.drawGrid(gc);
            }
        });

        // Render stage
        primaryStage.setResizable(false);
        primaryStage.setTitle("Test Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
