import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AlertDialog {
	static Stage stage = new Stage();
	static Label label;
	
	public static void display(String str) {
		VBox layout = new VBox();
		label = new Label(str);
		
		layout.getChildren().add(label);
		
		Scene scene = new Scene(layout, CopMaze.WIDTH / 2, CopMaze.HEIGHT / 2);
		stage.setTitle("the result >-<");
		stage.setScene(scene);
		stage.showAndWait();
	}
}
