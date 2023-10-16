import java.util.List;

/**
 * This class represents a policeman in the maze (model class).
 */
public class Police {
	/**
	 * c is the current coordinates of the policeman
	 */
	public Coordinate c;
	/**
	 * originC is the origin coordinates of the policeman
	 */
	public Coordinate originC;
	/**
	 * moveTurn is the number of turns the AI policeman has moved
	 */
	public int moveTurn = 0;
	/**
	 * turnShift is a random number that allows the AI to behave differently for each policeman
	 */
	public int turnShift;
	
	/**
	 * Creates a policeman starting at some coordinates.
	 * @param c the starting coordinates of the policeman
	 * @param maxTurnShift the maximum value for the random number turnShift
	 */
    Police(Coordinate c, int maxTurnShift) {
        this.c = c;
        originC = c;
		turnShift = (int) (Math.random() * (maxTurnShift+1));
    }
    
    /**
     * Moves the policeman by one tile in the maze towards the player
     * @param maze the maze in which to move
     * @return the new coordinates of the policeman
     */
    public Coordinate moveToPlayer(Maze maze) {
		List<Coordinate> path = maze.shortestPath(c, maze.getCharacter().currentLocation);
		if (!path.isEmpty()) {
			c = path.get(0);
		}
		return c;
    }
    
    /**
     * Moves the policeman by one tile in the maze towards the origin coordinates of the policeman
     * @param maze the maze in which to move
     * @return the new coordinates of the policeman
     */
    public Coordinate moveToOrigin(Maze maze) {
		List<Coordinate> path = maze.shortestPath(c, originC);
		if (!path.isEmpty()) {
			c = path.get(0);
		}
		return c;
    }

    /**
     * Moves the policeman by one tile in the maze in a random direction
     * @param maze the maze in which to move
     * @return the new coordinates of the policeman
     */
	public Coordinate moveRandom(Maze maze) {
		List<Coordinate> possibleMoves = maze.getValidMoves(c);
		if (possibleMoves.size() > 0) {
			c = possibleMoves.get((int) (Math.random() * possibleMoves.size()));
		}
		return c;
	}
}
