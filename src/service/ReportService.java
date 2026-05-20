package service;

import model.Appliance;
import model.AppData;
import model.CategorySummary;
import model.ClientTypePolicy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ReportService {

    public void exportToCSV(List<Appliance> appliances, String filename) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            writer.println("ID,Name,Category,Watts,Hours/Day,Peak Hrs,Days/Month,Quantity,kWh/Month");

            for (int i = 0; i < appliances.size(); i++) {
                Appliance app = appliances.get(i);
                writer.printf("%d,%s,%s,%.1f,%.1f,%.1f,%.1f,%d,%.2f%n",
                        app.getId(), app.getName(), app.getCategory(),
                        app.getWatts(), app.getHoursPerDay(),
                        app.getPeakHoursPerDay(),
                        app.getDaysPerMonth(), app.getQuantity(),
                        app.getMonthlyKwh());
            }

            writer.close();
            System.out.println("Exported to " + filename);
        } catch (IOException e) {
            System.out.println("Export error: " + e.getMessage());
        }
    }

    public void exportToTXT(AppData data, String filename) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            writeHeader(writer);
            writeAppliances(writer, data.getAppliances());
            writeCategories(writer, data.getCategorySummaries());
            writeTotals(writer, data);
            writer.close();
            System.out.println("Exported to " + filename);
        } catch (IOException e) {
            System.out.println("Export error: " + e.getMessage());
        }
    }

    private void writeHeader(PrintWriter writer) {
        writer.println("========================================");
        writer.println("       ELECTRICITY BILL SUMMARY");
        writer.println("========================================");
        writer.println();
    }

    private void writeAppliances(PrintWriter writer, List<Appliance> appliances) {
        writer.println("--- Appliances ---");
        writer.printf("%-20s %-15s %-10s %-6s%n",
                "Name", "Category", "kWh/Mon", "Qty");

        for (int i = 0; i < appliances.size(); i++) {
            Appliance app = appliances.get(i);
            writer.printf("%-20s %-15s %-10.2f %-6d%n",
                    app.getName(), app.getCategory(),
                    app.getMonthlyKwh(), app.getQuantity());
        }
        writer.println();
    }

    private void writeCategories(PrintWriter writer, List<CategorySummary> summaries) {
        writer.println("--- By Category ---");
        writer.printf("%-15s %-10s %-12s %-8s%n",
                "Category", "kWh", "Cost", "%");

        for (int i = 0; i < summaries.size(); i++) {
            CategorySummary cs = summaries.get(i);
            writer.printf("%-15s %-10.2f %-12.2f %-8.1f%n",
                    cs.getCategory(), cs.getTotalKwh(),
                    cs.getTotalCost(), cs.getPercentage());
        }
        writer.println();
    }

    private void writeTotals(PrintWriter writer, AppData data) {
        writer.printf("Total kWh:  %.2f%n", data.getTotalKwh());
        double peakKwh = data.getTotalPeakKwh();
        if (peakKwh > 0) {
            writer.printf("Peak kWh:   %.2f%n", peakKwh);
            writer.printf("Off-peak:   %.2f%n", data.getTotalKwh() - peakKwh);
            writer.printf("Surcharge:  +%.2f %s%n", data.getPeakSurcharge(), data.getCurrency());
        }
        writer.printf("Raw Cost:   %.2f %s%n", data.getRawCost(), data.getCurrency());

        String clientType = data.getClientType();
        String label = ClientTypePolicy.getDiscountLabel(clientType);
        writer.printf("Subsidy:    -%.2f %s  (%s)%n",
                data.getDiscount(), data.getCurrency(), label);
        writer.printf("Final Cost: %.2f %s%n", data.getTotalCost(), data.getCurrency());

        if (data.getMonthlyBudget() > 0) {
            writer.printf("Budget:     %.2f %s%n",
                    data.getMonthlyBudget(), data.getCurrency());
            if (data.isOverBudget()) {
                writer.println("*** WARNING: Over budget! ***");
            } else {
                writer.println("Within budget.");
            }
        }
    }
}
