import javafx.scene.image.ImageView;

public class KeyNode extends ImageView {
	private static final String KEY_IMAGES_URL = "/resources/image/key.png";
	
	public KeyNode(int widthPx, int heightPx) {
		super(KEY_IMAGES_URL);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
	}
}
