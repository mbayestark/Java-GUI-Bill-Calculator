package model;

public class TariffTier {
    private double upToKwh;
    private double ratePerKwh;

    public TariffTier(double upToKwh, double ratePerKwh) {
        this.upToKwh = upToKwh;
        this.ratePerKwh = ratePerKwh;
    }

    public double getUpToKwh() { return upToKwh; }
    public double getRatePerKwh() { return ratePerKwh; }
}
