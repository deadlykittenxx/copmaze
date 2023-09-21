import javafx.application.Application;
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

	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
	
		VBox root = new VBox();
		Scene scene = new Scene(root, 700, 500);
		root.setAlignment(Pos.CENTER);
		
		initGUI(root);
		primaryStage.setTitle("Cop Maze"); 
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
	
	/*
	 * 	Initalize the first Scene
	 */
	public void initGUI(Pane root) {
		Label label = new Label("Cop Maze");
		label.setPadding(new Insets(20, 50, 50, 50));
		
		Button btnHowtoPlay = new Button("How to Play");
		Button btnStart = new Button("Start");
		
		root.getChildren().addAll(label, btnHowtoPlay, btnStart);
		
	}
	
	
}