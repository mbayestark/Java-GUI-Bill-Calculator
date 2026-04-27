package model;

public class TariffTier {
    private double uPpToKwh;
    private double ratePerKwh;

    public double getRate() {
        return uPpToKwh*ratePerKwh;
    }
}
