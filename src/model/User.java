package model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role;
    private String clientType;
    private String fullName;
    private double monthlyBudget;
    private int tariffPlanId;
    private int capacity;

    public User(String username, String passwordHash, String role,
                String clientType, String fullName) {
        this.setUsername(username);
        this.setPasswordHash(passwordHash);
        this.setRole(role);
        this.setClientType(clientType);
        this.setFullName(fullName);
        this.monthlyBudget = 0;
        this.tariffPlanId = 0;
        this.setCapacity(1);
    }

    public User(int id, String username, String passwordHash, String role,
                String clientType, String fullName, double monthlyBudget,
                int tariffPlanId, int capacity) {
        this.setId(id);
        this.setUsername(username);
        this.setPasswordHash(passwordHash);
        this.setRole(role);
        this.setClientType(clientType);
        this.setFullName(fullName);
        this.setMonthlyBudget(monthlyBudget);
        this.setTariffPlanId(tariffPlanId);
        this.setCapacity(capacity);
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getClientType() { return clientType; }
    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(double monthlyBudget) {
        if (monthlyBudget < 0) {
            throw new IllegalArgumentException("Budget cannot be negative");
        }
        this.monthlyBudget = monthlyBudget;
    }

    public int getTariffPlanId() { return tariffPlanId; }
    public void setTariffPlanId(int tariffPlanId) {
        this.tariffPlanId = tariffPlanId;
    }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) {
        if (capacity < 1) {
            capacity = 1;
        }
        this.capacity = capacity;
    }
}
