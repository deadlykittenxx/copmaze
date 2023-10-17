import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

/**
 * The Wall class represents a wall within the maze.
 */
class Wall {
	/**
     * The coordinates of the wall in the maze.
     */
	public Coordinate c;
	
	/**
     * The location of the wall: LEFT, BOTTOM, RIGHT, or TOP.
     */
	public int location;
	
	 /**
     * Constructs a Wall object with the specified coordinates and location.
     *
     * @param c The coordinates of the wall.
     * @param location The location of the wall (LEFT, BOTTOM, RIGHT, or TOP).
     */
	Wall(Coordinate c, int location) {
		this.c = c;
		this.location = location;
	}
}

/**
 * The Maze class manages the maze game and its elements.
 */
public class Maze {
	/**
	 * Constant representing the sound level at which the police gets scared.
	 */
	static final int SOUND_LEVEL_POLICE_SCARED = 12000;

	/**
	 * Constant representing the minimum distance between the player and the police.
	 */
	static final int MIN_DISTANCE_PLAYER_POLICE = 10;
	
	 /**
     * Constant representing the number of turns during which police characters move towards the player.
     */
	static final int POLICE_GO_TO_PLAYER = 20;
	
	 /**
     * Constant representing number of turns during which police characters move towards their origin.
     */
	static final int POLICE_GO_TO_ORIGIN = 10;
	
	/**
     * Constant representing total number of turns police characters alternate between going to the player and their origin.
     */
	static final int TOTAL_POLICE_TURNS = POLICE_GO_TO_PLAYER + POLICE_GO_TO_ORIGIN;
	
	 /**
     * Constant representing the left wall of a maze cell.
     */
	static final int LEFT = 1;
	
	/**
     * Constant representing the bottom wall of a maze cell.
     */
	static final int BOTTOM = 2;
	
	/**
     * Constant representing the right wall of a maze cell.
     */
	static final int RIGHT = 4;
	
	/**
     * Constant representing the top wall of a maze cell.
     */
	static final int TOP = 8;
	
	/**
     * Constant representing a gem in the maze.
     */
	static final int GEM = 16;
	
	/**
     * Constant representing the left wall as visited during pathfinding.
     */
	static final int VISITED_LEFT = 32;
	
	/**
     * Constant representing the bottom wall as visited during pathfinding.
     */
	static final int VISITED_BOTTOM = 64;
	
	/**
     * Constant representing the right wall as visited during pathfinding.
     */
	static final int VISITED_RIGHT = 128;
	
	/**
     * Constant representing the top wall as visited during pathfinding.
     */
	static final int VISITED_TOP = 256;
	
	/**
	 * Constant representing the game state when the maze is in progress.
	 */
	private static final int IN_PROGRESS = 0;
	
	/**
	 * Constant representing the game state when the player won.
	 */
	private static final int WON = 1;
	
	/**
	 * Constant representing the game state when the player lost.
	 */
	private static final int LOST = 2;
	
	 /**
     * The 2D array representing the structure of the maze.
     */
	private int[][] maze;
	
	/**
     * An array of Gem objects in the maze.
     */
	private Gem[] gems;
	
	 /**
     * The Key object in the maze.
     */
	private Key key;
	
	 /**
     * The Door object in the maze.
     */
	private Door door;
	
	/**
     * The main character in the maze.
     */
	private Character character;
	
	 /**
     * A callback function to handle state changes in the maze.
     */
	private Runnable onChangeCallback;
	
	 /**
     * The exit wall of the maze.
     */
	private Wall exit;
	
	 /**
     * An array of police characters in the maze.
     */
	private Police[] police;

    /**
     * A flag to control the activation of police characters in the maze.
     */
	private boolean policeActive;
	
	/**
     * An executor for controlling the artificial intelligence of police characters.
     */
	private ScheduledExecutorService policeAIExecutor;
	
	/**
     * The current game state: IN_PROGRESS, WON, or LOST.
     */
	private int gameState = IN_PROGRESS;
	
	/**
     * A sound detector used to detect in-game sounds.
     */
	private SoundDetector soundDetector = new SoundDetector();
	
