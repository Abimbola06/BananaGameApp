import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private AuthManager authManager;

    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    @FXML
    private void onFinish(ActionEvent event) {
        if (authManager == null) {
            messageLabel.setText("AuthManager not set.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isBlank() || password.isBlank() || confirm.isBlank()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        boolean success = authManager.register(username, password);
        if (success) {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Navigation.goToLogin(stage, authManager, "Account created! Please log in.");
        } else {
            messageLabel.setText("Could not create account. Username may already exist.");
        }
    }

    @FXML
    private void onBackToLogin(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        Navigation.goToLogin(stage, authManager, null);
    }
}
