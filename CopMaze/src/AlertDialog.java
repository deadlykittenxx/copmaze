import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

		Scene scene = new Scene(layout, CopMaze.WIDTH, CopMaze.HEIGHT);
		stage.setTitle("the result >-<");
		stage.setScene(scene);

		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(50));
		layout.setStyle("-fx-background-color: black;");
		VBox.setMargin(label, new Insets(0, 0, 20, 0));
		if(str.equals("YOU LOSE")){
			label.setStyle("-fx-text-fill: red; -fx-font-size: 50; -fx-font-family: 'Pixel Script';");
		} else {
			label.setStyle("-fx-text-fill: white; -fx-font-size: 50; -fx-font-family: 'Pixel Script';");
		}
		goBackBtn.setStyle("-fx-text-fill: white; -fx-font-family: 'DePixel'; -fx-font-size: 12; -fx-background-color: transparent;");
		stage.show();

	}
}
