
/**
 * This class represents a key in the maze (model class).
 */

public class Key {
    /**
     * c is the current coordinates of the key.
     */
	public Coordinate c;

    /**
     * collected is to show if players get the key.
     */
    public boolean collected = false;

    /**
     * visible is to show if key is actually visible in the maze.
     */
    public boolean visible = false;

    /**
     * Creates a key starting at some coordinates.
     * @param c the starting coordinates of the key.
     */
    Key(Coordinate c) {
        this.c = c;
    }
}
