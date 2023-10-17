import javafx.animation.PathTransition;
import javafx.scene.shape.Path;

/**
 * This class represents a custom JavaFX ImageView for displaying a police image.
 * It extends the JavaFX Sprite class.
 */
public class PoliceNode extends Sprite{

	/**
	 * The URL of the sprite resource representing a character.
	 */
	private static final String POLICE_IMAGE_URL = "policeSprite.png";

	/**
	 * The width of the sprite resource.
	 */
	private static final int SPRITE_WIDTH_PX = 128;

	/**
	 * The height of the sprite resource.
	 */
	private static final int SPRITE_HEIGHT_PX = 128;

	/**
	 * Frames per second for sprite animation.
	 */
	private static final int FPS = 5;

	/**
	 * An instance of PathTransition for animating the movement of the police character.
	 */
	PathTransition pathTransition;

	Path path = new Path();

	/**
	 * Constructs a PoliceNode with the specified width and height in pixels.
	 *
	 * @param widthPx  The width of the police sprite in pixels.
	 * @param heightPx The height of the police sprite in pixels.
	 */
	public PoliceNode(int widthPx, int heightPx) {
		super(POLICE_IMAGE_URL, SPRITE_WIDTH_PX, SPRITE_HEIGHT_PX);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
		setFPS(FPS);
		play();
	}
	
	
}
