import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * A JavaFX Pane representing a visual display of a maze with interactive elements.
 * This class provides a graphical representation of the maze, character, gems, police,
 * key, and door. It also handles user interaction and updates to the maze state.
 */
public class MazeNode extends Pane {
	 /**
     * The Maze object associated with this MazeNode.
     */
    private Maze maze;
    
    /**
     * The size of each maze cell in pixels.
     */
    private double cellSizePx;
    
    /**
     * The width of the maze cell's border line in pixels.
     */
    private double lineWidthPx;
    
    /**
     * The size of the cell content (excluding the border) in pixels.
     */
    private double cellContentPx;
    
    /**
     * An array of GemNode objects representing gems in the maze.
     */
    private GemNode[] gemNodes;
    
    /**
     * An array of PoliceNode objects representing police in the maze.
     */
    private PoliceNode[] policeNodes;
    
    /**
     * The KeyNode representing the key in the maze.
     */
    private KeyNode keyNode;
    
    /**
     * The DoorNode representing the door in the maze.
     */
    private DoorNode doorNode;
    
    /**
     * The CharacterNode representing the character in the maze.
     */
    private CharacterNode characterNode;
    
    /**
     * The canvas used for drawing the maze and its elements.
     */
    private Canvas canvas;
    
    /**
     * The content pane within the MazeNode for adding visual elements.
     */
    private Pane mazeContentPane;
    
    /**
     * Constructs a MazeNode with the provided maze, cell size, and line width.
     *
     * @param maze         The Maze object to be displayed.
     * @param cellSizePx   The size of each maze cell in pixels.
     * @param lineWidthPx  The width of the maze cell's border line in pixels.
     */
    public MazeNode(Maze maze, double cellSizePx, double lineWidthPx) {
        this.canvas = new Canvas(maze.getWidth() * cellSizePx + lineWidthPx, maze.getHeight() * cellSizePx + lineWidthPx);
        mazeContentPane = new Pane();
        this.maze = maze;
        this.cellSizePx = cellSizePx;
        this.lineWidthPx = lineWidthPx;
        this.cellContentPx = cellSizePx - lineWidthPx;
        generateCharacterNode();
        generateGemNodes();
        generateDoorNode();
        generatePoliceNodes();
        generateKeyNode();
        draw();
        this.getChildren().addAll(canvas, mazeContentPane);
        
        this.maze.setOnChangeCallback(() -> {
            update();
            if (maze.hasWon()) {          // when the character reaches the exit
            	System.out.println("YOU WIN");
            	AlertDialog.display("YOU WIN");
            } else if (maze.hasLost()) {  // when the character bump into police
            	System.out.println("YOU LOSE");
            	AlertDialog.display("YOU LOSE");
            }
        });
    }
    
    /**
     * Generates and initializes the graphical representation of the character within the maze.
     * The character node is created with a specific sprite and positioned within the maze's grid.
     *
     * @see CharacterNode
     */
    private void generateCharacterNode() {
        Character character = maze.getCharacter();
        characterNode = new CharacterNode(character.spriteId, (int)cellContentPx, (int)cellContentPx);
        characterNode.setX(character.currentLocation.x * cellSizePx + lineWidthPx);
        characterNode.setY(character.currentLocation.y * cellSizePx + lineWidthPx);
        mazeContentPane.getChildren().add(characterNode);
    }
    
    /**
     * Updates the graphical representation of the character's position within the maze.
     * This method is called to reposition the character node according to the character's
     * current location in the maze.
     */
    public void updateCharacterNode() {
        Character character = maze.getCharacter();
        characterNode.setX(character.currentLocation.x * cellSizePx + lineWidthPx);
        characterNode.setY(character.currentLocation.y * cellSizePx + lineWidthPx);
    }
    
    /**
     * Generates and initializes the graphical representation of gems within the maze.
     * This method creates gem nodes for each gem in the maze, sets their initial positions,
     * and adds them to the maze content pane. Each gem node's visibility is determined by
     * whether the corresponding gem has been collected.
     */
    private void generateGemNodes() {
        Gem[] gems = maze.getGems();
        gemNodes = new GemNode[gems.length];
        for (int i = 0; i < gems.length; i++) {
            gemNodes[i] = new GemNode(i, (int)cellContentPx, (int)cellContentPx);
            gemNodes[i].setX(gems[i].c.x * cellSizePx + lineWidthPx);
            gemNodes[i].setY(gems[i].c.y * cellSizePx + lineWidthPx);
            gemNodes[i].setVisible(!gems[i].collected);
            mazeContentPane.getChildren().add(gemNodes[i]);
        }
    }
    
