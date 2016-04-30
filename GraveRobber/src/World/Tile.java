package World;

import Enumerations.TileType;
import java.util.List;

import Renderer.QuickView;
import javafx.scene.image.Image;

public class Tile {
	/**
	 * Да се създаде клас, който представя квадратно поле с размер 1х1метър от игралната карта. Класа трябва да
	 * съхранява следната информация:
	 * - Пореден номер на полето (без повтаряне)
	 * - Координати на полето
	 * - Плътност (дали може да се преминава през него): solid (плътно), transparent(свободно преминаване, пода на
	 * играланта карта), clip (блокира преминаване на създания, не блокира ефекти)
	 * - Как изглежда полето (име на Sprite или път към изображение на харддиска)
	 * - Допълнителна информация за изобразяването на полето, ако е нужна
	 * <p>
	 * При подадена информация, обектът трябва да може да изпълнява следните функции:
	 * - Да променя своята плътност и/или външен вид
	 * <p>
	 * <p>
	 * Всички полета се съхраняват в масив, който да може да се обхожда и сортира чрез Stream API
	 */

	private int id;
	private static int nextID = 0;
	private int x, y;
	private TileType tileType;
	private Image img;

	public Tile(int x, int y, TileType tileType) {

		this.x = x;
		this.y = y;
		this.id = ++nextID;
		this.tileType = tileType;
		//this.img = setImageTile(tileType); // removed to prevent exception error on missing files
	}

    // TODO: fix paths
	public Image setImageTile(TileType tileType){
		Image img;
		if (tileType == TileType.WALL){
			img = new Image("\\wall.jpg");
		}else{
			img = new Image("\\floor.jpg");
		}
		return img;
	}


	//Setters
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setTileType(TileType tileType) {
		this.tileType = tileType;
	}


	//Getters
	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public TileType getTileType() {
		return tileType;
	}

    public void render() {
        QuickView.renderBlock(x-1, y-1, tileType == TileType.WALL ? 1 : 2);
    }
}
