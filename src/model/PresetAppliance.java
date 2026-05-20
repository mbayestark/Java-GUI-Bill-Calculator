package model;

public class PresetAppliance {
    private int id;
    private String name;
    private String category;
    private double defaultWatts;

    public PresetAppliance(String name, String category, double defaultWatts) {
        this.setName(name);
        this.setCategory(category);
        this.setDefaultWatts(defaultWatts);
    }

    public PresetAppliance(int id, String name, String category, double defaultWatts) {
        this.setId(id);
        this.setName(name);
        this.setCategory(category);
        this.setDefaultWatts(defaultWatts);
    }

    public Appliance toAppliance(double hoursPerDay, double daysPerMonth,
                                 int quantity, int userId) {
        Appliance appliance = new Appliance(name, category, defaultWatts,
                hoursPerDay, daysPerMonth, quantity);
        appliance.setUserId(userId);
        return appliance;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getDefaultWatts() { return defaultWatts; }
    public void setDefaultWatts(double defaultWatts) {
        if (defaultWatts <= 0) {
            throw new IllegalArgumentException("Watts must be positive");
        }
        this.defaultWatts = defaultWatts;
    }
}
