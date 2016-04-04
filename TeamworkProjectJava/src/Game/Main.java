package Game;

import Models.Player;
import Models.Sprite;
import World.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import Renderer.QuickView;

import java.util.ArrayList;

public class Main extends Application {

    static ArrayList<Dungeon> dungeonList;
    final static double horizontalRes = 800;
    final static double verticalRes = 600;
    public final static Level level = new Level(new Player(0, 0, 0, new Coord(horizontalRes / 2, verticalRes / 2), true));

    public static void main(String[] args) {
        // Add functionality to display generated map, like a foreach. Use the output of the following method:
        Generator.Generate();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Setup scene and nodes
        Group root = new Group();
        Scene scene = new Scene(root, horizontalRes, verticalRes, Color.BLACK);
        Canvas canvas = new Canvas(horizontalRes, verticalRes);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        root.getChildren().add(canvas);

        final double[] mousePos = {0.0, 0.0};
        final double[] direction = {0.0};
        // Register event handler for key presses
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.W) {
                    level.getPlayer().moveUp();
                }
                else if (ke.getCode() == KeyCode.S) {
                    level.getPlayer().moveDown();
                }
                else if (ke.getCode() == KeyCode.A) {
                    level.getPlayer().moveLeft();
                }
                else if (ke.getCode() == KeyCode.D) {
                    level.getPlayer().moveRight();
                }
            }
        });
        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                mousePos[0] = event.getX();
                mousePos[1] = event.getY();
            }
        });

        final long startNanoTime = System.nanoTime();

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;
                double offsetX = mousePos[0] - level.getPlayer().getX();
                double offsetY = mousePos[1] - level.getPlayer().getY();
                direction[0] = Math.atan(offsetY / offsetX);
                if (offsetX >= 0) direction[0] = direction[0] + Math.PI;

                String text = mousePos[0] + " -> " + offsetX + "\n" + mousePos[1] + " -> " + offsetY;

                // Output
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, 800, 600);
                QuickView.drawGrid(gc);
                QuickView.renderSprite(gc, level.getPlayer().getX(), level.getPlayer().getY(), direction[0]);
                QuickView.renderDot(gc, mousePos[0], mousePos[1]);

                // Debug text
                gc.setFill(Color.WHITE);
                gc.fillText(text, mousePos[0] + 20, mousePos[1]);
            }
        }.start();

        // Render stage
        primaryStage.setResizable(false);
        primaryStage.setTitle("Test Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
