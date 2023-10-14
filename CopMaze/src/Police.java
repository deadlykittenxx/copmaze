import java.util.List;

public class Police {
	public Coordinate c;
	
    Police(Coordinate c) {
        this.c = c;
        
    }
    
    public Coordinate moveSmart(Maze maze) {
		List<Coordinate> path = maze.shortestPath(c, maze.getCharacter().currentLocation);
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
