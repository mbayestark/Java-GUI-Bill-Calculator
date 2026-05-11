package model;

public class TariffTier {
    private int id;
    private double upToKwh;
    private double ratePerKwh;

    public TariffTier(double upToKwh, double ratePerKwh) {
        this.upToKwh = upToKwh;
        this.ratePerKwh = ratePerKwh;
    }

    public TariffTier(int id, double upToKwh, double ratePerKwh) {
        this.id = id;
        this.upToKwh = upToKwh;
        this.ratePerKwh = ratePerKwh;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getUpToKwh() { return upToKwh; }
    public double getRatePerKwh() { return ratePerKwh; }
}