    /**
     * Generates and initializes the graphical representation of the key within the maze.
     * This method creates a key node, sets its initial position, and adds it to the maze content pane.
     * The key node's visibility is determined by whether the key is visible and not collected.
     * Additionally, it adds drag-and-drop functionality to the key node for interaction with the door.
     */
    private void generateKeyNode() {
    	Key key = maze.getKey();
    	keyNode = new KeyNode((int)cellContentPx-2, (int)cellContentPx-6);
    	keyNode.setX(key.c.x * cellSizePx + lineWidthPx);
    	keyNode.setY(key.c.y * cellSizePx + lineWidthPx + 4);
    	keyNode.setVisible(key.visible && !key.collected);
    	mazeContentPane.getChildren().add(keyNode);
            	
        /* make key draggable */
        keyNode.setOnDragDetected((MouseEvent event) -> {
            System.out.println("Key drag detected");

            Dragboard db = keyNode.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString("door open");



            db.setContent(content);
        });
        keyNode.setOnMouseDragged((MouseEvent event) -> {
            event.setDragDetect(true);
        });
    	
    }
    
    /**
     * Generates and initializes graphical representations of police officers within the maze.
     * This method creates police nodes for each police officer in the maze, sets their initial positions,
     * and adds them to the maze content pane. The visibility and position of police nodes are determined by the
     * police officers' coordinates. These graphical representations are updated to match the positions of police
     * officers in the maze.
     */
    private void generatePoliceNodes() {
    	Police[] police = maze.getPolice();

        policeNodes = new PoliceNode[police.length];
        for (int i = 0; i < police.length; i++) {
            policeNodes[i] = new PoliceNode((int)cellContentPx, (int)cellContentPx);
            policeNodes[i].setX(police[i].c.x * cellSizePx + lineWidthPx);
            policeNodes[i].setY(police[i].c.y * cellSizePx + lineWidthPx);
            mazeContentPane.getChildren().add(policeNodes[i]);
        }
    }
    
    /**
     * Generates and initializes a graphical representation of the door within the maze.
     * This method creates a door node to represent the door in the maze, sets its initial position, and adds it
     * to the maze content pane. The visibility and state of the door node (opened or closed) are determined by
     * the state of the door within the maze. It also sets up drag-and-drop functionality for the door node to
     * allow interaction with the key node for opening the door.
     */
    private void generateDoorNode() {
    	Door door = maze.getDoor();
    	doorNode = new DoorNode((int)cellContentPx-10, (int)cellContentPx);
    	doorNode.setX(door.c.x * cellSizePx + lineWidthPx);
    	doorNode.setY(door.c.y * cellSizePx + lineWidthPx);
        doorNode.setOpened(door.isOpened);
    	mazeContentPane.getChildren().add(doorNode);
    	
    	doorNode.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getGestureSource() != doorNode && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
			}
        });
    	
    	doorNode.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                System.out.println("Dropped: " + db.getString());
                maze.setDoorOpen(true);
                maze.setKeyCollected(true);
                event.setDropCompleted(true);

            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }
    
    /**
     * Updates the graphical representation of gems within the maze.
     * This method adjusts the positions and visibility of gem nodes based on the state of gems in the maze.
     */
    private void updateGemNodes() {
        Gem[] gems = maze.getGems();
        for (int i = 0; i < gems.length; i++) {
            gemNodes[i].setX(gems[i].c.x * cellSizePx + lineWidthPx);
            gemNodes[i].setY(gems[i].c.y * cellSizePx + lineWidthPx);
            gemNodes[i].setVisible(!gems[i].collected);
        }
    }
    
    /**
     * Updates the graphical representation of police nodes within the maze.
     * This method adjusts the positions of police nodes based on the current positions of police in the maze.
     */
    private void updatePoliceNodes() {
        Police[] police = maze.getPolice();
        for (int i = 0; i < police.length; i++) {
            policeNodes[i].setX(police[i].c.x * cellSizePx + lineWidthPx);
            policeNodes[i].setY(police[i].c.y * cellSizePx + lineWidthPx);
        }

    }
    
    /**
     * Updates the graphical representation of the key node within the maze.
     * This method adjusts the position and visibility of the key node based on the state of the key in the maze.
     */
    private void updateKeyNode() {
        Key key = maze.getKey();
        keyNode.setX(key.c.x * cellSizePx + lineWidthPx);
        keyNode.setY(key.c.y * cellSizePx + lineWidthPx);
        keyNode.setVisible(key.visible && !key.collected);
    }
    
    /**
     * Updates the graphical representation of the door node within the maze.
     * This method adjusts the position and state (opened or closed) of the door node based on the state of the door in the maze.
     */
    private void updateDoorNode() {
        Door door = maze.getDoor();
    	doorNode.setX(door.c.x * cellSizePx + lineWidthPx);
    	doorNode.setY(door.c.y * cellSizePx + lineWidthPx);
        doorNode.setOpened(door.isOpened);
    }

