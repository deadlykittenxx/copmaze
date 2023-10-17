
/**
 * This class represents a gem in the maze (model class).
 */

public class Gem {
    /**
     * c is the current coordinates of the gem.
     */
    public Coordinate c;

    /**
     * originC is the index of the gem.
     */
    public int id;

    /**
     * collected is to show if players get the gem.
     */
    public boolean collected;


    /**
     * Creates a gem starting at some coordinates.
     * @param c the starting coordinates of the gem
     * @param id the index of the gem
     */
    Gem(Coordinate c, int id) {
        this.c = c;
        this.id = id;
        this.collected = false;
    }
}
