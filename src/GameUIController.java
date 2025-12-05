import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Priority;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.Optional;

public class GameUIController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ImageView questionImageView;

    @FXML
    private TextField answerField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button nextQuestionButton;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Button menuButton;

    @FXML
    private VBox menuPanel;

    private final BananaAPIClient apiClient = new BananaAPIClient();
    private BananaQuestion currentQuestion;
    private String username;
    private int triesLeft;

    private static final int MAX_TRIES = 3;

    private AuthManager authManager;

    /**
     * Called from LoginController after loading this FXML.
     * This wires the AuthManager and sets the welcome text.
     */
    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;

        // If AuthManager already has a logged-in user, use that name
        if (authManager != null && authManager.getCurrentUsername() != null) {
            setUsername(authManager.getCurrentUsername());
        }
    }

    public void setUsername(String username) {
        this.username = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
        }
    }

    @FXML
    private void initialize() {
        VBox.setVgrow(questionImageView, Priority.ALWAYS);

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            questionImageView.setFitWidth(width * 0.6);
        });
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double h = newVal.doubleValue();
            double fontSize = Math.max(26, h / 30);
            welcomeLabel.setStyle("-fx-font-size: " + fontSize + "px; -fx-padding: 10; -fx-font-weight: bold;");
        });

        nextQuestionButton.setDisable(true);
        loadNewQuestion();
    }

    @FXML
    private void loadNewQuestion() {
        statusLabel.setText("Loading new question...");
        answerField.clear();
        answerField.setDisable(false);

        currentQuestion = apiClient.fetchQuestion();

        if (currentQuestion == null) {
            statusLabel.setText("Could not load question. Please try again.");
            nextQuestionButton.setDisable(false);
            return;
        }

        triesLeft = MAX_TRIES;
        statusLabel.setText("You have " + triesLeft + " tries.");
        nextQuestionButton.setDisable(true);

        try {
            Image image = new Image(currentQuestion.getImageUrl(), true);
            questionImageView.setImage(image);
        } catch (Exception e) {
            statusLabel.setText("Failed to load image.");
        }
    }

    @FXML
    private void onSubmitAnswer() {
        if (currentQuestion == null) {
            statusLabel.setText("No question loaded yet.");
            return;
        }

        String text = answerField.getText();

        if (text == null || text.isBlank()) {
            statusLabel.setText("Please enter a number.");
            return;
        }

        int answer;
        try {
            answer = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("Answer must be a number.");
            answerField.clear();
            answerField.requestFocus();
            return;
        }

        boolean correct = (answer == currentQuestion.getSolution());

        // Record this attempt in the database (virtual identity + stats)
        if (authManager != null && authManager.isLoggedIn()) {
            authManager.recordAttempt(correct);
        }

        if (correct) {
            // right answer
            if (authManager != null && authManager.isLoggedIn()) {
                authManager.recordAttempt(true);
            }
            statusLabel.setText("Correct! The answer is " + currentQuestion.getSolution() + ".");
            answerField.setDisable(true);
            nextQuestionButton.setDisable(false);
        } else {
            // wrong answer
            triesLeft--;
            if (triesLeft > 0) {
                statusLabel.setText(
                        "Incorrect. You have " + triesLeft + " more " + (triesLeft == 1 ? "try." : "tries.")
                );
            } else {
                if (authManager != null && authManager.isLoggedIn()) {
                    authManager.recordAttempt(false);
                }
                statusLabel.setText(
                        "No tries left. The correct answer was " + currentQuestion.getSolution() + "."
                );
                answerField.setDisable(true);
                nextQuestionButton.setDisable(false);
            }
        }
        if (!answerField.isDisabled()) {
            answerField.clear();
            answerField.requestFocus();
        }
    }

    @FXML
    private void onNextQuestion() {
        loadNewQuestion();
    }

    /**
     * "View Progress" button handler.
     * Shows total attempts, correct attempts and rank using AuthManager.
     */
    @FXML
    private void onViewProgress() {
        menuPanel.setVisible(false);
        menuPanel.setManaged(false);

        if (authManager == null || !authManager.isLoggedIn()) {
            showAlert("Not logged in", "You must be logged in to view your progress.");
            return;
        }

        UserRepository.UserStats stats = authManager.getCurrentUserStats();
        int rank = authManager.getCurrentUserRank();

        StringBuilder sb = new StringBuilder();
        sb.append("Your Progress\n\n");
        sb.append("Total attempts: ").append(stats.totalAttempts).append("\n");
        sb.append("Correct attempts: ").append(stats.correctAttempts).append("\n");
        sb.append("Rank among all users: ");
        sb.append(rank == -1 ? "Unknown" : rank);

        ButtonType okButton = new ButtonType("OK", ButtonType.OK.getButtonData());
        ButtonType leaderboardButton = new ButtonType("View Leaderboard");

        Alert alert = new Alert(AlertType.INFORMATION, sb.toString(), okButton, leaderboardButton);
        alert.setTitle("Your Progress");
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == leaderboardButton) {
            showLeaderboardWindow();
        }
    }

    @FXML
    private void onLogout() {
        menuPanel.setVisible(false);
        menuPanel.setManaged(false);

        if (authManager != null && authManager.isLoggedIn()) {
            authManager.logout();
        }

        Stage stage = (Stage) rootPane.getScene().getWindow();
        Navigation.goToLogin(stage, authManager, "You have been logged out.");
    }

    @FXML
    private void onMenuClicked() {
        boolean showing = menuPanel.isVisible();
        menuPanel.setVisible(!showing);
        menuPanel.setManaged(!showing);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showLeaderboardWindow() {
        if (authManager == null) {
            showAlert("Error", "AuthManager not available.");
            return;
        }

        List<UserRepository.PlayerScore> topPlayers = authManager.getTopPlayers(10);

        if (topPlayers.isEmpty()) {
            showAlert("Leaderboard", "No players found yet.");
            return;
        }

        // Axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Player");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Correct Attempts");

        // Bar chart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top Players");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Correct Attempts");

        for (UserRepository.PlayerScore ps : topPlayers) {
            XYChart.Data<String, Number> data =
                    new XYChart.Data<>(ps.username, ps.correctAttempts);
            series.getData().add(data);
        }

        barChart.getData().add(series);

        // 🎨 Make bars colourful
        String[] colors = {
                "#ff6b6b", "#feca57", "#54a0ff", "#5f27cd", "#1dd1a1",
                "#ee5253", "#ff9f43", "#48dbfb", "#10ac84", "#341f97"
        };

        // Nodes for bars are created lazily, so we listen for them:
        for (int i = 0; i < series.getData().size(); i++) {
            final int index = i;
            XYChart.Data<String, Number> item = series.getData().get(i);
            item.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    String color = colors[index % colors.length];
                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                }
            });
        }

        Scene scene = new Scene(barChart, 700, 400);
        Stage stage = new Stage();
        stage.setTitle("Leaderboard");
        stage.setScene(scene);
        stage.show();
    }
}