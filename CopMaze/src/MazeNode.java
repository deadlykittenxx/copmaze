import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class MazeNode extends Pane {
    private Maze maze;
    private double cellSizePx;
    private double lineWidthPx;
    private double cellContentPx;
    private GemNode[] gemNodes;
    private KeyNode keyNode;
    private DoorNode doorNode;
    private PoliceNode policeNode;
    private CharacterNode characterNode;
    private Canvas canvas;
    private Pane mazeContentPane;

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
        generatePoliceNode();
        draw();
        this.getChildren().addAll(canvas, mazeContentPane);
        
        
        
        this.maze.setOnChangeCallback(() -> {
            update();
            
            if(this.maze.getNumOfGemsLeft().equals("0")) {
            	
            	generateKeyNode();
            	
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
           
            // when the cahracter reaches the exit
            if(maze.getDoor().isOpened) {
            	Coordinate c = new Coordinate(maze.getCharacter().currentLocation.x, maze.getCharacter().currentLocation.y);
                Wall exit = maze.getExit();
                if(c.x == exit.c.x && c.y == exit.c.y) {
                	System.out.println("YOU WIN");
                	AlertDialog.display("YOU WIN");
                }
            }
            
            // when the character bump into police
            if (maze.getCharacter().currentLocation.x == maze.getPolice().c.x && maze.getCharacter().currentLocation.y == maze.getPolice().c.y) {
            	System.out.println("YOU LOSE");
            	AlertDialog.display("YOU LOSE");
            }
            
            
        });
        
        updatePoliceNode();
        
    }
    

    private void generateCharacterNode() {
        Character character = maze.getCharacter();
        characterNode = new CharacterNode(character.spriteId, (int)cellContentPx, (int)cellContentPx);
        characterNode.setX(character.currentLocation.x * cellSizePx + lineWidthPx);
        characterNode.setY(character.currentLocation.y * cellSizePx + lineWidthPx);
        mazeContentPane.getChildren().add(characterNode);
    }

    public void updateCharacterNode() {
        Character character = maze.getCharacter();
        characterNode.setX(character.currentLocation.x * cellSizePx + lineWidthPx);
        characterNode.setY(character.currentLocation.y * cellSizePx + lineWidthPx);
    }

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
    
    private void generateKeyNode() {
    	Key key = maze.getKey();
    	keyNode = new KeyNode((int)cellContentPx, (int)cellContentPx);
    	keyNode.setX(key.c.x * cellSizePx + lineWidthPx);
    	keyNode.setY(key.c.y * cellSizePx + lineWidthPx);
    	keyNode.setVisible(!key.collected);
    	mazeContentPane.getChildren().add(keyNode);
    	
    }
    
    private void generatePoliceNode() {
    	Police police = maze.getPolice();
    	
    	policeNode = new PoliceNode((int)cellContentPx, (int)cellContentPx);
    	policeNode.setX(police.c.x * cellSizePx + lineWidthPx);
    	policeNode.setY(police.c.y * cellSizePx + lineWidthPx);
    	mazeContentPane.getChildren().add(policeNode);
    }
    
    private void generateDoorNode() {
    	Door door = maze.getDoor();
    	doorNode = new DoorNode((int)cellContentPx, (int)cellContentPx);
    	doorNode.setX(door.c.x * cellSizePx + lineWidthPx);
    	doorNode.setY(door.c.y * cellSizePx + lineWidthPx);
    	doorNode.setVisible(true);
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
            
            /* DOOR IS OPENED */
            Image doorOpened = new Image("/resources/image/doorOpened.png");
            doorNode.setImage(doorOpened);
            door.isOpened = true;
            
            if (db.hasString()) {
                System.out.println("Dropped: " + db.getString());
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    private void updateGemNodes() {
        Gem[] gems = maze.getGems();
        for (int i = 0; i < gems.length; i++) {
            gemNodes[i].setX(gems[i].c.x * cellSizePx + lineWidthPx);
            gemNodes[i].setY(gems[i].c.y * cellSizePx + lineWidthPx);
            gemNodes[i].setVisible(!gems[i].collected);
        }
        
        
    }

    public void updatePoliceNode(){
    	Police police = maze.getPolice();
    	
    	
    	Thread thread = new Thread() {
			@Override
			public void run() {
				
				Coordinate newPos = new Coordinate(police.c.x, police.c.y);
				while(!police.stop) {
					Platform.runLater(()->{
						int rand = (int)(Math.random()*4);
						if (rand == 0) { newPos.x = police.c.x +1; }
						else if (rand == 1) { newPos.x = police.c.x -1; }
						else if (rand == 2) { newPos.y = police.c.y + 1; }
						else if (rand == 3) { newPos.y = police.c.y - 1; }
						
						
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
						
						
				    	
					});
					try { Thread.sleep(700); } catch (InterruptedException e) {}
				}
			};
		};
		thread.setDaemon(true);
		thread.start();
    	
    }
    
    public void update() {
        updateCharacterNode();
        updateGemNodes();
    }

    
    public Maze getMaze() {
        return maze;
    }
    
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