	/**
	 * Constructs a Maze object with the specified parameters.
	 *
	 * @param width The width (number of columns) of the maze.
	 * @param height The height (number of rows) of the maze.
	 * @param easiness A coefficient to adjust the maze difficulty.
	 * @param character The main character in the maze.
	 * @param nbGems The number of gems to place in the maze.
	 * @param nbPolice The number of police characters to place in the maze.
	 */
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

	/**
	 * Sets a callback function to be invoked when the game state changes.
	 *
	 * @param onChangeCallback The callback function to set.
	 */
	public void setOnChangeCallback(Runnable onChangeCallback) {
		this.onChangeCallback = onChangeCallback;
	}
	
	/**
	 * Calls the registered callback function to notify of state changes in the maze.
	 * If a callback function is registered, this method triggers it to handle state changes.
	 * It is typically used to update the UI or respond to game events.
	 */
	private void callCallback() {
		if (onChangeCallback != null) {
			onChangeCallback.run();
		}
	}
	
	/**
     * Determines if the player has won the game.
     *
     * @return True if the player has won, false otherwise.
     */
	public boolean hasWon() {
		return gameState == WON;
	}
	
	/**
     * Determines if the player has lost the game.
     *
     * @return True if the player has lost, false otherwise.
     */
	public boolean hasLost() {
		return gameState == LOST;
	}
	
	/**
     * Gets the width of the maze.
     *
     * @return The width of the maze.
     */
	public int getWidth() {
		return maze.length;
	}
	
	/**
     * Gets the height of the maze.
     *
     * @return The height of the maze.
     */
	public int getHeight() {
		return maze[0].length;
	}
	
	 /**
     * Checks if there is a wall in the top direction at the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if there is a wall in the top direction, false otherwise.
     */
	public boolean hasTopWall(int x, int y) {
		return (maze[x][y] & TOP) == 0;
	}
	
	 /**
     * Checks if there is a wall in the right direction at the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if there is a wall in the right direction, false otherwise.
     */
	public boolean hasRightWall(int x, int y) {
		return (maze[x][y] & RIGHT) == 0;
	}
	
	/**
     * Checks if there is a wall in the bottom direction at the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if there is a wall in the bottom direction, false otherwise.
     */
	public boolean hasBottomWall(int x, int y) {
		return (maze[x][y] & BOTTOM) == 0;
	}
	
	 /**
     * Checks if there is a wall in the left direction at the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if there is a wall in the left direction, false otherwise.
     */
	public boolean hasLeftWall(int x, int y) {
		return (maze[x][y] & LEFT) == 0;
	}

	 /**
     * Generates a maze of the specified dimensions using Wilson's algorithm.
     *
     * @param n The width of the maze.
     * @param m The height of the maze.
     */
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

	/**
	 * Performs a random walk from the start coordinate to the end coordinate with no loops, stopping early if joining the maze.
	 *
	 * @param start The starting coordinate for the random walk.
	 * @param end   The target coordinate for the random walk.
	 * @return An ArrayList of coordinates representing the random walk path.
	 */
	private ArrayList<Coordinate> randomWalk(Coordinate start, Coordinate end) {
		ArrayList<Coordinate> path = new ArrayList<Coordinate>();
		path.add(start);
		Coordinate current = new Coordinate(start.x, start.y);
		// While not joining the maze and not reaching the target
		while (maze[current.x][current.y] == 0 && (current.x != end.x || current.y != end.y)) {
			// Go in one random direction
			int direction = (int) (Math.random() * 4);
			if (direction == 0) { // Left
				if (current.x > 0) {
					current.x--;
				}
			} else if (direction == 1) { // Right
				if (current.x < getWidth() - 1) {
					current.x++;
				}
			} else if (direction == 2) { // Up
				if (current.y > 0) {
					current.y--;
				}
			} else { // Down
				if (current.y < getHeight() - 1) {
					current.y++;
				}
			}
			// Detect cycles
			for (int i = 0; i < path.size(); i++) {
				Coordinate c = path.get(i);
				if (c.x == current.x && c.y == current.y) { // If we've already been there
					// Found a cycle, remove everything from this point
					path.subList(i, path.size()).clear(); // Remove everything from index i to the end
					break;
				}
			}
			// Add the new cell to the path
			path.add(new Coordinate(current.x, current.y));
		}
		return path;
	}
	
