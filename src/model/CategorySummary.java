package model;

import java.util.ArrayList;
import java.util.List;

public class CategorySummary {
    private String category;
    private double totalKwh;
    private double totalCost;
    private double percentage;

    public CategorySummary(String category, double totalKwh,
                           double totalCost, double percentage) {
        this.category = category;
        this.totalKwh = totalKwh;
        this.totalCost = totalCost;
        this.percentage = percentage;
    }

    public static List<CategorySummary> buildSummaries(List<Appliance> appliances,
                                                       double overallCost) {
        List<String> categories = getUniqueCategories(appliances);
        double overallKwh = sumKwh(appliances);
        List<CategorySummary> summaries = new ArrayList<>();

        for (int i = 0; i < categories.size(); i++) {
            String cat = categories.get(i);
            CategorySummary summary = buildOne(cat, appliances, overallKwh, overallCost);
            summaries.add(summary);
        }
        return summaries;
    }

    private static List<String> getUniqueCategories(List<Appliance> appliances) {
        List<String> categories = new ArrayList<>();
        for (int i = 0; i < appliances.size(); i++) {
            String cat = appliances.get(i).getCategory();
            if (!categories.contains(cat)) {
                categories.add(cat);
            }
        }
        return categories;
    }

    private static double sumKwh(List<Appliance> appliances) {
        double total = 0;
        for (int i = 0; i < appliances.size(); i++) {
            total = total + appliances.get(i).getMonthlyKwh();
        }
        return total;
    }

    private static CategorySummary buildOne(String category, List<Appliance> appliances,
                                            double overallKwh, double overallCost) {
        double catKwh = 0;
        for (int i = 0; i < appliances.size(); i++) {
            if (appliances.get(i).getCategory().equals(category)) {
                catKwh = catKwh + appliances.get(i).getMonthlyKwh();
            }
        }
        double pct = 0;
        double catCost = 0;
        if (overallKwh > 0) {
            pct = (catKwh / overallKwh) * 100;
            catCost = (catKwh / overallKwh) * overallCost;
        }
        return new CategorySummary(category, catKwh, catCost, pct);
    }

    public String getCategory() { return category; }
    public double getTotalKwh() { return totalKwh; }
    public double getTotalCost() { return totalCost; }
    public double getPercentage() { return percentage; }
}
