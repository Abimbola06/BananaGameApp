import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class GameUIController {

    private final BananaAPIClient apiClient = new BananaAPIClient();
    private String username;

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextArea resultArea;

    public void setUsername(String username) {
        this.username = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
        }
    }

    @FXML
    private void initialize() {
        if (username != null && welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
        }
    }

    @FXML
    private void onFetchQuestion() {
        String response = apiClient.fetchQuestion();
        resultArea.setText(response);
    }
}