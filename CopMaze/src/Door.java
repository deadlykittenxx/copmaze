/**
 * This class represents an exit in the maze (model class).
 */

public class Door {
    /**
     * c is the current coordinates of the exit.
     */
	public Coordinate c;

    /**
     * isOpened represents if players opened the exit.
     */
    public boolean isOpened;

    /**
     * Creates a gem starting at some coordinates.
     * @param c the starting coordinates of the exit
     */
    Door(Coordinate c) {
        this.c = c;
        this.isOpened = false;
    }
    
}
