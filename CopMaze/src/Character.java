public class Character {
	String name;
	int spriteId;
	int healthPoints;
	int nbOwnedGems;
	boolean hasKey;
	Coordinate currentLocation;
	
	public Character(String name, int spriteId) {
		this.name = name;
		this.spriteId = spriteId;
		healthPoints = 3;
		nbOwnedGems = 0;
		hasKey = false;
		currentLocation = new Coordinate(0, 0);
	}
	
	
}

