import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Responsible for connecting to the SQLite database
 * and creating tables if they do not exist.
 */
public class Database {

    private static final String DB_URL = "jdbc:sqlite:banana.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Call this once at application startup
     * to create tables if they don't exist.
     */
    public static void init() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // users table
            String createUsers = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        created_at TEXT DEFAULT CURRENT_TIMESTAMP
                    );
                    """;

            // user_stats table
            String createStats = """
                    CREATE TABLE IF NOT EXISTS user_stats (
                        user_id INTEGER PRIMARY KEY,
                        total_attempts INTEGER NOT NULL DEFAULT 0,
                        correct_attempts INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    );
                    """;

            stmt.execute(createUsers);
            stmt.execute(createStats);

            System.out.println("Database initialized.");

        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
}
