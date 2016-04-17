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

public class TestLevel  extends Application {
    // TODO: place level gen and display code here; display is sensitive to grid size (recommended 4-8)
    static ArrayList<Dungeon> dungeonList;
    private DungeonMaker map;

    public static void main(String[] args) {
        // Add functionality to display generated map, like a foreach. Use the output of the following method:
        Generator.Generate();
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

        // First pass
        map = new DungeonMaker();
        // Register event handler for key presses
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                // Advance generation
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, 800, 600);
                QuickView.drawGrid(gc);
                if (ke.getCode() == KeyCode.ENTER) {
                    // Finalize generation, make rooms within boundaries

                    //QuickView.renderDungeon(gc, map.getMaze(), true);
                    gc.setFill(Color.WHITE);
                    map.getLevelTiles().stream().forEach(tile -> {
                        gc.setFill(tile.getTileType() == TileType.WALL ? Color.GREY : Color.WHITE);
                        gc.fillRect(tile.getX() * 2, tile.getY() * 2, 2, 2);
                    });
                }
//                else {
//                    // Iterate dungeon
//                    GenerateDungeon.sampleStep(dungeonList);
//                    QuickView.renderDungeon(gc, dungeonList, false);
//                }
            }
        });

        // Render stage
        primaryStage.setResizable(false);
        primaryStage.setTitle("Test Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
