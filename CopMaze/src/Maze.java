import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

class Wall {
    public Coordinate c;
    public int location;
    Wall(Coordinate c, int location) {
        this.c = c;
        this.location = location;
    }
}

public class Maze {
    static final int SOUND_LEVEL_POLICE_SCARED = 12000;
    static final int POLICE_GO_TO_PLAYER = 20; // Number of turns during which the police goes to the player
    static final int POLICE_GO_TO_ORIGIN = 10; // Number of turns during which the police goes to their origin
    static final int TOTAL_POLICE_TURNS = POLICE_GO_TO_PLAYER + POLICE_GO_TO_ORIGIN;

    static final int LEFT = 1;
    static final int BOTTOM = 2;
    static final int RIGHT = 4;
    static final int TOP = 8;
    static final int GEM = 16;
    static final int VISITED_LEFT = 32;
    static final int VISITED_BOTTOM = 64;
    static final int VISITED_RIGHT = 128;
    static final int VISITED_TOP = 256;

    private static final int IN_PROGRESS = 0;
    private static final int WON = 1;
    private static final int LOST = 2;


    private int[][] maze;
    private Gem[] gems;
    private Key key;
    private Door door;
    private Character character;
    private Runnable onChangeCallback;
    private Wall exit; 
    private Police[] police;
    private boolean policeActive;
    private ScheduledExecutorService policeAIExecutor;
    private int gameState = IN_PROGRESS;

    private SoundDetector soundDetector = new SoundDetector();
    
    public Maze(int width, int height, double easiness, Character character, int nbGems, int nbPolice) {
        this.character = character;
        this.policeActive = true;
        generate(width, height);
        makeMazeEasier(easiness);
        addExit();
        addGems(nbGems);
        addKey();
        addDoor();
        addPolice(nbPolice);
        soundDetector.start(200);
    }

    public void setOnChangeCallback(Runnable onChangeCallback) {
        this.onChangeCallback = onChangeCallback;
    }

    private void callCallback() {
        if (onChangeCallback != null) {
            onChangeCallback.run();
        }
    }

    public boolean hasWon() {
        return gameState == WON;
    }

