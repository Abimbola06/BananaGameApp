// Manages authentication (virtual identity)

public class AuthManager {

    // "fake" login
    public boolean login(String username, String password) {
        // Next step is to integrate database connection
        return username != null && !username.isBlank()
                && password != null && !password.isBlank();
    }
}
