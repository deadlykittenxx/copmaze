import javafx.scene.image.ImageView;

/**
 * This class represents a custom JavaFX ImageView for displaying a key image.
 * It extends the JavaFX ImageView class.
 */
public class KeyNode extends ImageView {

	/**
	 * The URL of the image resource representing a key.
	 */
	private static final String KEY_IMAGES_URL = "/resources/image/key.png";

	/**
	 * Constructs a KeyNode with the specified width and height in pixels.
	 *
	 * @param widthPx  The width of the door image in pixels.
	 * @param heightPx The height of the door image in pixels.
	 */
	public KeyNode(int widthPx, int heightPx) {
		super(KEY_IMAGES_URL);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
	}
}
