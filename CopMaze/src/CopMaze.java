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

/**
 * The main class for the Cop Maze game application. This class extends the
 * JavaFX Application class and serves as the entry point for the game (controller class).
 */
public class CopMaze extends Application {
	/**
	 * The width of the game window.
	 */
	public static final int WIDTH = 720;

	/**
	 * The height of the game window.
	 */
	public static final int HEIGHT = 720;

	/**
	 * The size of each grid cell in pixels.
	 */
	public static final int GRID_SIZE = 30;

	/**
	 * The size of the border around cells.
	 */
	private static final int BORDER_SIZE = 2;

	/**
	 * An array of character information for character selection in the game.
	 */
	private static final CharacterInfo[] CHARACTERS_INFO = new CharacterInfo[] { new CharacterInfo("Bonnie", 0),
			new CharacterInfo("Clyde", 1) };

	/**
	 * An array of predefined game difficulty levels.
	 */
	private static final DifficultyLevel[] DIFFICULTY_LEVELS = new DifficultyLevel[] {
			new DifficultyLevel("Easy", 3, 1, 2, 18, 14, 0.3, 100),
			new DifficultyLevel("Hard", 5, 2, 2, 20, 15, 0.2, 100),
			new DifficultyLevel("Super Hard", 7, 3, 2, 22, 16, 0.1, 150) };

	/**
	 * The initial scene for the game (main menu).
	 */
	private Scene initScene;

	/**
	 * The scene for character selection.
	 */
	private Scene characterScene;

	/**
	 * The scene for selecting the game level.
	 */
	private Scene levelScene;

	/**
	 * The root layout for the main game scene.
	 */
	private Scene mazeScene;

	/**
	 * The scene for displaying game rules.
	 */
	private Scene ruleScene;

	/**
	 * The root layout for the main game scene.
	 */
	private BorderPane mazeRoot;

	/**
	 * Event handler for the "Start" button.
	 */
	private EventHandler<ActionEvent> btnStartListener;

	/**
	 * Event handler for the "How to Play" button.
	 */
	private EventHandler<ActionEvent> btnHowtoPlayListener;

	/**
	 * Event handler for the "Character" button.
	 */
	private EventHandler<ActionEvent> btnCharacterListener;

	/**
	 * Event handler for the "Level" button.
	 */
	private EventHandler<ActionEvent> btnLevelListener;

	/**
	 * Event handler for the "Next" button in the rule scene.
	 */
	private EventHandler<ActionEvent> btnRuleNextListener;

	/**
	 * Event handler for the "Previous" button in the rule scene.
	 */
	private EventHandler<ActionEvent> btnRulePreviousListener;

	/**
	 * Event handler for the "Go Back" button.
	 */
	public static EventHandler<ActionEvent> btnGoBackListener;

	/**
	 * Event handler for keyboard input during character movement.
	 */
	private EventHandler<KeyEvent> characterListener;

	/**
	 * The "Go Back" button for navigating back to the previous scene.
	 */
	private Button btnGoBack;

	/**
	 * The "Next" button for navigating to the next rule in the rule scene.
	 */
	private Button btnRuleNext;

	/**
	 * The "Previous" button for navigating to the previous rule in the rule scene.
	 */
	private Button btnRulePrevious;

	/**
	 * The label displaying game rules in the rule scene.
	 */
	private Label lblRule;

	/**
	 * An array of strings containing the content of game rules.
	 */
	private String[] contentOfRule = new String[8];

	/**
	 * The text component for displaying game rules.
	 */
	private Text txtRule;

	/**
	 * The label displaying the number of remaining gems in the game.
	 */
	private Label numOfGems;

	/**
	 * The label indicating the status of collecting a key to open the door.
	 */
	public static Label keyCollect;

	/**
	 * The current step in the "How to Play" rules.
	 */
	private int howToPlayStep = 0;

	/**
	 * The selected player character.
	 */
	private Character player;

	/**
	 * The selected game difficulty level.
	 */
	private DifficultyLevel difficultyLevel;

	/**
	 * The game's maze, representing the game world.
	 */
	private Maze maze;

	/**
	 * The main entry point for the JavaFX application.
	 *
	 * @param args The command-line arguments passed to the application.
	 * @throws Exception If an exception occurs during application initialization.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Initializes the game application and sets up the initial scene.
	 *
	 * @param primaryStage The primary stage where the game is displayed.
	 * @throws Exception If an exception occurs during the initialization.
	 */
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
		// mazeRoot.setAlignment(Pos.CENTER);

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

