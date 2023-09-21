import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class CopMaze extends Application {

	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
			
		FlowPane root = new FlowPane();
		
				
		Scene scene = new Scene(root, 300, 300);
		
		primaryStage.setTitle("Layout Window!");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
}