	/**
	 * Opens a path between two coordinates by removing walls in the maze.
	 *
	 * @param path A list of coordinates representing the path to be opened.
	 */
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
	
	/**
	 * Retrieves a list of unconnected cells within the maze.
	 *
	 * @return An ArrayList of Coordinate objects representing the unconnected cells.
	 */
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
	
	/**
	 * Retrieves a list of non-border walls within the maze.
	 *
	 * @return An ArrayList of Wall objects representing the non-border walls.
	 */
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
	
	/**
	 * Makes the maze easier by removing a specified number of non-border walls.
	 *
	 * @param coefficient A coefficient specifying the percentage of walls to remove (0.0 to 1.0).
	 */
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
	
	/**
	 * Retrieves a list of valid moves (adjacent coordinates) from a given coordinate in the maze.
	 *
	 * @param c The coordinate for which valid moves are to be determined.
	 * @return An ArrayList of Coordinate objects representing the valid moves from the given coordinate.
	 */
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
	
	/**
	 * Adds an exit point to the maze by removing a wall from the maze's border.
	 * The exit wall location is determined randomly.
	 */
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
	
	 /**
     * Gets the exit wall in the maze.
     *
     * @return The exit wall.
     */
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
	
	/**
     * Gets an array of gems present in the maze.
     *
     * @return An array of gems.
     */
	public Gem[] getGems() {
		return gems;
	}
	
	 /**
     * Gets the number of uncollected gems in the maze.
     *
     * @return The number of uncollected gems.
     */
	public int getNumOfGemsLeft() {
		int num = 0;
		for (Gem gem : gems) {
			if (!gem.collected)
				num++;
		}
		return num;
	}
	
	 /**
     * Collects a gem at the specified coordinates within the maze.
     *
     * @param x The x-coordinate of the gem.
     * @param y The y-coordinate of the gem.
     * @return True if a gem was successfully collected, false otherwise.
     */
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
	
	/**
	 * Adds a key to the maze by placing it at a random location.
	 * The key's location is determined randomly.
	 */
	private void addKey() {
		int x = (int) (Math.random() * getWidth());
		int y = (int) (Math.random() * getHeight());

		key = new Key(new Coordinate(x, y));

	}
	
	 /**
     * Gets the key object in the maze.
     *
     * @return The key object.
     */
	public Key getKey() {
		return key;
	}
	
	/**
     * Sets whether the key has been collected in the maze.
     *
     * @param collected True if the key has been collected, false otherwise.
     */
	void setKeyCollected(boolean collected) {
		key.collected = collected;
		callCallback();
	}
	
	/**
	 * Adds a door to the maze near the exit point, effectively blocking the exit.
	 * The door is placed at the same location as the exit.
	 */
	private void addDoor() {
		Wall exit = getExit();

		door = new Door(new Coordinate(exit.c.x, exit.c.y));
	}
	
	 /**
     * Gets the door object in the maze.
     *
     * @return The door object.
     */
	public Door getDoor() {
		return door;
	}
	
	 /**
     * Sets the state of the door (opened or closed).
     *
     * @param open True if the door is open, false if closed.
     */
	public void setDoorOpen(boolean open) {
		door.isOpened = open;
		callCallback();
	}
	
