import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Coordinate {
    public int x;
    public int y;
    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class Maze {
    static final int TOP = 8;
    static final int RIGHT = 4;
    static final int BOTTOM = 2;
    static final int LEFT = 1;

    private static int[][] maze;

    public Maze(int width, int height) {
        generate(width, height);
    }

    public int getWidth() {
        return maze.length;
    }

    public int getHeight() {
        return maze[0].length;
    }

    public boolean hasTopWall(int x, int y) {
        return (maze[x][y] & TOP) == 0;
    }

    public boolean hasRightWall(int x, int y) {
        return (maze[x][y] & RIGHT) == 0;
    }

    public boolean hasBottomWall(int x, int y) {
        return (maze[x][y] & BOTTOM) == 0;
    }

    public boolean hasLeftWall(int x, int y) {
        return (maze[x][y] & LEFT) == 0;
    }

    // Generate a maze of size n x m with Wilson's algorithm
    void generate(int n, int m) {
        assert n > 0 && m > 0;
        maze = new int[n][m];
        ArrayList<Coordinate> unconnectedCells = getUnconnectedCells();
        int index = (int) (Math.random() * unconnectedCells.size());
        Coordinate target = unconnectedCells.get(index);
        while (unconnectedCells.size() > 0) {
            Coordinate start;
            do {
                index = (int) (Math.random() * unconnectedCells.size());
                start = unconnectedCells.get(index);
            } while (start.x == target.x && start.y == target.y);
            // The first time: stops at target, then: stops when joining the maze
            ArrayList<Coordinate> path = randomWalk(start, target);
            openPath(path);
            unconnectedCells = getUnconnectedCells();
        }
    }

    // Random walk from start to end with no loops, returns early if joining the maze
    private ArrayList<Coordinate> randomWalk(Coordinate start, Coordinate end) {
        ArrayList<Coordinate> path = new ArrayList<Coordinate>();
        path.add(start);
        Coordinate current = new Coordinate(start.x, start.y);
        // While not joining the maze and not reaching the target
        while (maze[current.x][current.y] == 0 && (current.x != end.x || current.y != end.y)) {
            // Go in one random direction
            int direction = (int) (Math.random() * 4);
            if (direction == 0) {  // Left
                if (current.x > 0) {
                    current.x--;
                }
            } else if (direction == 1) {  // Right
                if (current.x < getWidth()-1) {
                    current.x++;
                }
            } else if (direction == 2) {  // Up
                if (current.y > 0) {
                    current.y--;
                }
            } else {  // Down
                if (current.y < getHeight() - 1) {
                    current.y++;
                }
            }
            // Detect cycles
            for (int i = 0; i < path.size(); i++) {
                Coordinate c = path.get(i);
                if (c.x == current.x && c.y == current.y) {  // If we've already been there
                    // Found a cycle, remove everything from this point
                    path.subList(i, path.size()).clear();  // Remove everything from index i to the end
                    break;
                }
            }
            // Add the new cell to the path
            path.add(new Coordinate(current.x, current.y));
        }
        return path;
    }

    private void openPath(ArrayList<Coordinate> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Coordinate c1 = path.get(i);
            Coordinate c2 = path.get(i + 1);
            if (c1.x == c2.x) {
                if (c1.y > c2.y) {
                    maze[c1.x][c1.y] |= TOP;
                    maze[c2.x][c2.y] |= BOTTOM;
                } else {
                    maze[c1.x][c1.y] |= BOTTOM;
                    maze[c2.x][c2.y] |= TOP;
                }
            } else {
                if (c1.x > c2.x) {
                    maze[c1.x][c1.y] |= LEFT;
                    maze[c2.x][c2.y] |= RIGHT;
                } else {
                    maze[c1.x][c1.y] |= RIGHT;
                    maze[c2.x][c2.y] |= LEFT;
                }
            }
        }
    }

    private ArrayList<Coordinate> getUnconnectedCells() {
        ArrayList<Coordinate> unconnectedCells = new ArrayList<Coordinate>();
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                Coordinate c = new Coordinate(i, j);
                if (maze[i][j] == 0) {
                    unconnectedCells.add(c);
                }
            }
        }
        return unconnectedCells;
    }

	public void draw(GraphicsContext gc, double cellSizePx, double lineWidthPx) {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, cellSizePx*getWidth(), cellSizePx*getHeight());
		gc.setFill(Color.BLACK);

		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (hasTopWall(x, y)) {
                    gc.fillRect(x * cellSizePx, y * cellSizePx, cellSizePx, lineWidthPx);
				}
                if (hasLeftWall(x, y)) {
                    gc.fillRect(x * cellSizePx, y * cellSizePx, lineWidthPx, cellSizePx);
                }
                if (x == getWidth() - 1) {
                    if (hasRightWall(x, y)) {
                        gc.fillRect((x + 1) * cellSizePx, y * cellSizePx, lineWidthPx, cellSizePx);
                    }
                }
                if (y == getHeight() - 1) {
                    if (hasBottomWall(x, y)) {
                        gc.fillRect(x * cellSizePx, (y + 1) * cellSizePx, cellSizePx, lineWidthPx);
                    }
                }
			}
		}
	}

    public String toString() {
        String s = "";
        for (int j = 0; j < maze[0].length; j++) {
            // Top walls
            for (int i = 0; i < maze.length; i++) {
                if (hasTopWall(i, j)) {
                    s += "+--";
                } else {
                    s += "+  ";
                }
            }
            s += "+\n";
            // Left walls
            for (int i = 0; i < maze.length; i++) {
                if (hasLeftWall(i, j)) {
                    s += "|  ";
                } else {
                    s += "   ";
                }
            }
            s += "|\n";
        }
        // Bottom walls
        for (int i = 0; i < maze.length; i++) {
            s += "+--";
        }
        s += "+\n";
        return s;
    }

    public static void main(String[] args) {
        Maze maze = new Maze(7, 5);
        System.out.println(maze);
    }
}
