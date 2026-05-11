import dao.ApplianceDAO;
import dao.TariffTierDAO;
import model.AppData;
import model.Appliance;
import model.TariffTier;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ApplianceDAO applianceDAO = new ApplianceDAO();
        TariffTierDAO tariffTierDAO = new TariffTierDAO();
        AppData data = AppData.getInstance();
        Scanner scanner = new Scanner(System.in);

        try {
            List<TariffTier> tiers = tariffTierDAO.getAll();
            for (TariffTier tier : tiers) {
                data.addTariffTier(tier);
            }
            if (!tiers.isEmpty()) {
                data.setUseTieredPricing(true);
            }

            List<Appliance> savedAppliances = applianceDAO.getAll();
            for (Appliance appliance : savedAppliances) {
                data.addAppliance(appliance);
            }

            boolean running = true;
            while (running) {
                System.out.println("\n=== Electricity Bill Estimator ===");
                System.out.println("1. Add appliance");
                System.out.println("2. View appliances");
                System.out.println("3. View bill estimate");
                System.out.println("4. Delete appliance");
                System.out.println("5. Exit");
                System.out.print("Choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Category: ");
                        String category = scanner.nextLine();
                        System.out.print("Watts: ");
                        double watts = scanner.nextDouble();
                        System.out.print("Hours per day: ");
                        double hours = scanner.nextDouble();
                        System.out.print("Days per month: ");
                        double days = scanner.nextDouble();
                        System.out.print("Quantity: ");
                        int qty = scanner.nextInt();

                        Appliance appliance = new Appliance(name, category, watts, hours, days, qty);
                        applianceDAO.insert(appliance);
                        data.addAppliance(appliance);
                        System.out.println("Appliance added.");
                        break;

                    case 2:
                        List<Appliance> appliances = data.getAppliances();
                        if (appliances.isEmpty()) {
                            System.out.println("No appliances added yet.");
                        } else {
                            System.out.printf("%-4s %-20s %-15s %-10s %-10s %-10s %-8s %-10s%n",
                                    "ID", "Name", "Category", "Watts", "Hrs/Day", "Days/Mon", "Qty", "kWh/Mon");
                            for (Appliance a : appliances) {
                                System.out.printf("%-4d %-20s %-15s %-10.1f %-10.1f %-10.1f %-8d %-10.2f%n",
                                        a.getId(), a.getName(), a.getCategory(), a.getWatts(),
                                        a.getHoursPerDay(), a.getDaysPerMonth(), a.getQuantity(), a.getMonthlyKwh());
                            }
                        }
                        break;

                    case 3:
                        System.out.println("\n--- Bill Estimate ---");
                        System.out.printf("Total kWh: %.2f%n", data.getTotalKwh());
                        System.out.printf("Total Cost: %.2f %s%n", data.getTotalCost(), data.getCurrency());
                        break;

                    case 4:
                        System.out.print("Enter appliance ID to delete: ");
                        int id = scanner.nextInt();
                        Appliance toDelete = applianceDAO.get(id);
                        if (toDelete != null) {
                            applianceDAO.delete(toDelete);
                            data.getAppliances().removeIf(a -> a.getId() == id);
                            System.out.println("Appliance deleted.");
                        } else {
                            System.out.println("Appliance not found.");
                        }
                        break;

                    case 5:
                        running = false;
                        System.out.println("Goodbye.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