    public boolean hasLost() {
        return gameState == LOST;
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

    public List<Coordinate> getValidMoves(Coordinate c) {
        ArrayList<Coordinate> validMoves = new ArrayList<Coordinate>();
        if (c.x > 0 && !hasLeftWall(c.x, c.y)) {
            validMoves.add(new Coordinate(c.x - 1, c.y));
        }
        if (c.x < getWidth() - 1 && !hasRightWall(c.x, c.y)) {
            validMoves.add(new Coordinate(c.x + 1, c.y));
        }
        if (c.y > 0 && !hasTopWall(c.x, c.y)) {
            validMoves.add(new Coordinate(c.x, c.y - 1));
        }
        if (c.y < getHeight() - 1 && !hasBottomWall(c.x, c.y)) {
            validMoves.add(new Coordinate(c.x, c.y + 1));
        }
        return validMoves;
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
        
        exit = w;
    }
    
    public Wall getExit() {
    	return exit;
    }

    private void addGems(int nbGems) {
        gems = new Gem[nbGems];
        int i = 0;
        while (i < nbGems) {
            int x = (int) (Math.random() * getWidth());
            int y = (int) (Math.random() * getHeight());
            
            if ((maze[x][y] & GEM) == 0) {
                maze[x][y] |= GEM;
                gems[i] = new Gem(new Coordinate(x, y), i);
                i++;
            }
        }
    }
    

    public Gem[] getGems() {
        return gems;
    }
  
    
    public int getNumOfGemsLeft() {
    	int num = 0;
    	for (Gem gem : gems) {
    		if (!gem.collected) num++;
    	}
    	return num;
    }

    public boolean collectGem(int x, int y) {
        for (Gem gem : gems) {
            if (gem.c.x == x && gem.c.y == y && !gem.collected) {
                maze[x][y] &= ~GEM;
                gem.collected = true;
                character.nbOwnedGems++;
                if (character.nbOwnedGems == gems.length) {
                	key.visible = true;
                }
                return true;
            }
        }
        return false;
    }
    
    private void addKey() {
    	int x = (int) (Math.random() * getWidth());
        int y = (int) (Math.random() * getHeight());
        
    	key = new Key(new Coordinate(x, y));
    	
    }
    
    public Key getKey() {
    	return key;
    }

    void setKeyCollected(boolean collected) {
        key.collected = collected;
        callCallback();
    }

    private void addDoor() {
    	Wall exit = getExit();
    	
    	door = new Door(new Coordinate(exit.c.x, exit.c.y));
    }
    
    public Door getDoor() {
    	return door;
    }

    public void setDoorOpen(boolean open) {
        door.isOpened = open;
        callCallback();
    }
    
    private void addPolice(int nbPolice) {
        police = new Police[nbPolice];
        int i = 0;
        while (i < nbPolice) {
            int x = (int) (Math.random() * getWidth());
            int y = (int) (Math.random() * getHeight());
            Coordinate playerCoordinate = character.currentLocation;
            
            if ((maze[x][y] & GEM) == 0 &&
              (playerCoordinate.x != x || playerCoordinate.y != y) &&
              (door.c.x != x || door.c.y != y)) {
                police[i] = new Police(new Coordinate(x, y), POLICE_GO_TO_PLAYER-5);
                i++;
            }
        }

        Maze maze = this;

        Runnable movePolice = new Runnable() {
            public void run() {
                double soundLevel = soundDetector.getLevel();
                boolean policeScared = soundLevel >= SOUND_LEVEL_POLICE_SCARED;
                System.out.print("Sound level: " + soundLevel);
                System.out.println(policeScared ? " (police scared)" : "");
                if (policeActive && !policeScared) {
                    for (Police p : police) {
                        int turn = (p.moveTurn + p.turnShift) % TOTAL_POLICE_TURNS;
                        if (turn < POLICE_GO_TO_PLAYER) {
                            p.moveToPlayer(maze);
                        } else {
                            p.moveToOrigin(maze);
                        }
                        p.moveTurn++;
                        if (p.c.x == character.currentLocation.x && p.c.y == character.currentLocation.y) {
                            terminateGame(LOST);
                        }
                    }
                    Platform.runLater(() -> {
                        callCallback();
                    });
                }
            }
        };
    	
        policeAIExecutor = Executors.newScheduledThreadPool(1);
        policeAIExecutor.scheduleAtFixedRate(movePolice, 0, 1, TimeUnit.SECONDS);
    }
    
    public Police[] getPolice() {
    	return police;
    }
    
    public Character getCharacter() {
        return character;
    }

    public boolean moveCharacterUp() {
        if (hasTopWall(character.currentLocation.x, character.currentLocation.y)) {
            return false;
        }
        moveCharacter(0, -1);
        return true;
    }

    public boolean moveCharacterDown() {
        if (hasBottomWall(character.currentLocation.x, character.currentLocation.y)) {
            return false;
        }
        moveCharacter(0, 1);
        return true;
    }

    public boolean moveCharacterLeft() {
        if (hasLeftWall(character.currentLocation.x, character.currentLocation.y)) {
            return false;
        }
        moveCharacter(-1, 0);
        return true;
    }

    public boolean moveCharacterRight() {
        if (hasRightWall(character.currentLocation.x, character.currentLocation.y)) {
            return false;
        }
        moveCharacter(1, 0);
        return true;
    }

    private void moveCharacter(int dx, int dy) {
        assert dx == 0 || dy == 0;
        assert dx == 0 || dx == 1 || dx == -1;
        assert dy == 0 || dy == 1 || dy == -1;
        character.currentLocation.x += dx;
        character.currentLocation.y += dy;
        collectGem(character.currentLocation.x, character.currentLocation.y);
        for (Police p : police) {
            if (p.c.x == character.currentLocation.x && p.c.y == character.currentLocation.y) {
                terminateGame(LOST);
            }
        }
        if (door.isOpened && character.currentLocation.x == door.c.x && character.currentLocation.y == door.c.y) {
            terminateGame(WON);
        }
        callCallback();
    }

    private void terminateGame(int state) {
        gameState = state;
        policeActive = false;
        policeAIExecutor.shutdownNow();
        soundDetector.stop();
        callCallback();
    }

    private void setVisitedLeft(int x, int y) {
        maze[x][y] |= VISITED_LEFT;
    }

    private void setVisitedBottom(int x, int y) {
        maze[x][y] |= VISITED_BOTTOM;
    }

    private void setVisitedRight(int x, int y) {
        maze[x][y] |= VISITED_RIGHT;
    }

    private void setVisitedTop(int x, int y) {
        maze[x][y] |= VISITED_TOP;
    }

    private void setUnvisited(int x, int y) {
        maze[x][y] &= ~(VISITED_LEFT | VISITED_BOTTOM | VISITED_RIGHT | VISITED_TOP);
    }

    private boolean isVisited(int x, int y) {
        return (maze[x][y] & (VISITED_LEFT | VISITED_BOTTOM | VISITED_RIGHT | VISITED_TOP)) != 0;
    }

    private boolean isVisitedLeft(int x, int y) {
        return (maze[x][y] & VISITED_LEFT) != 0;
    }

    private boolean isVisitedBottom(int x, int y) {
        return (maze[x][y] & VISITED_BOTTOM) != 0;
    }

    private boolean isVisitedRight(int x, int y) {
        return (maze[x][y] & VISITED_RIGHT) != 0;
    }

    private boolean isVisitedTop(int x, int y) {
        return (maze[x][y] & VISITED_TOP) != 0;
    }

    public List<Coordinate> shortestPath(Coordinate start, Coordinate end) {
        // Reset visited cells
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                setUnvisited(i, j);
            }
        }

