package model;

public class Appliance {
    private int id;
    private int userId;
    private String name;
    private String category;
    private double watts;
    private double hoursPerDay;
    private double daysPerMonth;
    private int quantity;
    private double peakHoursPerDay;

    public Appliance(String name, String category, double watts,
                     double hoursPerDay, double daysPerMonth, int quantity) {
        this.setName(name);
        this.setCategory(category);
        this.setWatts(watts);
        this.setHoursPerDay(hoursPerDay);
        this.setDaysPerMonth(daysPerMonth);
        this.setQuantity(quantity);
        this.setPeakHoursPerDay(0);
    }

    public Appliance(int id, int userId, String name, String category,
                     double watts, double hoursPerDay, double daysPerMonth,
                     int quantity) {
        this.setId(id);
        this.setUserId(userId);
        this.setName(name);
        this.setCategory(category);
        this.setWatts(watts);
        this.setHoursPerDay(hoursPerDay);
        this.setDaysPerMonth(daysPerMonth);
        this.setQuantity(quantity);
        this.setPeakHoursPerDay(0);
    }

    public Appliance(int id, int userId, String name, String category,
                     double watts, double hoursPerDay, double daysPerMonth,
                     int quantity, double peakHoursPerDay) {
        this.setId(id);
        this.setUserId(userId);
        this.setName(name);
        this.setCategory(category);
        this.setWatts(watts);
        this.setHoursPerDay(hoursPerDay);
        this.setDaysPerMonth(daysPerMonth);
        this.setQuantity(quantity);
        this.setPeakHoursPerDay(peakHoursPerDay);
    }

    public double getMonthlyKwh() {
        return (watts * hoursPerDay * daysPerMonth * quantity) / 1000.0;
    }

    public double getMonthlyCost(double rate) {
        return getMonthlyKwh() * rate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getWatts() { return watts; }
    public void setWatts(double watts) {
        if (watts <= 0) {
            throw new IllegalArgumentException("Watts must be positive");
        }
        this.watts = watts;
    }

    public double getHoursPerDay() { return hoursPerDay; }
    public void setHoursPerDay(double hoursPerDay) {
        if (hoursPerDay <= 0 || hoursPerDay > 24) {
            throw new IllegalArgumentException("Hours must be between 0 and 24");
        }
        this.hoursPerDay = hoursPerDay;
    }

    public double getDaysPerMonth() { return daysPerMonth; }
    public void setDaysPerMonth(double daysPerMonth) {
        if (daysPerMonth < 1 || daysPerMonth > 31) {
            throw new IllegalArgumentException("Days must be between 1 and 31");
        }
        this.daysPerMonth = daysPerMonth;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }

    public double getPeakHoursPerDay() { return peakHoursPerDay; }
    public void setPeakHoursPerDay(double peakHoursPerDay) {
        if (peakHoursPerDay < 0 || peakHoursPerDay > 24) {
            throw new IllegalArgumentException("Peak hours must be between 0 and 24");
        }
        this.peakHoursPerDay = peakHoursPerDay;
    }

    public double getPeakKwh() {
        return (watts * peakHoursPerDay * daysPerMonth * quantity) / 1000.0;
    }

    public double getOffPeakKwh() {
        return getMonthlyKwh() - getPeakKwh();
    }
}