/*
    public void oldUpdatePoliceNodes() {
    	Police police = maze.getPolice();
        final boolean[] shouldPause = {false};
    	
    	Thread thread = new Thread() {
			@Override
			public void run() {
				
				Coordinate newPos = new Coordinate(police.c.x, police.c.y);

				while(!police.stop) {
					Platform.runLater(()->{

						int rand = (int)(Math.random()*4);
						if (rand == 0) { newPos.x = police.c.x + 1; newPos.y = police.c.y; }
						else if (rand == 1) { newPos.x = police.c.x - 1; newPos.y = police.c.y; }
						else if (rand == 2) { newPos.y = police.c.y + 1; newPos.x = police.c.x; }
						else if (rand == 3) { newPos.y = police.c.y - 1; newPos.x = police.c.x; }
						
						
						if (newPos.x > maze.getWidth()-1) {}
						else if (newPos.x < 0) {}
						else if (newPos.y > maze.getHeight()-1) {}
						else if (newPos.y < 0) {}
						else {
							police.c.x = newPos.x;
							police.c.y = newPos.y;
							policeNode.setX(police.c.x * cellSizePx + lineWidthPx);
					    	policeNode.setY(police.c.y * cellSizePx + lineWidthPx);
						}
						
                        if(soundDetector.soundLevel > 5000.0) {
                            shouldPause[0] = true;
                        }

                        if (shouldPause[0]) {
                            try {
                                Thread.sleep(5000); // Sleep for 5 seconds
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            shouldPause[0] = false;
                        }

					});
					try { Thread.sleep(700); } catch (InterruptedException e) {}
				}
			};
		};
		thread.setDaemon(true);
		thread.start();
    }
*/

    /**
     * Updates the entire graphical representation of the maze.
     * This method updates the character, gems, police, key, and door nodes within the maze based on their current states.
     */
    public void update() {
        updateCharacterNode();
        updateGemNodes();
        updatePoliceNodes();
        updateKeyNode();
        updateDoorNode();
    }

    /**
     * Retrieves the maze object associated with this MazeNode.
     *
     * @return The maze object that this MazeNode represents.
     */
    public Maze getMaze() {
        return maze;
    }
    
    /**
     * Redraws the graphical representation of the maze.
     * This method clears and redraws the maze, including its walls and other components, on the canvas.
     */
	public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, cellSizePx*maze.getWidth() + lineWidthPx, cellSizePx*maze.getHeight() + lineWidthPx);
		gc.setFill(Color.BLACK);

		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {
				if (maze.hasTopWall(x, y)) {
                    gc.fillRect(x * cellSizePx, y * cellSizePx, cellSizePx, lineWidthPx);
				}
                if (maze.hasLeftWall(x, y)) {
                    gc.fillRect(x * cellSizePx, y * cellSizePx, lineWidthPx, cellSizePx);
                }
                if (x == maze.getWidth() - 1) {
                    if (maze.hasRightWall(x, y)) {
                        gc.fillRect((x + 1) * cellSizePx, y * cellSizePx, lineWidthPx, cellSizePx);
                    }
                }
                if (y == maze.getHeight() - 1) {
                    if (maze.hasBottomWall(x, y)) {
                        gc.fillRect(x * cellSizePx, (y + 1) * cellSizePx, cellSizePx, lineWidthPx);
                    }
                }
			}
		}
	}

	
}
