package model;

public class Appliance {
    private int id;
    private String name;
    private String category;
    private double watts;
    private double hoursPerDay;
    private double daysPerMonth;
    private int quantity;

    public Appliance(String name, String category, double watts, double hoursPerDay, double daysPerMonth, int quantity) {
        this.name = name;
        this.category = category;
        this.watts = watts;
        this.hoursPerDay = hoursPerDay;
        this.daysPerMonth = daysPerMonth;
        this.quantity = quantity;
    }

    public Appliance(int id, String name, String category, double watts, double hoursPerDay, double daysPerMonth, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.watts = watts;
        this.hoursPerDay = hoursPerDay;
        this.daysPerMonth = daysPerMonth;
        this.quantity = quantity;
    }

    public double getMonthlyKwh() {
        return (watts * hoursPerDay * daysPerMonth * quantity) / 1000.0;
    }

    public double getMonthlyCost(double rate) {
        return getMonthlyKwh() * rate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getWatts() { return watts; }
    public double getHoursPerDay() { return hoursPerDay; }
    public double getDaysPerMonth() { return daysPerMonth; }
    public int getQuantity() { return quantity; }
}
