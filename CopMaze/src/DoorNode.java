import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class DoorNode extends ImageView {
	private static final String CLOSED_DOOR_IMAGE_URL = "/resources/image/door.png";
	private static final String OPEN_DOOR_IMAGE_URL = "/resources/image/doorOpened.png";

	private static final Image closedDoorImage = new Image(CLOSED_DOOR_IMAGE_URL);
	private static final Image openDoorImage = new Image(OPEN_DOOR_IMAGE_URL);
	private boolean opened;
	
	public DoorNode(int widthPx, int heightPx) {
		super(CLOSED_DOOR_IMAGE_URL);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
		opened = false;
	}
	
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