	/**
	 * Adds police characters to the maze at random positions while respecting specified conditions.
	 *
	 * @param nbPolice The number of police characters to add to the maze.
	 */
	private void addPolice(int nbPolice) {
		police = new Police[nbPolice]; // Create an array to store police characters
		int i = 0;
		while (i < nbPolice) {
			int x = (int) (Math.random() * getWidth()); // Generate a random x-coordinate
			int y = (int) (Math.random() * getHeight()); // Generate a random y-coordinate
			Coordinate playerCoordinate = character.currentLocation;

			int distanceToPlayer = Math.abs(character.currentLocation.x - x)
					+ Math.abs(character.currentLocation.y - y);
			
			// Check conditions for placing police:
			if ((maze[x][y] & GEM) == 0 && (playerCoordinate.x != x || playerCoordinate.y != y)
					&& (door.c.x != x || door.c.y != y) && distanceToPlayer > MIN_DISTANCE_PLAYER_POLICE) {
				police[i] = new Police(new Coordinate(x, y), POLICE_GO_TO_PLAYER - 5);
				i++;
			}
		}

		Maze maze = this;

		Runnable movePolice = new Runnable() {
			public void run() {
				// Calculate sound level and check if police should be scared
				double soundLevel = soundDetector.getLevel();
				boolean policeScared = soundLevel >= SOUND_LEVEL_POLICE_SCARED;
				System.out.print("Sound level: " + soundLevel);
				System.out.println(policeScared ? " (police scared)" : "");
				if (policeActive && !policeScared) {
					 // Move police characters based on their current turn
					for (Police p : police) {
						int turn = (p.moveTurn + p.turnShift) % TOTAL_POLICE_TURNS;
						if (turn < POLICE_GO_TO_PLAYER) {
							p.moveToPlayer(maze);
						} else {
							p.moveToOrigin(maze);
						}
						p.moveTurn++;
						// Check if a police character reaches the player's location
						if (p.c.x == character.currentLocation.x && p.c.y == character.currentLocation.y) {
							terminateGame(LOST);
						}
					}
					 // Update the game state and trigger callbacks
					Platform.runLater(() -> {
						callCallback();
					});
				}
			}
		};
		
		// Create and start a scheduled executor for police movement
		policeAIExecutor = Executors.newScheduledThreadPool(1);
		policeAIExecutor.scheduleAtFixedRate(movePolice, 0, 1, TimeUnit.SECONDS);
	}
	
	 /**
     * Gets an array of police objects in the maze.
     *
     * @return An array of police objects.
     */
	public Police[] getPolice() {
		return police;
	}
	
	 /**
     * Gets the character object in the maze.
     *
     * @return The character object.
     */
	public Character getCharacter() {
		return character;
	}
	
	 /**
     * Moves the character one step up if there is no top wall.
     *
     * @return True if the character successfully moved up, false if blocked by a wall.
     */
	public boolean moveCharacterUp() {
		if (hasTopWall(character.currentLocation.x, character.currentLocation.y)) {
			return false;
		}

		if (character.currentLocation.x == exit.c.x && character.currentLocation.y == exit.c.y) {
			if (character.currentLocation.y == 0)
				return false;
		}
		moveCharacter(0, -1);
		return true;
	}
	
	 /**
     * Moves the character one step down if there is no bottom wall.
     *
     * @return True if the character successfully moved down, false if blocked by a wall.
     */
	public boolean moveCharacterDown() {
		if (hasBottomWall(character.currentLocation.x, character.currentLocation.y)) {
			return false;
		}

		if (character.currentLocation.x == exit.c.x && character.currentLocation.y == exit.c.y) {
			if (character.currentLocation.y == (getHeight() - 1))
				return false;
		}

		moveCharacter(0, 1);
		return true;
	}
	
    /**
     * Moves the character one step left if there is no left wall.
     *
     * @return True if the character successfully moved left, false if blocked by a wall.
     */
	public boolean moveCharacterLeft() {
		if (hasLeftWall(character.currentLocation.x, character.currentLocation.y)) {
			return false;
		}

		if (character.currentLocation.x == exit.c.x && character.currentLocation.y == exit.c.y) {
			if (character.currentLocation.x == 0)
				return false;
		}

		moveCharacter(-1, 0);
		return true;
	}
	
	 /**
     * Moves the character one step right if there is no right wall.
     *
     * @return True if the character successfully moved right, false if blocked by a wall.
     */
	public boolean moveCharacterRight() {
		if (hasRightWall(character.currentLocation.x, character.currentLocation.y)) {
			return false;
		}

		if (character.currentLocation.x == exit.c.x && character.currentLocation.y == exit.c.y) {
			if (character.currentLocation.x == (maze.length - 1))
				return false;
		}

		moveCharacter(1, 0);
		return true;
	}
	
    /**
     * Moves the character by the specified amount in the x and y directions.
     *
     * @param dx The change in the x-coordinate (1 for right, -1 for left, 0 for no change).
     * @param dy The change in the y-coordinate (1 for down, -1 for up, 0 for no change).
     */
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
	
	 /**
     * Terminates the game with the specified state (WON or LOST) and stops police movement.
     *
     * @param state The game state to set (WON or LOST).
     */
	private void terminateGame(int state) {
		gameState = state;
		policeActive = false;
		policeAIExecutor.shutdownNow();
		soundDetector.stop();
		callCallback();
	}
	
