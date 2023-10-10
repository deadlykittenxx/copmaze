import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AlertDialog {
	static Stage stage = new Stage();
	static Label label;
	
	public static void display() {
		VBox layout = new VBox();
		label = new Label("YOU WIN");
		
		layout.getChildren().add(label);
		
		Scene scene = new Scene(layout, CopMaze.WIDTH / 2, CopMaze.HEIGHT / 2);
		stage.setTitle("congrats >-<");
		stage.setScene(scene);
		stage.showAndWait();
	}
}
