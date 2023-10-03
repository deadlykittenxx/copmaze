import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

	private static final int MAZE_WIDTH = 20; // Number of cells
	private static final int MAZE_HEIGHT = 15; // Number of cells
	private static final int GRID_SIZE = 20; // Number of pixels per cell
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
	private Button btnGoBack;
	private Button btnRuleNext;
	private Button btnRulePrevious;
	private Label lblRule;
	private String[] contentOfRule = new String[8];
	private Text txtRule;
	private int howToPlayStep = 0;
	private Character player;

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
		
		VBox mazeRoot = new VBox();
		mazeScene = new Scene(mazeRoot);
		mazeRoot.setAlignment(Pos.CENTER);

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
		AnchorPane.setTopAnchor(btnGoBack, 10.0);
		AnchorPane.setLeftAnchor(btnGoBack, 10.0);
		return topPane;
	}
	
	/*
	 * Update RuleGUI
	 */
	void updateRules(int step) {
		btnRulePrevious.setVisible(true);
		howToPlayStep = step;
		if (howToPlayStep <= 7) {
			txtRule.setText(contentOfRule[howToPlayStep]);
			if (howToPlayStep == 0) {
				btnRulePrevious.setVisible(false);
			}
			if (howToPlayStep < 4) {
				lblRule.setText("How to Play!");
			} else if (howToPlayStep >= 4 && howToPlayStep < 7) {
				lblRule.setText("Game Controls");
			} else if (howToPlayStep == 7) {
				lblRule.setText("Be careful!");
				btnRuleNext.setText("Play");
			}

		}
	}
	
	/*
	 * Game Scene
	 */
	public void mazeGUI(VBox root) {
		root.setId("mazeScene");
		
		Maze maze = new Maze(MAZE_WIDTH, MAZE_HEIGHT, EASINESS);

		Canvas canvas = new Canvas(MAZE_WIDTH * GRID_SIZE + BORDER_SIZE, MAZE_HEIGHT * GRID_SIZE + BORDER_SIZE);
		maze.draw(canvas.getGraphicsContext2D(), GRID_SIZE, BORDER_SIZE);

		root.getChildren().add(canvas);
		
	}
	
	/*
	 * Initalize the first Scene
	 */
	public void initGUI(VBox root) {
		root.setId("initScene");

		Label label = new Label("Cop Maze");
		label.setId("mainTitle");
		label.setPadding(new Insets(0, 50, 70, 50));

		Button btnHowtoPlay = new Button("How to Play");
		btnHowtoPlay.setPrefSize(120, 30);
		btnHowtoPlay.setId("btnStyle1");

		Button btnStart = new Button("Start");
		btnStart.setPrefSize(120, 30);
		btnStart.setId("btnStyle1");

		btnHowtoPlay.setOnAction(btnHowtoPlayListener);
		btnStart.setOnAction(btnStartListener);

		root.getChildren().addAll(label, btnHowtoPlay, btnStart);

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
		VBox JohnnyBox = new VBox();
		VBox SarahBox = new VBox();
		
		
		Button Johnny = new Button();
		Johnny.setId("btnJohnny");
		Johnny.setPrefSize(60, 100);
		
		Button Sarah = new Button();
		Sarah.setPrefSize(60, 100);
		Sarah.setId("btnSarah");
		
		Button btnJohnny = new Button("Johnny");
		Button btnSarah = new Button("Sarah");
		btnJohnny.setId("btnStyle1");
		btnSarah.setId("btnStyle1");

		JohnnyBox.getChildren().addAll(Johnny, btnJohnny);
		SarahBox.getChildren().addAll(Sarah, btnSarah);
		characterBox.getChildren().addAll(JohnnyBox, SarahBox);
		characterBox.setAlignment(Pos.CENTER);
		menu.getChildren().addAll(label, characterBox);
		
		/* Set Margins */
		VBox.setMargin(btnJohnny, new Insets(20, 0, 0, 0));
		VBox.setMargin(btnSarah, new Insets(20, 0, 0, 0));
		HBox.setMargin(JohnnyBox, new Insets(0, 50, 0, 0));
		VBox.setMargin(label, new Insets(0, 0, 50, 0));

		
		btnJohnny.setOnAction(btnCharacterListener);
		btnSarah.setOnAction(btnCharacterListener);
		Johnny.setOnAction(btnCharacterListener);
		Sarah.setOnAction(btnCharacterListener);
		
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

		btnEasy.setId("btnStyle1");
		btnHard.setId("btnStyle1");
		btnSuperHard.setId("btnStyle1");

		menu.getChildren().addAll(label, btnEasy, btnHard, btnSuperHard);

		btnEasy.setOnAction(btnLevelListener);
		btnHard.setOnAction(btnLevelListener);
		btnSuperHard.setOnAction(btnLevelListener);
		
		root.setCenter(menu);
		root.setTop(getBackButtonBar());
		
		VBox.setMargin(btnHard, new Insets(20, 0, 20, 0));
		VBox.setMargin(label, new Insets(0, 0, 50, 0));
	}
	
	/*
	 * Show Rules
	 */
	
	public void ruleGUI(BorderPane root) {
		root.setId("ruleScene");
		Image img = new Image("/resources/image/box02.png");
		
		
		VBox menu = new VBox(15);
		menu.setAlignment(Pos.CENTER);
		menu.setPadding(new Insets(15));
		
		lblRule = new Label("How to play!");
		lblRule.setId("subTitle");
		lblRule.setPadding(new Insets(0, 10, 10, 10));

		Rectangle rectangle = new Rectangle(430, 300);
		rectangle.setId("Box");
		rectangle.setFill(new ImagePattern(img));
		rectangle.setArcHeight(30);
		rectangle.setArcWidth(30);
		rectangle.setStroke(Color.TRANSPARENT);

		contentOfRule[0] = "1. Move around the maze and collect all the gems.";
		contentOfRule[1] = "2. You should avoid policmen while moving around.";
		contentOfRule[2] = "3. When the key appears, go get it.";
		contentOfRule[3] = "4. Drag the key and drop it to the door to unlock it.";
		contentOfRule[4] = "1. Move around with keyborad arrows.";
		contentOfRule[5] = "2. Put light on the camera to look further at night.";
		contentOfRule[6] = "3. Smash the space bar to use Hide Mode.";
		contentOfRule[7] = "You only have 2 days to steal the gems and escape!";

		txtRule = new Text();
		txtRule.setId("Boxtxt");

		StackPane stack = new StackPane();
		stack.getChildren().addAll(rectangle, txtRule);
		stack.setPadding(new Insets(0, 10, 0, 10));

		btnRulePrevious = new Button();
		btnRulePrevious.setPrefSize(60, 40);
		btnRulePrevious.setId("btnBack");
		btnRulePrevious.setOnAction(btnRulePreviousListener);

		btnRuleNext = new Button("");
		btnRuleNext.setPrefSize(60, 40);
		btnRuleNext.setId("btnForward");
		btnRuleNext.setOnAction(btnRuleNextListener);
		
		HBox rulesScreen = new HBox();
		rulesScreen.setAlignment(Pos.CENTER); // BOTTOM_CENTER
		rulesScreen.getChildren().addAll(btnRulePrevious, stack, btnRuleNext);

		menu.getChildren().addAll(lblRule, rulesScreen);
		
		root.setCenter(menu);
		root.setTop(getBackButtonBar());
		
		updateRules(0); // Set screen 0

	}

}