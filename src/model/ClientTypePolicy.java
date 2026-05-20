package model;

public class ClientTypePolicy {

    public static double getDiscountRate(String clientType) {
        if (clientType == null) {
            return 0;
        }
        if ("hospital".equals(clientType)) {
            return 0.20;
        }
        if ("school".equals(clientType)) {
            return 0.15;
        }
        if ("individual".equals(clientType)) {
            return 0.05;
        }
        return 0;
    }

    public static String getDiscountLabel(String clientType) {
        if (clientType == null) {
            return "No subsidy";
        }
        if ("hospital".equals(clientType)) {
            return "Healthcare subsidy (20%)";
        }
        if ("school".equals(clientType)) {
            return "Education subsidy (15%)";
        }
        if ("individual".equals(clientType)) {
            return "Domestic subsidy (5%)";
        }
        return "No subsidy";
    }

    public static double applyDiscount(double cost, String clientType) {
        double rate = getDiscountRate(clientType);
        double discount = cost * rate;
        return cost - discount;
    }

    public static String getDescription(String clientType) {
        if (clientType == null) {
            return "Unknown";
        }
        if ("hospital".equals(clientType)) {
            return "Hospital — 24/7 essential service, healthcare subsidy applied";
        }
        if ("school".equals(clientType)) {
            return "School — Educational institution, education subsidy applied";
        }
        if ("individual".equals(clientType)) {
            return "Individual — Domestic household, domestic subsidy applied";
        }
        return "Unknown type";
    }
}
