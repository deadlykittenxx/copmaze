import java.util.ArrayList;
import java.util.Collections;

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

class Wall {
    public Coordinate c;
    public int location;
    Wall(Coordinate c, int location) {
        this.c = c;
        this.location = location;
    }
}

public class Maze {
    static final int TOP = 8;
    static final int RIGHT = 4;
    static final int BOTTOM = 2;
    static final int LEFT = 1;

    private static int[][] maze;

    public Maze(int width, int height, double easiness) {
        generate(width, height);
        makeMazeEasier(easiness);
        addExit();
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
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                Coordinate c = new Coordinate(i, j);
                if (maze[i][j] == 0) {
                    unconnectedCells.add(c);
                }
            }
        }
        return unconnectedCells;
    }

    private ArrayList<Wall> getNonBorderWalls() {
        ArrayList<Wall> nonBorderWalls = new ArrayList<Wall>();
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                Coordinate c = new Coordinate(i, j);
                if (j > 0 && hasTopWall(i, j)) {
                    nonBorderWalls.add(new Wall(c, TOP));
                }
                if (i > 0 && hasLeftWall(i, j)) {
                    nonBorderWalls.add(new Wall(c, LEFT));
                }
            }
        }
        return nonBorderWalls;
    }

    private void makeMazeEasier(double coefficient) {
        ArrayList<Wall> nonBorderWalls = getNonBorderWalls();
        Collections.shuffle(nonBorderWalls);
        int numWallsToRemove = (int) (coefficient * nonBorderWalls.size());
        for (int i = 0; i < numWallsToRemove; i++) {
            Wall w = nonBorderWalls.get(i);
            Coordinate c = w.c;
            if (w.location == TOP) {
                maze[c.x][c.y] |= TOP;
                maze[c.x][c.y - 1] |= BOTTOM;
            } else { // Left
                maze[c.x][c.y] |= LEFT;
                maze[c.x - 1][c.y] |= RIGHT;
            }
        }
    }

    private ArrayList<Wall> getBorderWalls() {
        ArrayList<Wall> borderWalls = new ArrayList<Wall>();
        for (int i = 0; i < getWidth(); i++) {
            borderWalls.add(new Wall(new Coordinate(i, 0), TOP));
            borderWalls.add(new Wall(new Coordinate(i, getHeight() - 1), BOTTOM));
        }
        for (int j = 0; j < getHeight(); j++) {
            borderWalls.add(new Wall(new Coordinate(0, j), LEFT));
            borderWalls.add(new Wall(new Coordinate(getWidth() - 1, j), RIGHT));
        }
        return borderWalls;
    }

    private void addExit() {
        ArrayList<Wall> borderWalls = getBorderWalls();
        int index = (int) (Math.random() * borderWalls.size());
        Wall w = borderWalls.get(index);
        Coordinate c = w.c;
        if (w.location == TOP) {
            maze[c.x][c.y] |= TOP;
        } else if (w.location == BOTTOM) {
            maze[c.x][c.y] |= BOTTOM;
        } else if (w.location == LEFT) {
            maze[c.x][c.y] |= LEFT;
        } else { // Right
            maze[c.x][c.y] |= RIGHT;
        }
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
        for (int j = 0; j < getHeight(); j++) {
            // Top walls
            for (int i = 0; i < getWidth(); i++) {
                if (hasTopWall(i, j)) {
                    s += "+---";
                } else {
                    s += "+   ";
                }
            }
            s += "+\n";
            // Left walls
            for (int i = 0; i < getWidth(); i++) {
                if (hasLeftWall(i, j)) {
                    s += "|   ";
                } else {
                    s += "    ";
                }
            }
            if (hasRightWall(getWidth() - 1, j)) {
                s += "|\n";
            } else {
                s += " \n";
            }
        }
        // Bottom walls
        for (int i = 0; i < getWidth(); i++) {
            if (hasBottomWall(i, getHeight() - 1)) {
                s += "+---";
            } else {
                s += "+   ";
            }
        }
        s += "+\n";
        return s;
    }
}
