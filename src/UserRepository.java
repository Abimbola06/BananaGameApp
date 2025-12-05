import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for users and their stats.
 */
public class UserRepository {

    /**
     * Registers a new user.
     * Returns true if success, false if username already exists or error.
     */
    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.executeUpdate();

            // Initialize stats row for this user
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    initStatsForUser(userId);
                }
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logs in a user. Returns the user ID if successful, or -1 if invalid.
     */
    public int login(String username, String password) {
        String sql = "SELECT id, password_hash FROM users WHERE username = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return -1; // no such user
                }

                int userId = rs.getInt("id");
                String storedHash = rs.getString("password_hash");

                if (storedHash.equals(hashPassword(password))) {
                    return userId; // login OK
                } else {
                    return -1; // wrong password
                }
            }

        } catch (SQLException e) {
            System.out.println("Error logging in: " + e.getMessage());
            return -1;
        }
    }

    private void initStatsForUser(int userId) throws SQLException {
        String sql = "INSERT INTO user_stats (user_id) VALUES (?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public void recordAttempt(int userId, boolean correct) {
        String sql = """
                UPDATE user_stats
                SET total_attempts = total_attempts + 1,
                    correct_attempts = correct_attempts + ?
                WHERE user_id = ?;
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, correct ? 1 : 0);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error recording attempt: " + e.getMessage());
        }
    }

    public UserStats getStats(int userId) {
        String sql = "SELECT total_attempts, correct_attempts FROM user_stats WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_attempts");
                    int correct = rs.getInt("correct_attempts");
                    return new UserStats(total, correct);
                } else {
                    return new UserStats(0, 0);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching stats: " + e.getMessage());
            return new UserStats(0, 0);
        }
    }

    public int getRank(int userId) {
        String sql = """
                SELECT user_id
                FROM user_stats
                ORDER BY correct_attempts DESC
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int rank = 1;
            while (rs.next()) {
                int id = rs.getInt("user_id");
                if (id == userId) {
                    return rank;
                }
                rank++;
            }
            return -1; // should not happen if stats exist

        } catch (SQLException e) {
            System.out.println("Error calculating rank: " + e.getMessage());
            return -1;
        }
    }

    // Simple record type for stats
    public static class UserStats {
        public final int totalAttempts;
        public final int correctAttempts;

        public UserStats(int totalAttempts, int correctAttempts) {
            this.totalAttempts = totalAttempts;
            this.correctAttempts = correctAttempts;
        }
    }

    public List<PlayerScore> getTopPlayers(int limit) {
        String sql = """
                SELECT u.username, s.correct_attempts
                FROM users u
                JOIN user_stats s ON u.id = s.user_id
                ORDER BY s.correct_attempts DESC
                LIMIT ?
                """;

        List<PlayerScore> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    int correct = rs.getInt("correct_attempts");
                    result.add(new PlayerScore(username, correct));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching top players: " + e.getMessage());
        }

        return result;
    }

    // simple record / DTO to hold leaderboard rows
    public static class PlayerScore {
        public final String username;
        public final int correctAttempts;

        public PlayerScore(String username, int correctAttempts) {
            this.username = username;
            this.correctAttempts = correctAttempts;
        }
    }
}
