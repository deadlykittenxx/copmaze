import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CopMaze extends Application {

	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;

	public static final int MAZE_WIDTH = 20; // Number of cells
	public static final int MAZE_HEIGHT = 15; // Number of cells
	public static final int GRID_SIZE = 25; // Number of pixels per cell
	private static final int BORDER_SIZE = 2;
	private static final double EASINESS = 0.2;  // Number from 0 to 1, 1 remove all the walls

	private Scene initScene;
	private Scene characterScene;
	private Scene levelScene;
	private Scene mazeScene;
	private Scene ruleScene;
	private EventHandler<ActionEvent> btnStartListener;
	private EventHandler<ActionEvent> btnHowtoPlayListener;
	private EventHandler<ActionEvent> btnCharacterListener;
	private EventHandler<ActionEvent> btnLevelListener;
	private EventHandler<ActionEvent> btnRuleNextListener;
	private EventHandler<ActionEvent> btnRulePreviousListener;
	private EventHandler<ActionEvent> btnGoBackListener;
	private EventHandler<KeyEvent> characterListener;
	private Button btnGoBack;
	private Button btnRuleNext;
	private Button btnRulePrevious;
	private Label lblRule;
	private String[] contentOfRule = new String[8];
	private Text txtRule;
	private int howToPlayStep = 0;
	public Character player;
	public Button character;
	
	public Maze maze;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox initRoot = new VBox(15);
		initScene = new Scene(initRoot);
		initRoot.setAlignment(Pos.CENTER);

		BorderPane characterRoot = new BorderPane();
		characterScene = new Scene(characterRoot);
		
		BorderPane levelRoot = new BorderPane();
		levelScene = new Scene(levelRoot);
		
		BorderPane ruleRoot = new BorderPane();
		ruleScene = new Scene(ruleRoot);
		
		Pane mazeRoot = new Pane();
		mazeScene = new Scene(mazeRoot);
		//mazeRoot.setAlignment(Pos.CENTER);

		initListener(primaryStage);

		initGUI(initRoot);
		characterGUI(characterRoot);
		levelGUI(levelRoot);
		mazeGUI(mazeRoot);
		ruleGUI(ruleRoot);

		String css = this.getClass().getResource("styles.css").toExternalForm();
		initScene.getStylesheets().add(css);
		characterScene.getStylesheets().add(css);
		levelScene.getStylesheets().add(css);
		mazeScene.getStylesheets().add(css);
		ruleScene.getStylesheets().add(css);

		primaryStage.setTitle("Cop Maze");
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);
		primaryStage.setScene(initScene);
		primaryStage.show();
		
	}

	/*
	 * Initialize the listeners
	 */
	public void initListener(Stage stage) {
		
		/*
		 *	Character moving
		 * */
		characterListener = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if(e.getCode().equals(KeyCode.RIGHT)) {
					if(maze.hasRightWall(player.currentLocation.x, player.currentLocation.y)) {
						
					} else {
						character.setLayoutX(character.getLayoutX()+GRID_SIZE);
						player.currentLocation.x += 1;
					}
				} else if(e.getCode().equals(KeyCode.LEFT)) {
					if(maze.hasLeftWall(player.currentLocation.x, player.currentLocation.y)) {
					} else {
						character.setLayoutX(character.getLayoutX()-GRID_SIZE);
						player.currentLocation.x -= 1;
					}
				} else if(e.getCode().equals(KeyCode.UP)) {
					if(maze.hasTopWall(player.currentLocation.x, player.currentLocation.y)) {
					} else {
						character.setLayoutY(character.getLayoutY()-GRID_SIZE);
						player.currentLocation.y -= 1;
					}
				} else if(e.getCode().equals(KeyCode.DOWN)) {
					if(maze.hasBottomWall(player.currentLocation.x, player.currentLocation.y)) {
					} else {
						character.setLayoutY(character.getLayoutY()+GRID_SIZE);
						player.currentLocation.y += 1;
					}
				}
			}
		};
		
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
				 * Create new Character
				 */
				Button btn = (Button) event.getSource();
				player = new Character(btn.getText());
				stage.setScene(levelScene);
			}
		};

		btnHowtoPlayListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.setScene(ruleScene);
			}
		};

		btnRuleNextListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (howToPlayStep < 7) {
					updateRules(howToPlayStep + 1);
				} else {
					stage.setScene(characterScene);
					howToPlayStep = 0;
				}
			}
		};

		btnRulePreviousListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (howToPlayStep > 0) {
					updateRules(howToPlayStep - 1);
				}
			}
		};
		
		btnGoBackListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stage.setScene(initScene);
				updateRules(0);
			}
		};

	}
	
	/*
	 * Generating BackButtonBar
	 */
	private Pane getBackButtonBar() {
		AnchorPane topPane = new AnchorPane();
		btnGoBack = new Button("◀ Main Menu");
		btnGoBack.setOnAction(btnGoBackListener);
		btnGoBack.setId("BtnGoBack");
		topPane.getChildren().add(btnGoBack);
		topPane.setPrefHeight(30);
		AnchorPane.setTopAnchor(btnGoBack, 25.0);
		AnchorPane.setLeftAnchor(btnGoBack, 25.0);
		return topPane;
	}
	
	/*
	 * Update RuleGUI
	 */
	void updateRules(int step) {
		btnRulePrevious.setVisible(true);
		howToPlayStep = step;
		if (howToPlayStep <= 2) {
			txtRule.setText(contentOfRule[howToPlayStep]);
			if (howToPlayStep == 0) {
				btnRulePrevious.setVisible(false);
				btnRuleNext.setVisible(true);
			}
			if (howToPlayStep < 1) {
				lblRule.setText("How to Play!");
			} else if (howToPlayStep >= 1 && howToPlayStep < 2) {
				lblRule.setText("Game Controls");
				btnRuleNext.setVisible(true);
			} else if (howToPlayStep == 2) {
				lblRule.setText("Be careful!");
				btnRuleNext.setVisible(false);
			}

		}
	}
	
	/*
	 * Game Scene
	 */
	public void mazeGUI(Pane root) {
		root.setId("mazeScene");
		
		maze = new Maze(MAZE_WIDTH, MAZE_HEIGHT, EASINESS, 3);
		MazeNode mazeNode = new MazeNode(maze, GRID_SIZE, BORDER_SIZE);

		character = new Button("◍");
		character.setPrefHeight(GRID_SIZE);
		character.setPrefWidth(GRID_SIZE);
		character.setOnKeyPressed(characterListener);
		
		root.getChildren().addAll(mazeNode, character);
	}
	
	/*
	 * Initalize the first Scene
	 */
	public void initGUI(VBox root) {
		root.setId("initScene");


		Button btnHowtoPlay = new Button("How to Play");
		btnHowtoPlay.setPrefSize(120, 30);
		btnHowtoPlay.setId("btnStyle1");
		btnHowtoPlay.setPadding(new Insets(6, 8, 6, 8));

		Button btnStart = new Button("Start");
		btnStart.setPrefSize(120, 30);
		btnStart.setId("btnStyle1");
		btnStart.setPadding(new Insets(6, 8, 6, 8));

		btnHowtoPlay.setOnAction(btnHowtoPlayListener);
		btnStart.setOnAction(btnStartListener);

		root.getChildren().addAll(btnHowtoPlay, btnStart);
		VBox.setMargin(btnHowtoPlay, new Insets(100, 0, 0, 0));
	}
	
	
	/*
	 * Choose Character
	 */

	public void characterGUI(BorderPane root) {
		root.setId("characterScene");
		VBox menu = new VBox();
		menu.setAlignment(Pos.CENTER);
		Label label = new Label("Choose Your Character!");
		label.setId("subTitle");
		
		
		HBox characterBox = new HBox();
		VBox character1Box = new VBox();
		VBox character2Box = new VBox();
	
		
		Button btnCharacterName1 = new Button("Bonnie");
		Button btnCharacterName2 = new Button("Clyde");
		btnCharacterName1.setPadding(new Insets(0, 25, 15, 25));
		btnCharacterName2.setPadding(new Insets(0, 25, 15, 25));
		btnCharacterName1.setId("btnLabel");
		btnCharacterName2.setId("btnLabel");
		
		Sprite spriteBonnie = new Sprite("bonnieSprite.png", 128, 128); 
		Sprite spriteClyde = new Sprite("clydeSprite.png", 128, 128);
		spriteBonnie.setFPS(5); // animation will play at 5 frames per second
		spriteBonnie.play(); // animates the first row of the sprite sheet
		spriteClyde.setFPS(5);
		spriteClyde.play();
		
		character1Box.getChildren().addAll(spriteBonnie, btnCharacterName1);
		character2Box.getChildren().addAll(spriteClyde, btnCharacterName2);
		characterBox.getChildren().addAll(character1Box, character2Box);
		characterBox.setAlignment(Pos.CENTER);
	
		menu.getChildren().addAll(label, characterBox);
		
		/* Set Margins */
		VBox.setMargin(btnCharacterName1, new Insets(20, 0, 0, 10));
		VBox.setMargin(btnCharacterName2, new Insets(20, 0, 0, 20));
		HBox.setMargin(character1Box, new Insets(0, 10, 0, 0));
		VBox.setMargin(label, new Insets(-70, 0, 0, 0));

		
		btnCharacterName1.setOnAction(btnCharacterListener);
		btnCharacterName2.setOnAction(btnCharacterListener);
		
		root.setCenter(menu);
		root.setTop(getBackButtonBar());
	}
	
	/*
	 * Choose Level
	 */

	public void levelGUI(BorderPane root) {
		root.setId("levelScene");
		VBox menu = new VBox();
		menu.setAlignment(Pos.CENTER);
		Label label = new Label("Choose Your Level!");
		label.setId("subTitle");

		Button btnEasy = new Button("Easy");
		Button btnHard = new Button("Hard");
		Button btnSuperHard = new Button("Super Hard");
		
		btnEasy.setId("levelBtn");
		btnHard.setId("levelBtn");
		btnSuperHard.setId("levelBtn");
		
		btnEasy.setPrefSize(100, 40);
		btnEasy.setPadding(new Insets(10, 25, 10, 25));
		btnHard.setPrefSize(100, 40);
		btnHard.setPadding(new Insets(10, 25, 10, 25));
		btnSuperHard.setPrefSize(150, 40);
		btnSuperHard.setPadding(new Insets(10, 25, 10, 25));

		menu.getChildren().addAll(label, btnEasy, btnHard, btnSuperHard);

		btnEasy.setOnAction(btnLevelListener);
		btnHard.setOnAction(btnLevelListener);
		btnSuperHard.setOnAction(btnLevelListener);
		
		root.setCenter(menu);
		root.setTop(getBackButtonBar());
		
		VBox.setMargin(btnHard, new Insets(20, 0, 20, 0));
		VBox.setMargin(label, new Insets(-70, 0, 50, 0));
	}
	
	/*
	 * Show Rules
	 */
	
	public void ruleGUI(BorderPane root) {
		root.setId("ruleScene");
		Image img = new Image("/resources/image/sketch.png");
		
		
		VBox menu = new VBox(15);
		menu.setAlignment(Pos.CENTER);
		menu.setPadding(new Insets(15));
		
		lblRule = new Label("How to play!");
		lblRule.setId("subTitle");
		VBox.setMargin(lblRule, new Insets(-50, 0, 0, 0));
		
		Rectangle rectangle = new Rectangle(430, 250);
		rectangle.setId("Box");
		rectangle.setFill(new ImagePattern(img));
		rectangle.setArcHeight(30);
		rectangle.setArcWidth(30);
		rectangle.setStroke(Color.TRANSPARENT);

		contentOfRule[0] = "1. Move around the maze and collect all the gems."
				+"\n\n2. You should avoid policmen while moving around."
				+"\n\n3. When the key appears, go get it."
				+"\n\n4. Drag the key and drop it to the door to unlock it.";
		contentOfRule[1] = "1. Move around with keyborad arrows."
				+"\n\n2. Put light on the camera to look further at night."
				+"\n\n3. Smash the space bar to use Hide Mode.";
		contentOfRule[2] = "You only have 2 days \nto steal the gems and escape!";

		txtRule = new Text();
		txtRule.setId("Boxtxt");

		StackPane stack = new StackPane();
		stack.getChildren().addAll(rectangle, txtRule);
		stack.setPadding(new Insets(0, 10, 0, 10));

		btnRulePrevious = new Button();
		btnRulePrevious.setPrefSize(40, 40);
		btnRulePrevious.setId("btnBack");
		btnRulePrevious.setOnAction(btnRulePreviousListener);

		btnRuleNext = new Button("");
		btnRuleNext.setPrefSize(40, 40);
		btnRuleNext.setId("btnForward");
		btnRuleNext.setOnAction(btnRuleNextListener);
		HBox rulesScreen = new HBox();
		
		rulesScreen.setAlignment(Pos.CENTER); 
		rulesScreen.getChildren().addAll(btnRulePrevious, stack, btnRuleNext);

		menu.getChildren().addAll(lblRule, rulesScreen);
		
		root.setCenter(menu);
		root.setTop(getBackButtonBar());
		
		
		
		updateRules(0); // Set screen 0

	}

}