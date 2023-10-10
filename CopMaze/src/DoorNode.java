import javafx.scene.image.ImageView;

public class DoorNode extends ImageView {
	private static final String DOOR_IMAGES_URL = "/resources/image/door.png";
	
	public DoorNode(int widthPx, int heightPx) {
		super(DOOR_IMAGES_URL);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
	}
}
