/**
 * The Character class represents a character in a game. It includes properties of a character,
 * such as their name, sprite ID, health points, owned gems,
 * possession of a key, and current location (model class).
 */
public class Character {
	/**
	 * The name of the character.
	 */
	String name;

	/**
	 * The sprite ID representing the character's appearance.
	 */
	int spriteId;

	/**
	 * The HP of the character. By default, it's set to 3.
	 */
	int healthPoints;

	/**
	 * The number of gems owned by the character. Initially, it's set to 0.
	 */
	int nbOwnedGems;

	/**
	 * A flag indicating whether the character possesses a key.
	 */
	boolean hasKey;

	/**
	 * The current location of the character.
	 */
	Coordinate currentLocation;

	/**
	 * Constructs a Character object with the specified name and sprite ID. The character's
	 * initial health points are set to 3, the number of owned gems to 0, the possession of a key
	 * to false, and the current location to (0, 0).
	 *
	 * @param name     The name of the character.
	 * @param spriteId The sprite ID representing the character's appearance.
	 */
	public Character(String name, int spriteId) {
		this.name = name;
		this.spriteId = spriteId;
		healthPoints = 3;
		nbOwnedGems = 0;
		hasKey = false;
		currentLocation = new Coordinate(0, 0);
	}
	
	
}