	/**
	 * Sets the "visited left" flag for the specified cell in the maze.
	 * This flag is used for maze pathfinding and indicates that the left side of the cell has been visited.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 */
	private void setVisitedLeft(int x, int y) {
		maze[x][y] |= VISITED_LEFT;
	}
	
	/**
	 * Sets the "visited bottom" flag for the specified cell in the maze.
	 * This flag is used for maze pathfinding and indicates that the left side of the cell has been visited.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 */
	private void setVisitedBottom(int x, int y) {
		maze[x][y] |= VISITED_BOTTOM;
	}
	
	/**
	 * Sets the "visited right" flag for the specified cell in the maze.
	 * This flag is used for maze pathfinding and indicates that the left side of the cell has been visited.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 */
	private void setVisitedRight(int x, int y) {
		maze[x][y] |= VISITED_RIGHT;
	}
	
	/**
	 * Sets the "visited top" flag for the specified cell in the maze.
	 * This flag is used for maze pathfinding and indicates that the left side of the cell has been visited.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 */
	private void setVisitedTop(int x, int y) {
		maze[x][y] |= VISITED_TOP;
	}
	
	/**
	 * Clears the "visited" flags for all sides of the specified cell in the maze.
	 * This method resets the visited status of a cell for maze pathfinding.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 */
	private void setUnvisited(int x, int y) {
		maze[x][y] &= ~(VISITED_LEFT | VISITED_BOTTOM | VISITED_RIGHT | VISITED_TOP);
	}
	
	/**
	 * Checks if the specified cell in the maze has been visited.
	 * This method returns true if any side of the cell has been visited; otherwise, it returns false.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 * @return True if the cell has been visited, false otherwise.
	 */
	private boolean isVisited(int x, int y) {
		return (maze[x][y] & (VISITED_LEFT | VISITED_BOTTOM | VISITED_RIGHT | VISITED_TOP)) != 0;
	}

	/**
	 * Checks if the left side of the specified cell in the maze has been visited.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 * @return True if the left side of the cell has been visited, false otherwise.
	 */
	private boolean isVisitedLeft(int x, int y) {
		return (maze[x][y] & VISITED_LEFT) != 0;
	}
	
	/**
	 * Checks if the bottom side of the specified cell in the maze has been visited.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 * @return True if the bottom side of the cell has been visited, false otherwise.
	 */
	private boolean isVisitedBottom(int x, int y) {
		return (maze[x][y] & VISITED_BOTTOM) != 0;
	}
	
	/**
	 * Checks if the right side of the specified cell in the maze has been visited.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 * @return True if the right side of the cell has been visited, false otherwise.
	 */
	private boolean isVisitedRight(int x, int y) {
		return (maze[x][y] & VISITED_RIGHT) != 0;
	}
	
	/**
	 * Checks if the top side of the specified cell in the maze has been visited.
	 *
	 * @param x The x-coordinate of the cell.
	 * @param y The y-coordinate of the cell.
	 * @return True if the top side of the cell has been visited, false otherwise.
	 */
	private boolean isVisitedTop(int x, int y) {
		return (maze[x][y] & VISITED_TOP) != 0;
	}
	
	 /**
     * Finds the shortest path from the specified start coordinate to the end coordinate in the maze.
     *
     * @param start The starting coordinate.
     * @param end   The ending coordinate.
     * @return A list of coordinates representing the shortest path.
     */
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
			if (current.x < getWidth() - 1 && !hasRightWall(current.x, current.y)
					&& !isVisited(current.x + 1, current.y)) {
				queue.add(new Coordinate(current.x + 1, current.y));
				setVisitedLeft(current.x + 1, current.y);
			}
			if (current.y > 0 && !hasTopWall(current.x, current.y) && !isVisited(current.x, current.y - 1)) {
				queue.add(new Coordinate(current.x, current.y - 1));
				setVisitedBottom(current.x, current.y - 1);
			}
			if (current.y < getHeight() - 1 && !hasBottomWall(current.x, current.y)
					&& !isVisited(current.x, current.y + 1)) {
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

	 /**
     * Returns a string representation of the maze for debugging purposes.
     *
     * @return A string representation of the maze.
     */
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
