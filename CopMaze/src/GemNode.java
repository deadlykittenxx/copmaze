import javafx.scene.image.ImageView;

public class GemNode extends ImageView {
	private static final String[] GEM_IMAGES_URLS = {
		"/resources/image/gem01.png",
		"/resources/image/gem02.png",
		"/resources/image/gem03.png"
	};
	public static final int NB_GEM_TYPES = GEM_IMAGES_URLS.length;
	
	public GemNode(int type, int widthPx, int heightPx) {
		super(GEM_IMAGES_URLS[type % GEM_IMAGES_URLS.length]);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
	}
}
