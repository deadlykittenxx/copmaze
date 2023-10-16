

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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CopMaze extends Application {

	public static final int WIDTH = 720;
	public static final int HEIGHT = 720;

	public static final int GRID_SIZE = 30; // Number of pixels per cell
	private static final int BORDER_SIZE = 2;

	private static final CharacterInfo[] CHARACTERS_INFO = new CharacterInfo[] {
		new CharacterInfo("Bonnie", 0),
		new CharacterInfo("Clyde", 1)
	};

	private static final DifficultyLevel[] DIFFICULTY_LEVELS = new DifficultyLevel[] {
		new DifficultyLevel("Easy", 3, 1, 2, 18, 14, 0.3, 100),
		new DifficultyLevel("Hard", 5, 2, 2, 20, 15, 0.2, 100),
		new DifficultyLevel("Super Hard", 7, 3, 2, 22, 16, 0.1, 150)
	};

	private Scene initScene;
	private Scene characterScene;
	private Scene levelScene;
	private Scene mazeScene;
	private Scene ruleScene;
	private BorderPane mazeRoot;
	private EventHandler<ActionEvent> btnStartListener;
	private EventHandler<ActionEvent> btnHowtoPlayListener;
	private EventHandler<ActionEvent> btnCharacterListener;
	private EventHandler<ActionEvent> btnLevelListener;
	private EventHandler<ActionEvent> btnRuleNextListener;
	private EventHandler<ActionEvent> btnRulePreviousListener;
	public static EventHandler<ActionEvent> btnGoBackListener;
	private EventHandler<KeyEvent> characterListener;
	private Button btnGoBack;
	private Button btnRuleNext;
	private Button btnRulePrevious;
	private Label lblRule;
	private String[] contentOfRule = new String[8];
	private Text txtRule;
	private Label numOfGems;
	public static Label keyCollect;
	private int howToPlayStep = 0;

	private Character player;
	private DifficultyLevel difficultyLevel;
	private Maze maze;
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
		
		mazeRoot = new BorderPane();
		mazeScene = new Scene(mazeRoot);
		//mazeRoot.setAlignment(Pos.CENTER);

		initListener(primaryStage);

		initGUI(initRoot);
		characterGUI(characterRoot);
		levelGUI(levelRoot);
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
				if (e.getCode().equals(KeyCode.RIGHT)) {
					maze.moveCharacterRight();
				} else if (e.getCode().equals(KeyCode.LEFT)) {
					maze.moveCharacterLeft();
				} else if(e.getCode().equals(KeyCode.UP)) {
					maze.moveCharacterUp();
				} else if(e.getCode().equals(KeyCode.DOWN)) {
					maze.moveCharacterDown();
				}
				
				updateGemsLabel();
				updateKeyLabel();
			}
		};
		
		btnLevelListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Button btn = (Button) arg0.getSource();
				difficultyLevel = (DifficultyLevel) btn.getUserData();
				mazeGUI(mazeRoot);
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
				CharacterInfo ci = (CharacterInfo) btn.getUserData();
				player = new Character(ci.name, ci.sprite_id);
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
				AlertDialog.stage.hide();
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
		AnchorPane.setTopAnchor(btnGoBack, 30.0);
		AnchorPane.setLeftAnchor(btnGoBack, 30.0);
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
	
	public void updateGemsLabel() {
		numOfGems.setText("Gems Left: " + maze.getNumOfGemsLeft());

	}


	public void updateKeyLabel() {
		if(maze.getNumOfGemsLeft() == 0 && maze.getDoor().isOpened == false) {
			keyCollect.setText("Open the door with Key.");
		} else if(maze.getDoor().isOpened == true) {
			keyCollect.setText("Door Opened ! ");
		}
	}
	/*
	 * Game Scene
	 */
	public void mazeGUI(BorderPane root) {
		root.setId("mazeScene");
		
		
		maze = new Maze(difficultyLevel.mazeWidth, difficultyLevel.mazeHeight, difficultyLevel.easiness, player, difficultyLevel.nbGems, difficultyLevel.nbPolice);

		Label label = new Label(difficultyLevel.name);
		label.setId("levelLabel");
		label.setPadding(new Insets(0, 25, 15, 25));
		label.setPrefSize(320, 60);
		label.setAlignment(Pos.CENTER);

		MazeNode mazeNode = new MazeNode(maze, GRID_SIZE, BORDER_SIZE);
		mazeNode.setMaxWidth(maze.getWidth()*GRID_SIZE + BORDER_SIZE);
		mazeNode.setMaxHeight(maze.getHeight()*GRID_SIZE + BORDER_SIZE);
		numOfGems = new Label("Gems Left: " + maze.getNumOfGemsLeft());
		numOfGems.setPadding(new Insets(7, 15, 7, 15));
		keyCollect = new Label("You need to get a Key.");
		keyCollect.setPadding(new Insets(7, 15, 7, 15));
		keyCollect.setId("tags");
		numOfGems.setId("tags");
		Region region1 = new Region();
		HBox.setHgrow(region1, Priority.ALWAYS);

		Region region2 = new Region();
		HBox.setHgrow(region2, Priority.ALWAYS);


		HBox tags = new HBox(numOfGems, region1, keyCollect, region2);
		tags.setPadding(new Insets(0, 0, 40, (WIDTH-(maze.getWidth()*GRID_SIZE + BORDER_SIZE))/2));

		mazeScene.setOnKeyPressed(characterListener);
		BorderPane.setAlignment(label, Pos.BOTTOM_CENTER);
		BorderPane.setAlignment(tags, Pos.TOP_CENTER);
		BorderPane.setMargin(label, new Insets(40, 0, 0, 0));
		root.setTop(label);
		root.setCenter(mazeNode);
		root.setBottom(tags);
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
		
		
		HBox charactersPanelBox = new HBox();

		for (CharacterInfo ci : CHARACTERS_INFO) {
			VBox characterBox = new VBox();

			Button btnCharacterName = new Button(ci.name);
			btnCharacterName.setUserData(ci);
			btnCharacterName.setPadding(new Insets(0, 25, 15, 25));
			VBox.setMargin(btnCharacterName, new Insets(20, 0, 0, 10));
			btnCharacterName.setId("btnLabel");
			btnCharacterName.setOnAction(btnCharacterListener);

			CharacterNode sprite = new CharacterNode(ci.sprite_id, 128, 128);

			characterBox.setAlignment(Pos.CENTER);
			characterBox.getChildren().addAll(sprite, btnCharacterName);
			HBox.setMargin(characterBox, new Insets(0, 10, 0, 0));
			charactersPanelBox.getChildren().add(characterBox);
		}
		
		charactersPanelBox.setAlignment(Pos.CENTER);
	
		menu.getChildren().addAll(label, charactersPanelBox);
		
		/* Set Margins */
		VBox.setMargin(label, new Insets(-70, 0, 0, 0));
		
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
		VBox.setMargin(label, new Insets(-70, 0, 50, 0));

		menu.getChildren().add(label);

		for (DifficultyLevel dl : DIFFICULTY_LEVELS) {
			Button btnLevel = new Button(dl.name);
			btnLevel.setUserData(dl);
			btnLevel.setPrefSize(dl.buttonWidth, 40);
			btnLevel.setPadding(new Insets(10, 25, 10, 25));
			btnLevel.setId("levelBtn");
			btnLevel.setOnAction(btnLevelListener);
			menu.getChildren().add(btnLevel);
		}
		
		root.setCenter(menu);
		root.setTop(getBackButtonBar());
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
		VBox.setMargin(lblRule, new Insets(-30, 0, 0, 0));
		
		Rectangle rectangle = new Rectangle(430, 350);
		rectangle.setId("Box");
		rectangle.setFill(new ImagePattern(img));
		rectangle.setArcHeight(30);
		rectangle.setArcWidth(30);
		rectangle.setStroke(Color.TRANSPARENT);

		contentOfRule[0] = "1. Move around the maze and collect all the gems."
				+"\n\n2. You should avoid policemen while moving around."
				+"\n\n3. When the key appears, go get it."
				+"\n\n4. Drag the key and drop it to the door to unlock it.";
		contentOfRule[1] = "1. Move around with keyboard arrows."
				+"\n\n2. You need to scream to avoid policemen.";
		contentOfRule[2] = "There are more policemen and gems depends on the level.";

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

class CharacterInfo {
	public String name;
	public int sprite_id;

	public CharacterInfo(String name, int sprite_id) {
		this.name = name;
		this.sprite_id = sprite_id;
	}
}

class DifficultyLevel {
	public String name;
	public int nbGems;
	public int nbPolice;
	public int nbDays;
	public int mazeWidth;
	public int mazeHeight;
	public double easiness;
	public int buttonWidth;

	public DifficultyLevel(String name, int nbGems, int nbPolice, int nbDays, int mazeWidth, int mazeHeight, double easiness, int buttonWidth) {
		this.name = name;
		this.nbGems = nbGems;
		this.nbPolice = nbPolice;
		this.nbDays = nbDays;
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.easiness = easiness;
		this.buttonWidth = buttonWidth;
	}
}