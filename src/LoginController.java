import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private AuthManager authManager;

    // Called from Main after FXML is loaded
    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authManager == null) {
            messageLabel.setText("AuthManager not set.");
            return;
        }

        boolean success = authManager.login(username, password);
        if (success) {
            messageLabel.setText("");
            switchToGameScreen();
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void onSignup(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
        messageLabel.setText("");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Registration.fxml"));
            Parent root = loader.load();

            RegistrationController regController = loader.getController();
            regController.setAuthManager(authManager);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Banana Game - Register");
            stage.setScene(new Scene(root, 400, 320));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error opening registration page.");
        }
    }

    private void switchToGameScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameUI.fxml"));
            Parent gameRoot = loader.load();

            GameUIController gameUIController = loader.getController();
            gameUIController.setAuthManager(authManager);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Banana Game");
            stage.setScene(new Scene(gameRoot, 900, 700));

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading game screen.");
        }
    }

    public void setInfoMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setTextFill(Color.GREEN);
            messageLabel.setText(message);
        }
    }
}
