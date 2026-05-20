package model;

public class CommercialPlan extends TariffPlan {

    private static final double TAX_RATE = 0.18;

    public CommercialPlan(String name, double flatRate, boolean tiered) {
        super(name, "commercial", flatRate, tiered);
    }

    public CommercialPlan(int id, String name, double flatRate, boolean tiered) {
        super(id, name, "commercial", flatRate, tiered);
    }

    @Override
    public double calculateCost(double totalKwh) {
        double baseCost = totalKwh * getFlatRate();
        double tax = baseCost * TAX_RATE;
        return baseCost + tax;
    }
}
