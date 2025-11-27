import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AuthManager authManager = new AuthManager();

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authManager.login(username, password)) {
            messageLabel.setText("");

            try {
                // Load the game screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("GameUI.fxml"));
                Parent root = loader.load();

                // Get controller and pass the username
                GameUIController controller = loader.getController();
                controller.setUsername(username);

                // Switch scene on the same window
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 600, 400));
                stage.setTitle("Banana Game - Player: " + username);
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Error loading game screen.");
            }

        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }
}
