package service;

import model.BillRecord;
import model.TariffPlan;

import java.util.List;

public class BillPredictor {

    private static final double HISTORY_WEIGHT = 0.3;
    private static final double CURRENT_WEIGHT = 0.7;

    public double predictMonthlyKwh(double consumedKwh, int dayOfMonth,
                                    int daysInMonth, String clientType) {
        if (dayOfMonth <= 0) {
            return 0;
        }
        double dailyRate = consumedKwh / dayOfMonth;
        double factor = getCorrectionFactor(clientType);
        return dailyRate * daysInMonth * factor;
    }

    public double predictWithHistory(double consumedKwh, int dayOfMonth,
                                     int daysInMonth,
                                     List<BillRecord> pastRecords) {
        if (dayOfMonth <= 0) {
            return 0;
        }
        double projected = (consumedKwh / dayOfMonth) * daysInMonth;
        double historicalAvg = calculateHistoricalAverage(pastRecords);
        return (CURRENT_WEIGHT * projected) + (HISTORY_WEIGHT * historicalAvg);
    }

    public double predictMonthlyCost(double predictedKwh, TariffPlan plan) {
        if (plan == null) {
            return 0;
        }
        return plan.calculateCost(predictedKwh);
    }

    public boolean willExceedBudget(double predictedCost, double budget) {
        if (budget <= 0) {
            return false;
        }
        return predictedCost > budget;
    }

    public double predictedSurplus(double predictedCost, double budget) {
        return predictedCost - budget;
    }

    private double getCorrectionFactor(String clientType) {
        if (clientType == null) {
            return 1.0;
        }
        if ("hospital".equals(clientType)) {
            return 1.0;
        }
        if ("school".equals(clientType)) {
            return 1.15;
        }
        return 1.08;
    }

    private double calculateHistoricalAverage(List<BillRecord> records) {
        if (records.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (int i = 0; i < records.size(); i++) {
            sum = sum + records.get(i).getTotalKwh();
        }
        return sum / records.size();
    }
}
