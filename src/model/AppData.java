package model;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private static AppData instance;

    private List<Appliance> appliances;
    private TariffPlan tariffPlan;
    private String currency;
    private double monthlyBudget;
    private String clientType;

    private AppData() {
        this.appliances = new ArrayList<>();
        this.tariffPlan = null;
        this.currency = "CFA";
        this.monthlyBudget = 0;
        this.clientType = null;
    }

    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    public void clear() {
        appliances.clear();
        tariffPlan = null;
        monthlyBudget = 0;
        clientType = null;
    }

    public void addAppliance(Appliance appliance) {
        appliances.add(appliance);
    }

    public double getTotalKwh() {
        double total = 0;
        for (int i = 0; i < appliances.size(); i++) {
            total = total + appliances.get(i).getMonthlyKwh();
        }
        return total;
    }

    public double getTotalPeakKwh() {
        double total = 0;
        for (int i = 0; i < appliances.size(); i++) {
            total = total + appliances.get(i).getPeakKwh();
        }
        return total;
    }

    public double getPeakSurcharge() {
        if (tariffPlan == null) {
            return 0;
        }
        double peakKwh = getTotalPeakKwh();
        double multiplier = tariffPlan.getPeakMultiplier();
        return peakKwh * tariffPlan.getFlatRate() * (multiplier - 1.0);
    }

    public double getRawCost() {
        if (tariffPlan == null) {
            return 0;
        }
        double baseCost = tariffPlan.calculateCost(getTotalKwh());
        double peakSurcharge = getPeakSurcharge();
        return baseCost + peakSurcharge;
    }

    public double getDiscount() {
        return getRawCost() * ClientTypePolicy.getDiscountRate(clientType);
    }

    public double getTotalCost() {
        return ClientTypePolicy.applyDiscount(getRawCost(), clientType);
    }

    public boolean isOverBudget() {
        if (monthlyBudget <= 0) {
            return false;
        }
        return getTotalCost() > monthlyBudget;
    }

    public List<CategorySummary> getCategorySummaries() {
        return CategorySummary.buildSummaries(appliances, getTotalCost());
    }

    public List<Appliance> getAppliances() { return appliances; }

    public TariffPlan getTariffPlan() { return tariffPlan; }
    public void setTariffPlan(TariffPlan tariffPlan) {
        this.tariffPlan = tariffPlan;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public String getClientType() { return clientType; }
    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
