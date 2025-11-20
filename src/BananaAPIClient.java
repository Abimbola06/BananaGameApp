// Connects to Banana API (interoperability)

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Handles interoperability by connecting to the Banana API.
 * This demonstrates how the application communicates with an
 * external web service to retrieve quiz questions.
 *
 * API Endpoint: https://marcconrad.com/uob/banana/api.php
 */
public class BananaAPIClient {

    // The API endpoint URL
    private static final String API_URL = "https://marcconrad.com/uob/banana/api.php";

    /**
     * Fetches a quiz question from the Banana API and prints the response.
     */
    public void fetchQuestion() {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        try {
            // Send the HTTP request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Successfully fetched question from Banana API!");
                System.out.println("Response Body:");
                System.out.println(response.body());
            } else {
                System.out.println("Failed to fetch question. HTTP Status: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error connecting to Banana API: " + e.getMessage());
        }
    }

    /**
     * A simple test method to demonstrate interoperability.
     */
    public static void main(String[] args) {
        BananaAPIClient apiClient = new BananaAPIClient();
        apiClient.fetchQuestion();
    }
}
