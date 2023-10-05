import java.util.Random;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class Gem {
	Random rand = new Random();
	int width;
	int height;
	
	public class Coordinate{
		int x;
		int y;
	}
	
	ImageView gemImage;
	
	
	Coordinate currentLocation = new Coordinate();
	
	// rand.nextInt(30) 0 ~ 29
	public Gem(String url, int w, int h) {
		currentLocation.x = rand.nextInt(CopMaze.MAZE_WIDTH);
		currentLocation.y = rand.nextInt(CopMaze.MAZE_HEIGHT);
		
		width = w;
		height = h;
		
		gemImage = new ImageView(url);
		gemImage.setFitWidth(w);
		gemImage.setFitHeight(h);
		
	}
	
	public void draw() {
		gemImage.setLayoutX(currentLocation.x*CopMaze.GRID_SIZE);
		gemImage.setLayoutY(currentLocation.y*CopMaze.GRID_SIZE);
	}
	
	
}
