// Connects to Banana API (interoperability)
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Handles interoperability by connecting to the Banana API.
 * This demonstrates how the application communicates with an
 * external web service to retrieve quiz questions.
 * <p>
 * API Endpoint: <a href="https://marcconrad.com/uob/banana/api.php">...</a>
 */
public class BananaAPIClient {

    // The API endpoint URL
    private static final String API_URL = "https://marcconrad.com/uob/banana/api.php";

    /**
     * Fetches a quiz question from the Banana API and prints the response.
     */
    public String fetchQuestion() {
        try {
            URI uri = URI.create(API_URL);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();

        } catch (Exception e) {
            return "Error connecting to Banana API: " + e.getMessage();  // ← IMPORTANT
        }
    }
}
