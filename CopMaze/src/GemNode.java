import javafx.scene.image.ImageView;

/**
 * This class represents a custom JavaFX ImageView for displaying a gem.
 * It extends the JavaFX ImageView class.
 */
public class GemNode extends ImageView {
	/**
	 * The URLs of the image resource representing a gem.
	 */
	private static final String[] GEM_IMAGES_URLS = {
		"/resources/image/gem01.png",
		"/resources/image/gem02.png",
		"/resources/image/gem03.png"
	};

	/**
	 * The total number of different gem types.
	 */
	public static final int NB_GEM_TYPES = GEM_IMAGES_URLS.length;

	/**
	 * Constructs a GemNode with a specific gem type and dimensions.
	 *
	 * @param type      The type of the gem, indicating which image to display.
	 * @param widthPx   The width of the gem image in pixels.
	 * @param heightPx  The height of the gem image in pixels.
	 */
	public GemNode(int type, int widthPx, int heightPx) {
		super(GEM_IMAGES_URLS[type % NB_GEM_TYPES]);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
	}
}
