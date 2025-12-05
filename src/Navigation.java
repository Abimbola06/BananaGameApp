import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigation {

    /**
     * Loads the Login screen and injects AuthManager.
     * Optionally sets a message on the LoginController.
     */
    public static void goToLogin(Stage stage, AuthManager authManager, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigation.class.getResource("Login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setAuthManager(authManager);

            if (message != null && !message.isBlank()) {
                loginController.setInfoMessage(message);   // green success text
            }

            stage.setTitle("Banana Game - Login");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
