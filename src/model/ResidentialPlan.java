package model;

import java.util.List;

public class ResidentialPlan extends TariffPlan {

    public ResidentialPlan(String name, double flatRate, boolean tiered) {
        super(name, "residential", flatRate, tiered);
    }

    public ResidentialPlan(int id, String name, double flatRate, boolean tiered) {
        super(id, name, "residential", flatRate, tiered);
    }

    @Override
    public double calculateCost(double totalKwh) {
        if (!isTiered()) {
            return totalKwh * getFlatRate();
        }
        return calculateTieredCost(totalKwh);
    }

    private double calculateTieredCost(double totalKwh) {
        double remaining = totalKwh;
        double cost = 0;
        double previousLimit = 0;
        List<TariffTier> tiers = getTiers();

        for (int i = 0; i < tiers.size(); i++) {
            if (remaining <= 0) {
                break;
            }
            TariffTier tier = tiers.get(i);
            double tierRange = tier.getUpToKwh() - previousLimit;
            double kwhInTier = Math.min(remaining, tierRange);
            cost = cost + (kwhInTier * tier.getRatePerKwh());
            remaining = remaining - kwhInTier;
            previousLimit = tier.getUpToKwh();
        }
        return cost;
    }
}
