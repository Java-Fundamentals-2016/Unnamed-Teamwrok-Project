package Renderer;

import Enumerations.GameState;
import Game.Main;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserInterface {

    static public Image menuCursor;

    public static void loadMouseImage(){
        InputStream is = null;
        try {
            is = Files.newInputStream(Paths.get("resources/cursor.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image img = new Image(is);
        menuCursor = img;
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void renderMenuBackground() {
        GraphicsContext gc = Main.game.getGc();
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,1100,600);
    }
    static public void renderTitle(double x, double y) {
        GraphicsContext gc = Main.game.getGc();
        gc.setFill(Color.GRAY);
        String t = "GRAVE ROBBER";
        gc.setFont(Font.font(68));
        gc.fillText("GRAVE ROBBER",x,y);
    }

    static public void renderStartButton(double x, double y) {
        GraphicsContext gc = Main.game.getGc();
        double mouseX = Main.game.getPlayer().getMouseX();
        double mouseY = Main.game.getPlayer().getMouseY();
        if (mouseX >= x && mouseX <= x + 200 && mouseY >= y && mouseY <= y + 30){
            gc.setFill(Color.RED);
            gc.fillRoundRect(x,y,200,30,30,30);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(30));
            if (Main.game.getControlState().isMouseLeft()) {
                Main.game.setGameState(GameState.LIVE);
            }
        }
        else{
            gc.setFill(Color.WHITE);
            gc.fillRoundRect(x,y,200,30,30,30);
            gc.setFill(Color.RED);
            gc.setFont(Font.font(30));
        }
        gc.fillText("START",x + 64,y +24,170);
    }

    static public void renderExitButton(double x, double y) {
        GraphicsContext gc = Main.game.getGc();
        double mouseX = Main.game.getPlayer().getMouseX();
        double mouseY = Main.game.getPlayer().getMouseY();
        if (mouseX >= x && mouseX <= x + 200 && mouseY >= y && mouseY <= y + 30){
            gc.setFill(Color.RED);
            gc.fillRoundRect(x,y,200,30,30,30);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(30));
            if (Main.game.getControlState().isMouseLeft()) {
                Platform.exit();
            }
        }
        else{
            gc.setFill(Color.WHITE);
            gc.fillRoundRect(x,y,200,30,30,30);
            gc.setFill(Color.RED);
            gc.setFont(Font.font(30));
        }
        gc.fillText("EXIT",x + 75,y +24,170);
    }

    static public void renderMenuCursor(double x, double y) {
        GraphicsContext gc = Main.game.getGc();

        gc.drawImage(menuCursor, x, y);
    }

}
