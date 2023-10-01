import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

	private static final int MAZE_WIDTH = 20; // Number of cells
	private static final int MAZE_HEIGHT = 15; // Number of cells
	private static final int GRID_SIZE = 20; // Number of pixels per cell
	private static final int BORDER_SIZE = 2;

	private Cell[][] grid;

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
	private Button btnGoBack;
	private Button btnRuleNext;
	private Button btnRulePrevious;
	private Label lblRule;
	private String[] contentOfRule = new String[8];
	private Text txtRule;
	private int howtoPlayStep = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox(15);
		Scene scene = new Scene(root);
		root.setAlignment(Pos.CENTER);

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

		initGUI(root);
		characterGUI(characterRoot);
		levelGUI(levelRoot);
		mazeGUI(mazeRoot);
		ruleGUI(ruleRoot);

		String css = this.getClass().getResource("styles.css").toExternalForm();
		scene.getStylesheets().add(css);
		characterScene.getStylesheets().add(css);
		levelScene.getStylesheets().add(css);
		mazeScene.getStylesheets().add(css);
		ruleScene.getStylesheets().add(css);

		primaryStage.setTitle("Cop Maze");
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);
		primaryStage.setScene(scene);
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

		btnRuleNextListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (howtoPlayStep < 7) {
					howtoPlayStep++;
					updateRules();
				} else {
					stage.setScene(characterScene);
					howtoPlayStep = 0;
				}
			}
		};

		btnRulePreviousListener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (howtoPlayStep > 0) {
					howtoPlayStep--;
					updateRules();
				}
			}
		};

	}
	
	/*
	 * Generating BackButtonBar
	 */
	private Pane getBackButtonBar() {
		AnchorPane topPane = new AnchorPane();
		btnGoBack = new Button("<");
		topPane.getChildren().add(btnGoBack);
		topPane.setPrefHeight(30);
		AnchorPane.setTopAnchor(btnGoBack, 10.0);
		AnchorPane.setLeftAnchor(btnGoBack, 10.0);
		return topPane;
	}
	
	/*
	 * Update RuleGUI
	 */
	void updateRules() {
		if (howtoPlayStep <= 7) {
			txtRule.setText(contentOfRule[howtoPlayStep]);
			btnRuleNext.setText(">");

			if (howtoPlayStep < 4) {
				lblRule.setText("How to Play!");
			} else if (howtoPlayStep >= 4 && howtoPlayStep < 7) {
				lblRule.setText("Game Controls");
			} else if (howtoPlayStep == 7) {
				lblRule.setText("Be careful!");
				btnRuleNext.setText("Play");
			}

		}
	}
	
	/*
	 * Game Scene
	 */
	public void mazeGUI(VBox root) {
		
		Maze maze = new Maze(MAZE_WIDTH, MAZE_HEIGHT);

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
		VBox menu = new VBox(15);
		menu.setAlignment(Pos.CENTER);
		Label label = new Label("Choose Your Character!");
		label.setId("subTitle");
		label.setPadding(new Insets(20, 50, 50, 50));

		Button btnJohnny = new Button("Johnny");
		Button btnSarah = new Button("Sarah");
		btnJohnny.setId("btnStyle1");
		btnSarah.setId("btnStyle1");

		menu.getChildren().addAll(label, btnJohnny, btnSarah);

		btnJohnny.setOnAction(btnCharacterListener);
		btnSarah.setOnAction(btnCharacterListener);
		
		root.setCenter(menu);
		root.setTop(getBackButtonBar());
	}
	
	/*
	 * Choose Level
	 */

	public void levelGUI(BorderPane root) {
		VBox menu = new VBox(15);
		menu.setAlignment(Pos.CENTER);
		Label label = new Label("Choose Your Level!");
		label.setId("subTitle");
		label.setPadding(new Insets(20, 50, 50, 50));

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
	}
	
	/*
	 * Show Rules
	 */
	
	public void ruleGUI(BorderPane root) {
		VBox menu = new VBox(15);
		menu.setAlignment(Pos.CENTER);
		menu.setPadding(new Insets(15));
		
		lblRule = new Label("How to play!");
		lblRule.setId("subTitle");
		lblRule.setPadding(new Insets(0, 10, 10, 10));

		Rectangle rectangle = new Rectangle(430, 300);
		rectangle.setId("Box");
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
		txtRule.setText(contentOfRule[0]);

		StackPane stack = new StackPane();
		stack.getChildren().addAll(rectangle, txtRule);
		stack.setPadding(new Insets(0, 10, 0, 10));

		btnRulePrevious = new Button("<");
		btnRulePrevious.setPrefSize(60, 40);
		btnRulePrevious.setId("btnStyle2");
		btnRulePrevious.setOnAction(btnRulePreviousListener);

		btnRuleNext = new Button(">");
		btnRuleNext.setPrefSize(60, 40);
		btnRuleNext.setId("btnStyle2");
		btnRuleNext.setOnAction(btnRuleNextListener);
		
		HBox rulesScreen = new HBox();
		rulesScreen.setAlignment(Pos.CENTER); // BOTTOM_CENTER
		rulesScreen.getChildren().addAll(btnRulePrevious, stack, btnRuleNext);

		menu.getChildren().addAll(lblRule, rulesScreen);
		
		root.setCenter(menu);
		root.setTop(getBackButtonBar());

	}

}