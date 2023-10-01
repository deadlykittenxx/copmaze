import java.util.ArrayList;

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
        path.add(new Coordinate(start.x, start.y));
        // While not joining the maze and not reaching the target
        while (maze[start.x][start.y] == 0 && (start.x != end.x || start.y != end.y)) {
            // Go in one random direction
            int direction = (int) (Math.random() * 4);
            if (direction == 0) {
                if (start.x > 0) {
                    start.x--;
                }
            } else if (direction == 1) {
                if (start.x < maze.length - 1) {
                    start.x++;
                }
            } else if (direction == 2) {
                if (start.y > 0) {
                    start.y--;
                }
            } else {
                if (start.y < maze[0].length - 1) {
                    start.y++;
                }
            }
            // Detect cycles
            for (int i = 0; i < path.size(); i++) {
                Coordinate c = path.get(i);
                if (c.x == start.x && c.y == start.y) {
                    // Found a cycle, remove everything from this point
                    path.subList(i, path.size()).clear();
                    break;
                }
            }
            // Add the new cell to the path
            path.add(new Coordinate(start.x, start.y));
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
