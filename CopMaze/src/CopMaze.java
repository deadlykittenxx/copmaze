import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CopMaze extends Application {
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	
	public Scene characterScene;
	public Scene levelScene;
	public EventHandler<ActionEvent> btnStartListener;
	public EventHandler<ActionEvent> btnCharacterListener;
	
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
		
		initListener(primaryStage);
		
		
		initGUI(root);
		characterGUI(characterRoot);
		levelGUI(levelRoot);
		
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
		
	}
	
	public void levelGUI(VBox root) {
		Label label = new Label("Choose Your Level !"); 
		label.setPadding(new Insets(20, 50, 50, 50));
		
		Button btnEasy = new Button("Easy");
		Button btnHard = new Button("Hard");
		Button btnSuperHard = new Button("Super Hard");
		
		root.getChildren().addAll(label, btnEasy, btnHard, btnSuperHard);
		
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
		
		btnStart.setOnAction(btnStartListener);
		
		
		
		root.getChildren().addAll(label, btnHowtoPlay, btnStart);
		
	}
	
	
}