        // Using BFS
        ArrayList<Coordinate> queue = new ArrayList<Coordinate>();
        queue.add(start);
        while (queue.size() > 0) {
            Coordinate current = queue.remove(0);
            if (current.x == end.x && current.y == end.y) {
                break;
            }
            // Add neighbors to the queue
            if (current.x > 0 && !hasLeftWall(current.x, current.y) && !isVisited(current.x - 1, current.y)) {
                queue.add(new Coordinate(current.x - 1, current.y));
                setVisitedRight(current.x - 1, current.y);
            }
            if (current.x < getWidth() - 1 && !hasRightWall(current.x, current.y) && !isVisited(current.x + 1, current.y)) {
                queue.add(new Coordinate(current.x + 1, current.y));
                setVisitedLeft(current.x + 1, current.y);
            }
            if (current.y > 0 && !hasTopWall(current.x, current.y) && !isVisited(current.x, current.y - 1)) {
                queue.add(new Coordinate(current.x, current.y - 1));
                setVisitedBottom(current.x, current.y - 1);
            }
            if (current.y < getHeight() - 1 && !hasBottomWall(current.x, current.y) && !isVisited(current.x, current.y + 1)) {
                queue.add(new Coordinate(current.x, current.y + 1));
                setVisitedTop(current.x, current.y + 1);
            }
        }

        // Reconstruct the path
        List<Coordinate> path = new ArrayList<Coordinate>();
        Coordinate current = end;
        while (current.x != start.x || current.y != start.y) {
            path.add(current);
            if (isVisitedLeft(current.x, current.y)) {
                current = new Coordinate(current.x - 1, current.y);
            } else if (isVisitedRight(current.x, current.y)) {
                current = new Coordinate(current.x + 1, current.y);
            } else if (isVisitedTop(current.x, current.y)) {
                current = new Coordinate(current.x, current.y - 1);
            } else if (isVisitedBottom(current.x, current.y)) {
                current = new Coordinate(current.x, current.y + 1);
            }
        }

        Collections.reverse(path);
        return path;
    }
	
    
	// For debugging
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
