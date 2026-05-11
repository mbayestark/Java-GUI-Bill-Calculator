package model;

import java.util.List;
import java.util.ArrayList;

public class AppData {
    private static AppData instance;

    private List<Appliance> appliances;
    private List<TariffTier> tariffs;
    private double flatRate;
    private String currency;
    private boolean useTieredPricing;

    private AppData() {
        appliances = new ArrayList<>();
        tariffs = new ArrayList<>();
        currency = "CFA";
        flatRate = 0.0;
        useTieredPricing = false;
    }

    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    public void addAppliance(Appliance appliance) {
        appliances.add(appliance);
    }

    public void addTariffTier(TariffTier tier) {
        tariffs.add(tier);
    }

    public double getTotalKwh() {
        double totalKwh = 0;
        for (Appliance appliance : appliances) {
            totalKwh += appliance.getMonthlyKwh();
        }
        return totalKwh;
    }

    public double getTotalCost() {
        double totalKwh = getTotalKwh();
        if (!useTieredPricing) {
            return totalKwh * flatRate;
        }
        double remaining = totalKwh;
        double cost = 0;
        double previousLimit = 0;
        for (TariffTier tier : tariffs) {
            double tierRange = tier.getUpToKwh() - previousLimit;
            if (remaining <= 0) break;
            double kwhInTier = Math.min(remaining, tierRange);
            cost += kwhInTier * tier.getRatePerKwh();
            remaining -= kwhInTier;
            previousLimit = tier.getUpToKwh();
        }
        return cost;
    }

    public List<Appliance> getAppliances() { return appliances; }
    public List<TariffTier> getTariffs() { return tariffs; }
    public double getFlatRate() { return flatRate; }
    public void setFlatRate(double flatRate) { this.flatRate = flatRate; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public boolean isUseTieredPricing() { return useTieredPricing; }
    public void setUseTieredPricing(boolean useTieredPricing) { this.useTieredPricing = useTieredPricing; }
}
