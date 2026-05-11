package model;

public class Appliance {
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

    public double getMonthlyKwh() {
        return (watts * hoursPerDay * daysPerMonth * quantity) / 1000.0;
    }

    public double getMonthlyCost(double rate) {
        return getMonthlyKwh() * rate;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getWatts() { return watts; }
    public double getHoursPerDay() { return hoursPerDay; }
    public double getDaysPerMonth() { return daysPerMonth; }
    public int getQuantity() { return quantity; }
}
