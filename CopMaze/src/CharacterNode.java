public class CharacterNode extends Sprite {
	private static final String[] GEM_IMAGES_URLS = {
		"bonnieSprite.png",
		"clydeSprite.png",
	};
	private static final int SPRITE_WIDTH_PX = 128;
	private static final int SPRITE_HEIGHT_PX = 128;
	private static final int FPS = 5;
	public static final int NB_CHARACTER_TYPES = GEM_IMAGES_URLS.length;
	
	public CharacterNode(int type, int widthPx, int heightPx) {
		super(GEM_IMAGES_URLS[type % GEM_IMAGES_URLS.length], SPRITE_WIDTH_PX, SPRITE_HEIGHT_PX);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
		setFPS(FPS);
		play();
	}
}