	/**
	 * Initializes event listeners for handling user interactions.
	 *
	 * @param stage The JavaFX stage where the application is running.
	 */
	public void initListener(Stage stage) {

		/*
		 * Character moving
		 */
		characterListener = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode().equals(KeyCode.RIGHT)) {
					maze.moveCharacterRight();
				} else if (e.getCode().equals(KeyCode.LEFT)) {
					maze.moveCharacterLeft();
				} else if (e.getCode().equals(KeyCode.UP)) {
					maze.moveCharacterUp();
				} else if (e.getCode().equals(KeyCode.DOWN)) {
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

	/**
	 * Creates and returns a user interface element representing a back button bar.
	 *
	 * @return A JavaFX Pane containing a back button that allows users to navigate
	 *         back to the main menu.
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

	/**
	 * Updates the display of the game rules for the How to Play screen.
	 *
	 * @param step The step or screen number of the rules to be displayed.
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

	/**
	 * Updates the label displaying the number of remaining gems in the game.
	 */
	public void updateGemsLabel() {
		numOfGems.setText("Gems Left: " + maze.getNumOfGemsLeft());

	}

	/**
	 * Updates the label displaying the status of key collection and door opening.
	 */
	public void updateKeyLabel() {
		if (maze.getNumOfGemsLeft() == 0 && maze.getDoor().isOpened == false) {
			keyCollect.setText("Open the door with Key.");
		} else if (maze.getDoor().isOpened == true) {
			keyCollect.setText("Door Opened ! ");
		}
	}

	/**
	 * Displays the main game screen with the maze.
	 *
	 * @param root The root BorderPane where the game content is placed.
	 */
	public void mazeGUI(BorderPane root) {
		root.setId("mazeScene");

		maze = new Maze(difficultyLevel.mazeWidth, difficultyLevel.mazeHeight, difficultyLevel.easiness, player,
				difficultyLevel.nbGems, difficultyLevel.nbPolice);

		Label label = new Label(difficultyLevel.name);
		label.setId("levelLabel");
		label.setPadding(new Insets(0, 25, 15, 25));
		label.setPrefSize(320, 60);
		label.setAlignment(Pos.CENTER);

		MazeNode mazeNode = new MazeNode(maze, GRID_SIZE, BORDER_SIZE);
		mazeNode.setMaxWidth(maze.getWidth() * GRID_SIZE + BORDER_SIZE);
		mazeNode.setMaxHeight(maze.getHeight() * GRID_SIZE + BORDER_SIZE);
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
		tags.setPadding(new Insets(0, 0, 40, (WIDTH - (maze.getWidth() * GRID_SIZE + BORDER_SIZE)) / 2));

		mazeScene.setOnKeyPressed(characterListener);
		BorderPane.setAlignment(label, Pos.BOTTOM_CENTER);
		BorderPane.setAlignment(tags, Pos.TOP_CENTER);
		BorderPane.setMargin(label, new Insets(40, 0, 0, 0));
		root.setTop(label);
		root.setCenter(mazeNode);
		root.setBottom(tags);
	}

	/**
	 * Initializes the initial scene of the game.
	 *
	 * @param root The root VBox where the initial menu options are placed.
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

	/**
	 * Displays the character selection screen.
	 *
	 * @param root The root BorderPane where character selection options are placed.
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

	/**
	 * Displays the level selection screen.
	 *
	 * @param root The root BorderPane where level selection options are placed.
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

	/**
	 * Displays the rules and how-to-play screen.
	 *
	 * @param root The root BorderPane where the rules and instructions are
	 *             displayed.
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
				+ "\n\n2. You should avoid policemen while moving around." + "\n\n3. When the key appears, go get it."
				+ "\n\n4. Drag the key and drop it to the door to unlock it.";
		contentOfRule[1] = "1. Move around with keyboard arrows." + "\n\n2. You need to scream to avoid policemen.";
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

/**
 * Represents information about a character available for selection in the game (model class).
 */
class CharacterInfo {
	/**
	 * The name of the character.
	 */
	public String name;

	/**
	 * The sprite ID associated with the character.
	 */
	public int sprite_id;

	/**
	 * Constructs a new CharacterInfo with the specified name and sprite ID.
	 *
	 * @param name      The name of the character.
	 * @param sprite_id The sprite ID associated with the character.
	 */
	public CharacterInfo(String name, int sprite_id) {
		this.name = name;
		this.sprite_id = sprite_id;
	}
}

/**
 * Represents information about a game difficulty level, including the number of
 * gems, police, maze size, and other settings (model class).
 */
class DifficultyLevel {
	/**
	 * The name of the difficulty level.
	 */
	public String name;

	/**
	 * The number of gems in the game.
	 */
	public int nbGems;

	/**
	 * The number of police characters in the game.
	 */
	public int nbPolice;

	/**
	 * The number of in-game days (or rounds).
	 */
	public int nbDays;

	/**
	 * The width of the maze for this difficulty level.
	 */
	public int mazeWidth;

	/**
	 * The height of the maze for this difficulty level.
	 */
	public int mazeHeight;

	/**
	 * The easiness factor for the difficulty level, affecting character behavior.
	 */
	public double easiness;

	/**
	 * The preferred button width for UI elements related to this difficulty level.
	 */
	public int buttonWidth;

	/**
	 * Constructs a new DifficultyLevel with the specified settings.
	 *
	 * @param name        The name of the difficulty level.
	 * @param nbGems      The number of gems in the game.
	 * @param nbPolice    The number of police characters in the game.
	 * @param nbDays      The number of in-game days (or rounds).
	 * @param mazeWidth   The width of the maze for this difficulty level.
	 * @param mazeHeight  The height of the maze for this difficulty level.
	 * @param easiness    The easiness factor affecting character behavior.
	 * @param buttonWidth The preferred button width for UI elements related to this
	 *                    difficulty level.
	 */
	public DifficultyLevel(String name, int nbGems, int nbPolice, int nbDays, int mazeWidth, int mazeHeight,
			double easiness, int buttonWidth) {
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