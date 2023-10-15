import java.util.List;

public class Police {
	public Coordinate c;
	public Coordinate originC;
	public int moveTurn = 0;
	public int turnShift;
	
    Police(Coordinate c, int maxTurnShift) {
        this.c = c;
        originC = c;
		turnShift = (int) (Math.random() * (maxTurnShift+1));
    }
    
    public Coordinate moveToPlayer(Maze maze) {
		List<Coordinate> path = maze.shortestPath(c, maze.getCharacter().currentLocation);
		if (!path.isEmpty()) {
			c = path.get(0);
		}
		return c;
    }
    
    public Coordinate moveToOrigin(Maze maze) {
		List<Coordinate> path = maze.shortestPath(c, originC);
		if (!path.isEmpty()) {
			c = path.get(0);
		}
		return c;
    }

	public Coordinate moveRandom(Maze maze) {
		List<Coordinate> possibleMoves = maze.getValidMoves(c);
		if (possibleMoves.size() > 0) {
			c = possibleMoves.get((int) (Math.random() * possibleMoves.size()));
		}
		return c;
	}
}
