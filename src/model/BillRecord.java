package model;

public class BillRecord {
    private int id;
    private int userId;
    private int month;
    private int year;
    private double totalKwh;
    private double totalCost;

    public BillRecord(int userId, int month, int year,
                      double totalKwh, double totalCost) {
        this.setUserId(userId);
        this.setMonth(month);
        this.setYear(year);
        this.setTotalKwh(totalKwh);
        this.setTotalCost(totalCost);
    }

    public BillRecord(int id, int userId, int month, int year,
                      double totalKwh, double totalCost) {
        this.setId(id);
        this.setUserId(userId);
        this.setMonth(month);
        this.setYear(year);
        this.setTotalKwh(totalKwh);
        this.setTotalCost(totalCost);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMonth() { return month; }
    public void setMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        this.month = month;
    }

    public int getYear() { return year; }
    public void setYear(int year) {
        if (year < 2000) {
            throw new IllegalArgumentException("Year must be 2000 or later");
        }
        this.year = year;
    }

    public double getTotalKwh() { return totalKwh; }
    public void setTotalKwh(double totalKwh) {
        if (totalKwh < 0) {
            throw new IllegalArgumentException("kWh cannot be negative");
        }
        this.totalKwh = totalKwh;
    }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) {
        if (totalCost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        this.totalCost = totalCost;
    }
}
