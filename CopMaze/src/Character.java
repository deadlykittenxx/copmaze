public class Character {
	String name;
	int HP;
	int gems;
	boolean key;
	
	public class Coordinate{
		int x;
		int y;
	}
	
	Coordinate currentLocation = new Coordinate();
	
	public Character(String name) {
		this.name = name;
		HP = 3;
		gems = 0;
		key = false;
		currentLocation.x = 0;
		currentLocation.y = 0;
	}
}

