package model;

import java.util.ArrayList;
import java.util.List;

public abstract class TariffPlan {
    private int id;
    private String name;
    private String planType;
    private double flatRate;
    private boolean tiered;
    private double peakMultiplier;
    private List<TariffTier> tiers;

    public TariffPlan(String name, String planType, double flatRate, boolean tiered) {
        this.setName(name);
        this.setPlanType(planType);
        this.setFlatRate(flatRate);
        this.setTiered(tiered);
        this.setPeakMultiplier(1.0);
        this.tiers = new ArrayList<>();
    }

    public TariffPlan(int id, String name, String planType,
                      double flatRate, boolean tiered) {
        this.setId(id);
        this.setName(name);
        this.setPlanType(planType);
        this.setFlatRate(flatRate);
        this.setTiered(tiered);
        this.setPeakMultiplier(1.0);
        this.tiers = new ArrayList<>();
    }

    public abstract double calculateCost(double totalKwh);

    public void addTier(TariffTier tier) {
        for (int i = 0; i < tiers.size(); i++) {
            if (tiers.get(i).getUpToKwh() == tier.getUpToKwh()) {
                throw new IllegalArgumentException(
                        "A tier with limit " + tier.getUpToKwh() + " kWh already exists");
            }
        }
        tiers.add(tier);
        sortTiers();
    }

    private void sortTiers() {
        for (int i = 0; i < tiers.size() - 1; i++) {
            for (int j = i + 1; j < tiers.size(); j++) {
                if (tiers.get(i).getUpToKwh() > tiers.get(j).getUpToKwh()) {
                    TariffTier temp = tiers.get(i);
                    tiers.set(i, tiers.get(j));
                    tiers.set(j, temp);
                }
            }
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Plan name cannot be empty");
        }
        this.name = name;
    }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public double getFlatRate() { return flatRate; }
    public void setFlatRate(double flatRate) {
        if (flatRate < 0) {
            throw new IllegalArgumentException("Flat rate cannot be negative");
        }
        this.flatRate = flatRate;
    }

    public boolean isTiered() { return tiered; }
    public void setTiered(boolean tiered) { this.tiered = tiered; }

    public double getPeakMultiplier() { return peakMultiplier; }
    public void setPeakMultiplier(double peakMultiplier) {
        if (peakMultiplier < 1.0) {
            throw new IllegalArgumentException("Peak multiplier must be at least 1.0");
        }
        this.peakMultiplier = peakMultiplier;
    }

    public List<TariffTier> getTiers() { return tiers; }
    public void setTiers(List<TariffTier> tiers) { this.tiers = tiers; }
}
