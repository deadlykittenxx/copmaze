import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

/**
 * This class represents a custom JavaFX ImageView for displaying a door image.
 * It extends the JavaFX ImageView class (view class).
 */
public class DoorNode extends ImageView {
	/**
	 * The URL of the image resource representing a closed door.
	 */
	private static final String CLOSED_DOOR_IMAGE_URL = "/resources/image/door.png";

	/**
	 * The URL of the image resource representing a opened door.
	 */
	private static final String OPEN_DOOR_IMAGE_URL = "/resources/image/doorOpened.png";

	/**
	 * The image of a closed door, loaded from the CLOSED_DOOR_IMAGE_URL.
	 */
	private static final Image closedDoorImage = new Image(CLOSED_DOOR_IMAGE_URL);

	/**
	 * The image of a opened door, loaded from the OPENED_DOOR_IMAGE_URL.
	 */
	private static final Image openDoorImage = new Image(OPEN_DOOR_IMAGE_URL);

	/**
	 * A flag indicating whether the door is in an open state.
	 */
	private boolean opened;

	/**
	 * Constructs a DoorNode with the specified width and height in pixels. By default,
	 * the door is created in a closed state.
	 *
	 * @param widthPx  The width of the door image in pixels.
	 * @param heightPx The height of the door image in pixels.
	 */
	public DoorNode(int widthPx, int heightPx) {
		super(CLOSED_DOOR_IMAGE_URL);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
		opened = false;
	}

	/**
	 * Sets the state of the door (open or closed) and updates the displayed image accordingly.
	 *
	 * @param open A flag indicating whether the door should be in the open state.
	 */
	public void setOpened(boolean open) {
		if (open != opened) {
			opened = open;
			if (opened) {
				setImage(openDoorImage);
			} else {
				setImage(closedDoorImage);
			}
		}
	}
}
