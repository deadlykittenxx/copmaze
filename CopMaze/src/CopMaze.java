import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CopMaze extends Application {
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	
	private static final int MAZE_WIDTH = 400;
    private static final int MAZE_HEIGHT = 400;
    private static final int GRID_SIZE = 20;
    
    private int numRows;
    private int numCols;
    private Cell[][] grid;
    
    
	public Scene characterScene;
	public Scene levelScene;
	public Scene mazeScene;
	public Scene ruleScene;
	public EventHandler<ActionEvent> btnStartListener;
	public EventHandler<ActionEvent> btnCharacterListener;
	public EventHandler<ActionEvent> btnLevelListener;
	public EventHandler<ActionEvent> btnNextListener;
	public EventHandler<ActionEvent> btnHowtoPlayListener;
	public Text Ruletxt;
	public String[] RuleContent = new String[5];
	public int count = 0;
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
	
		VBox root = new VBox();
		Scene scene = new Scene(root);
		root.setAlignment(Pos.CENTER);
		
		VBox characterRoot = new VBox();
		characterScene = new Scene(characterRoot);
		characterRoot.setAlignment(Pos.CENTER);
		
		VBox levelRoot = new VBox();
		levelScene = new Scene(levelRoot);
		levelRoot.setAlignment(Pos.CENTER);
		
		VBox mazeRoot = new VBox();
		mazeScene = new Scene(mazeRoot);
		
		VBox ruleRoot = new VBox();
		ruleScene = new Scene(ruleRoot);
		ruleRoot.setAlignment(Pos.CENTER);
		
		initListener(primaryStage);
		
		initGUI(root);
		characterGUI(characterRoot);
		levelGUI(levelRoot);
		mazeGUI(mazeRoot);
		ruleGUI(ruleRoot);
		
		primaryStage.setTitle("Cop Maze"); 
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
	/*
	 * 	Initialize the listeners
	 */
	public void initListener(Stage stage) {
		btnLevelListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stage.setScene(mazeScene);
			}
		};
		
		btnStartListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stage.setScene(characterScene);
			}
		};
		
		btnCharacterListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				/*
				 * 	Create new Character
				 */
				Button btn = (Button) event.getSource();
				Character player = new Character(btn.getText());
				stage.setScene(levelScene);
			}
		};
		
		btnHowtoPlayListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.setScene(ruleScene);
			}
		};
		
		btnNextListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(count < 4) {
					count++;
					Ruletxt.setText(RuleContent[count]);
				}
				
			}
		};
		
	}
	
	public void mazeGUI(VBox root) {
		numRows = MAZE_HEIGHT / GRID_SIZE;
        numCols = MAZE_WIDTH / GRID_SIZE;
        
        grid = new Cell[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
        
        generateMaze();

        Canvas canvas = new Canvas(MAZE_WIDTH, MAZE_HEIGHT);
        drawMaze(canvas.getGraphicsContext2D());

        
        root.getChildren().add(canvas);
        
	}
	
	public void levelGUI(VBox root) {
		Label label = new Label("Choose Your Level !"); 
		label.setPadding(new Insets(20, 50, 50, 50));
		
		Button btnEasy = new Button("Easy");
		Button btnHard = new Button("Hard");
		Button btnSuperHard = new Button("Super Hard");
		
		root.getChildren().addAll(label, btnEasy, btnHard, btnSuperHard);
		
		btnEasy.setOnAction(btnLevelListener);
		btnHard.setOnAction(btnLevelListener);
		btnSuperHard.setOnAction(btnLevelListener);
	}
	
	
	/*
	 * 	Choose Character
	 */
	
	public void characterGUI(VBox root) {
		Label label = new Label("Choose Your Character !"); 
		label.setPadding(new Insets(20, 50, 50, 50));
		
		Button btnJhonny = new Button("Johnny");
		Button btnSarah = new Button("Sarah");
		
		root.getChildren().addAll(label, btnJhonny, btnSarah);
		
		btnJhonny.setOnAction(btnCharacterListener);
		btnSarah.setOnAction(btnCharacterListener);
	}
	
	
	
	/*
	 * 	Initalize the first Scene
	 */
	public void initGUI(Pane root) {
		Label label = new Label("Cop Maze");
		label.setPadding(new Insets(20, 50, 50, 50));
		
		Button btnHowtoPlay = new Button("How to Play");
		Button btnStart = new Button("Start");
		
		btnHowtoPlay.setOnAction(btnHowtoPlayListener);
		btnStart.setOnAction(btnStartListener);
		
		root.getChildren().addAll(label, btnHowtoPlay, btnStart);
		
	}
	
	
    private void generateMaze() {
        Stack<Cell> stack = new Stack<>();
        Cell current = grid[0][0];
        current.visited = true;

        while (true) {
            List<Cell> neighbors = getUnvisitedNeighbors(current);
            if (!neighbors.isEmpty()) {
                Cell neighbor = neighbors.get((int) (Math.random() * neighbors.size()));
                removeWall(current, neighbor);
                stack.push(current);
                current = neighbor;
                current.visited = true;
            } else if (!stack.isEmpty()) {
                current = stack.pop();
            } else {
                break;
            }
        }
    }
    
    private List<Cell> getUnvisitedNeighbors(Cell cell) {
        int row = cell.row;
        int col = cell.col;
        List<Cell> neighbors = new ArrayList<>();

        if (row > 1 && !grid[row - 2][col].visited) {
            neighbors.add(grid[row - 2][col]);
        }
        if (row < numRows - 2 && !grid[row + 2][col].visited) {
            neighbors.add(grid[row + 2][col]);
        }
        if (col > 1 && !grid[row][col - 2].visited) {
            neighbors.add(grid[row][col - 2]);
        }
        if (col < numCols - 2 && !grid[row][col + 2].visited) {
            neighbors.add(grid[row][col + 2]);
        }

        Collections.shuffle(neighbors);
        return neighbors;
    }

    private void removeWall(Cell current, Cell neighbor) {
        int rowDiff = neighbor.row - current.row;
        int colDiff = neighbor.col - current.col;
        int wallRow = current.row + rowDiff / 2;
        int wallCol = current.col + colDiff / 2;
        grid[wallRow][wallCol].visited = true;
    }
    
    private void drawMaze(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, MAZE_WIDTH, MAZE_HEIGHT);
        gc.setFill(Color.WHITE);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!grid[row][col].visited) {
                    gc.fillRect(col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE, GRID_SIZE);
                }
            }
        }
    }


	public void ruleGUI (Pane root) {
		Label label = new Label("How to play!");
		label.setPadding(new Insets(20, 50, 50, 50));
		
		Rectangle rectangle = new Rectangle(400, 300);
		rectangle.setArcHeight(30);
		rectangle.setArcWidth(30);
		rectangle.setStroke(Color.TRANSPARENT);
		rectangle.setStyle("-fx-fill: white; -fx-opacity: 0.5;");
		
		RuleContent[0] = "1. Move around the maze and collect all the gems.";
		RuleContent[1] = "2. You should avoid policmen while moving around.";
		RuleContent[2] = "3. When the key appears, go get it.";
		RuleContent[3] = "4. Drag the key and drop to the door to unlock it.";
		RuleContent[4] = "5. Escape through the opened door.";
		
		Ruletxt = new Text();
		Ruletxt.setText(RuleContent[0]);
		
		StackPane stack = new StackPane();
		stack.getChildren().addAll(rectangle, Ruletxt);
		
		Button btnNext = new Button("Next");
		btnNext.setAlignment(Pos.CENTER_RIGHT);
		
		btnNext.setOnAction(btnNextListener);
		
		root.getChildren().addAll(label, stack, btnNext);
		
	}
	
	
}