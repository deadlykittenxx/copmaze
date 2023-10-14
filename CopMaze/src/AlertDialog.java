import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AlertDialog {
	static Stage stage = new Stage();
	static Label label;
	static Button goBackBtn;
	public static void display(String str) {
		VBox layout = new VBox();
		label = new Label(str);
		goBackBtn = new Button("◀ Main Menu");
		goBackBtn.setOnAction(CopMaze.btnGoBackListener);
		layout.getChildren().addAll(label, goBackBtn);
		
		Scene scene = new Scene(layout, CopMaze.WIDTH / 2, CopMaze.HEIGHT / 2);
		stage.setTitle("the result >-<");
		stage.setScene(scene);
		stage.showAndWait();
	}
}
