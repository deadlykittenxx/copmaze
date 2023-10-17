import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The AlertDialog class is a utility class for displaying alert dialogs.
 * It creates a dialog window with a message.
 */
public class AlertDialog {
	/**
	 * The JavaFX stage for the alert dialog.
	 */
	static Stage stage = new Stage();

	/**
	 * The label displaying the message.
	 */
	static Label label;

	/**
	 * The "Go Back" button for navigating to the main menu
	 */
	static Button goBackBtn;

	/**
	 * Displays an alert dialog with the given message.
	 * @param str The message to display in the alert dialog.
	 */
	public static void display(String str) {
		VBox layout = new VBox(); // Vertical layout for dialog components
		label = new Label(str);
		goBackBtn = new Button("◀ Main Menu");
		goBackBtn.setOnAction(CopMaze.btnGoBackListener);
		layout.getChildren().addAll(label, goBackBtn);

		Scene scene = new Scene(layout, CopMaze.WIDTH, CopMaze.HEIGHT);
		stage.setTitle("the result >-<");
		stage.setScene(scene);

		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(50));

		// Customize styles based on the message
		VBox.setMargin(label, new Insets(0, 0, 20, 0));
		if(str.equals("YOU LOSE")){
			layout.setStyle("-fx-background-color: black;");
			label.setStyle("-fx-text-fill: red; -fx-font-size: 50; -fx-font-family: 'Pixel Script';");
			goBackBtn.setStyle("-fx-text-fill: white; -fx-font-family: 'DePixel'; -fx-font-size: 12; -fx-background-color: transparent;");
		} else {
			layout.setStyle("-fx-background-image: url(/resources/image/backgroundWin.png); -fx-background-repeat: no-repeat; -fx-background-position: center; -fx-background-size: stretch;");
			label.setStyle("-fx-text-fill: black; -fx-font-size: 50; -fx-font-family: 'Pixel Script';");
			goBackBtn.setStyle("-fx-text-fill: black; -fx-font-family: 'DePixel'; -fx-font-size: 12; -fx-background-color: transparent;");
		}
		stage.show(); // Show the alert dialog

	}
}
