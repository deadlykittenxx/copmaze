
/**
 * The CharacterNode class represents a custom JavaFX Sprite for displaying character images.
 * It extends the JavaFX Sprite class (view class).
 */
public class CharacterNode extends Sprite {
	/**
	 * The URLs of the image resources representing different character sprites.
	 */
	private static final String[] GEM_IMAGES_URLS = {
		"bonnieSprite.png",
		"clydeSprite.png",
	};

	/**
	 * The width of the character sprite resource in pixels.
	 */
	private static final int SPRITE_WIDTH_PX = 128;

	/**
	 * The height of the character sprite resource in pixels.
	 */
	private static final int SPRITE_HEIGHT_PX = 128;

	/**
	 * FPS for sprite animation.
	 */
	private static final int FPS = 5;

	/**
	 * The total number of different character types available.
	 */
	public static final int NB_CHARACTER_TYPES = GEM_IMAGES_URLS.length;

	/**
	 * Constructs a CharacterNode with the specified character type, width, and height in pixels.
	 *
	 * @param type      The type of character, indicating which image to display.
	 * @param widthPx   The width of the character sprite image in pixels.
	 * @param heightPx  The height of the character sprite image in pixels.
	 */
	public CharacterNode(int type, int widthPx, int heightPx) {
		super(GEM_IMAGES_URLS[type % NB_CHARACTER_TYPES], SPRITE_WIDTH_PX, SPRITE_HEIGHT_PX);
		setFitWidth(widthPx);
		setFitHeight(heightPx);
		setFPS(FPS);
		play();
	}
}
