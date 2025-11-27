import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;

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

    private final BananaAPIClient apiClient = new BananaAPIClient();
    private BananaQuestion currentQuestion;
    private String username;
    private int triesLeft;

    private static final int MAX_TRIES = 3;

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
            double fontSize = Math.max(16, h / 30);
            welcomeLabel.setStyle("-fx-font-size: " + fontSize + "px; -fx-padding: 10;");
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

        if (answer == currentQuestion.getSolution()) {
            statusLabel.setText("Correct! The answer is " + currentQuestion.getSolution() + ".");
            answerField.setDisable(true);
            nextQuestionButton.setDisable(false);
        } else {
            triesLeft--;
            if (triesLeft > 0) {
                statusLabel.setText(
                        "Incorrect. You have " + triesLeft + " more " + (triesLeft == 1 ? "try." : "tries.")
                );
            } else {
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
}