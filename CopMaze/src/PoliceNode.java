import javafx.animation.PathTransition;
import javafx.scene.shape.Path;

public class PoliceNode extends Sprite{
	private static final String POLICE_IMAGE_URL = "policeSprite.png";
	
	private static final int SPRITE_WIDTH_PX = 128;
	private static final int SPRITE_HEIGHT_PX = 128;
	private static final int FPS = 5;
	
	PathTransition pathTransition;
	Path path = new Path();
	
	public PoliceNode(int widthPx, int heightPx) {
		super(POLICE_IMAGE_URL, SPRITE_WIDTH_PX, SPRITE_HEIGHT_PX);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
		setFPS(FPS);
		play();
	}
	
	
}
