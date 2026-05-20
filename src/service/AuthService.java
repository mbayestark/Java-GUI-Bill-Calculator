package service;

import dao.UserDAO;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

public class AuthService {

    private UserDAO userDAO;
    private BaseApplianceService baseApplianceService;
    private String lastError;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.baseApplianceService = new BaseApplianceService();
        this.lastError = "";
    }

    public String getLastError() {
        return lastError;
    }

    public User login(String username, String password) {
        try {
            User user = userDAO.findByUsername(username);
            if (user == null) {
                System.out.println("User not found.");
                return null;
            }
            boolean match = BCrypt.checkpw(password, user.getPasswordHash());
            if (!match) {
                System.out.println("Wrong password.");
                return null;
            }
            return user;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return null;
        }
    }

    public User register(String username, String password, String fullName,
                         String role, String clientType, int capacity) {
        if (fullName == null || fullName.trim().isEmpty()) {
            lastError = "Full name is required.";
            return null;
        }
        if (username == null || username.trim().isEmpty()) {
            lastError = "Username is required.";
            return null;
        }
        if (password.length() < 6) {
            lastError = "Password must be at least 6 characters.";
            return null;
        }
        if (!isValidClientType(clientType)) {
            lastError = "Type must be: individual, school, or hospital.";
            return null;
        }
        try {
            User existing = userDAO.findByUsername(username);
            if (existing != null) {
                lastError = "Username '" + username + "' is already taken.";
                return null;
            }
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            User newUser = new User(username, hash, role, clientType, fullName);
            newUser.setCapacity(capacity);
            userDAO.insert(newUser);
            baseApplianceService.seedAppliances(newUser);
            return newUser;
        } catch (SQLException e) {
            lastError = "Database error: " + e.getMessage();
            return null;
        }
    }

    private boolean isValidClientType(String clientType) {
        if (clientType == null || clientType.trim().isEmpty()) {
            return false;
        }
        if ("individual".equals(clientType)) { return true; }
        if ("school".equals(clientType)) { return true; }
        if ("hospital".equals(clientType)) { return true; }
        return false;
    }

    public void seedAdminIfNeeded() {
        try {
            List<User> users = userDAO.getAll();
            if (users.isEmpty()) {
                String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());
                User admin = new User("admin", hash, "admin", null, "System Admin");
                userDAO.insert(admin);
                System.out.println("Default admin created (username: admin, password: admin123)");
            }
        } catch (SQLException e) {
            System.out.println("Error checking for admin: " + e.getMessage());
        }
    }
}
