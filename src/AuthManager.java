import java.util.List;

/**
 * AuthManager is a higher-level service for handling
 * user login, registration and stats for the application.
 * It wraps the lower-level UserRepository which talks to the database.
 */
public class AuthManager {

    private final UserRepository userRepository = new UserRepository();

    // Information about the currently logged-in user
    private Integer currentUserId = null;
    private String currentUsername = null;

    /**
     * Attempts to register a new user.
     *
     * @return true if registration was successful, false otherwise.
     */
    public boolean register(String username, String password) {
        boolean success = userRepository.register(username, password);
        if (success) {
            System.out.println("User registered: " + username);
        }
        return success;
    }

    /**
     * Attempts to log in an existing user.
     *
     * @return true if login successful, false otherwise.
     */
    public boolean login(String username, String password) {
        int userId = userRepository.login(username, password);
        if (userId != -1) {
            this.currentUserId = userId;
            this.currentUsername = username;
            System.out.println("Login successful for user: " + username + " (id=" + userId + ")");
            return true;
        } else {
            System.out.println("Login failed for user: " + username);
            return false;
        }
    }

    /**
     * Logs the current user out.
     */
    public void logout() {
        System.out.println("User logged out: " + currentUsername);
        this.currentUserId = null;
        this.currentUsername = null;
    }

    /**
     * Returns true if there is a logged-in user.
     */
    public boolean isLoggedIn() {
        return currentUserId != null;
    }

    /**
     * Returns the current user's id, or null if not logged in.
     */
    public Integer getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Returns the current username, or null if not logged in.
     */
    public String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Records a quiz attempt (correct or incorrect) for the current user.
     * Does nothing if no user is logged in.
     */
    public void recordAttempt(boolean correct) {
        if (currentUserId == null) {
            System.out.println("No logged-in user; cannot record attempt.");
            return;
        }
        userRepository.recordAttempt(currentUserId, correct);
    }

    /**
     * Returns the current user's stats, or a default (0,0) if not logged in.
     */
    public UserRepository.UserStats getCurrentUserStats() {
        if (currentUserId == null) {
            return new UserRepository.UserStats(0, 0);
        }
        return userRepository.getStats(currentUserId);
    }

    /**
     * Returns the current user's rank among all users,
     * or -1 if not logged in or error.
     */
    public int getCurrentUserRank() {
        if (currentUserId == null) {
            return -1;
        }
        return userRepository.getRank(currentUserId);
    }

    public List<UserRepository.PlayerScore> getTopPlayers(int limit) {
        return userRepository.getTopPlayers(limit);
    }
}
