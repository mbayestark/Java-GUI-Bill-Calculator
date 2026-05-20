package model;

public class TariffTier {
    private int id;
    private int tariffPlanId;
    private double upToKwh;
    private double ratePerKwh;

    public TariffTier(double upToKwh, double ratePerKwh) {
        this.setUpToKwh(upToKwh);
        this.setRatePerKwh(ratePerKwh);
    }

    public TariffTier(int id, int tariffPlanId, double upToKwh, double ratePerKwh) {
        this.setId(id);
        this.setTariffPlanId(tariffPlanId);
        this.setUpToKwh(upToKwh);
        this.setRatePerKwh(ratePerKwh);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTariffPlanId() { return tariffPlanId; }
    public void setTariffPlanId(int tariffPlanId) {
        this.tariffPlanId = tariffPlanId;
    }

    public double getUpToKwh() { return upToKwh; }
    public void setUpToKwh(double upToKwh) {
        if (upToKwh <= 0) {
            throw new IllegalArgumentException("kWh threshold must be positive");
        }
        this.upToKwh = upToKwh;
    }

    public double getRatePerKwh() { return ratePerKwh; }
    public void setRatePerKwh(double ratePerKwh) {
        if (ratePerKwh <= 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
        this.ratePerKwh = ratePerKwh;
    }
}
