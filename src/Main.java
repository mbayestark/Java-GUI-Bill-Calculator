import dao.ApplianceDAO;
import dao.PresetApplianceDAO;
import dao.TariffPlanDAO;
import dao.TariffTierDAO;
import dao.UserDAO;
import model.AppData;
import model.Appliance;
import model.CategorySummary;
import model.CommercialPlan;
import model.PresetAppliance;
import model.ResidentialPlan;
import model.Session;
import model.TariffPlan;
import model.TariffTier;
import model.User;
import service.AuthService;
import service.ReportService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static AuthService authService = new AuthService();
    private static ApplianceDAO applianceDAO = new ApplianceDAO();
    private static TariffPlanDAO tariffPlanDAO = new TariffPlanDAO();
    private static TariffTierDAO tariffTierDAO = new TariffTierDAO();
    private static UserDAO userDAO = new UserDAO();
    private static PresetApplianceDAO presetDAO = new PresetApplianceDAO();
    private static ReportService reportService = new ReportService();
    private static AppData data = AppData.getInstance();
    private static Session session = Session.getInstance();

    public static void main(String[] args) {
        authService.seedAdminIfNeeded();
        System.out.println("=== Electricity Bill Estimator ===");

        boolean running = true;
        while (running) {
            running = showLoginMenu();
        }
        scanner.close();
        System.out.println("Goodbye.");
    }

    // ---- Login / Register ----

    private static boolean showLoginMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choice: ");
        int choice = readInt();

        switch (choice) {
            case 1: handleLogin(); break;
            case 2: handleRegister(); break;
            case 3: return false;
            default: System.out.println("Invalid choice."); break;
        }
        return true;
    }

    private static void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = authService.login(username, password);
        if (user == null) {
            return;
        }

        session.setCurrentUser(user);
        if (user.isAdmin()) {
            showAdminMenu();
        } else {
            loadUserData(user);
            showClientMenu();
        }
    }

    private static void handleRegister() {
        System.out.print("Full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password (min 6 chars): ");
        String password = scanner.nextLine();
        System.out.print("Client type (individual/school/hospital): ");
        String clientType = scanner.nextLine();

        User user = authService.register(username, password, fullName,
                "client", clientType, 1);
        if (user != null) {
            System.out.println("Registration successful. Please login.");
        }
    }

    private static void loadUserData(User user) {
        data.clear();
        data.setMonthlyBudget(user.getMonthlyBudget());
        try {
            loadTariffPlan(user);
            loadAppliances(user);
        } catch (SQLException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private static void loadTariffPlan(User user) throws SQLException {
        if (user.getTariffPlanId() > 0) {
            TariffPlan plan = tariffPlanDAO.get(user.getTariffPlanId());
            data.setTariffPlan(plan);
        }
    }

    private static void loadAppliances(User user) throws SQLException {
        List<Appliance> appliances = applianceDAO.getByUserId(user.getId());
        for (int i = 0; i < appliances.size(); i++) {
            data.addAppliance(appliances.get(i));
        }
    }

    // ---- Admin Menu ----

    private static void showAdminMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Admin Panel ===");
            System.out.println("1. Manage clients");
            System.out.println("2. Manage tariff plans");
            System.out.println("3. Manage preset appliances");
            System.out.println("4. View all bills");
            System.out.println("5. Logout");
            System.out.print("Choice: ");
            int choice = readInt();

            switch (choice) {
                case 1: manageClients(); break;
                case 2: manageTariffPlans(); break;
                case 3: managePresets(); break;
                case 4: viewAllBills(); break;
                case 5: running = false; break;
                default: System.out.println("Invalid choice."); break;
            }
        }
        logout();
    }

    // ---- Client Management ----

    private static void manageClients() {
        System.out.println("\n--- Manage Clients ---");
        System.out.println("1. View all clients");
        System.out.println("2. Assign tariff plan to client");
        System.out.println("3. Delete client");
        System.out.println("4. Back");
        System.out.print("Choice: ");
        int choice = readInt();

        switch (choice) {
            case 1: viewAllClients(); break;
            case 2: assignTariffPlan(); break;
            case 3: deleteClient(); break;
            default: break;
        }
    }

    private static void viewAllClients() {
        try {
            List<User> clients = userDAO.getAllClients();
            if (clients.isEmpty()) {
                System.out.println("No clients registered.");
                return;
            }
            System.out.printf("%-4s %-15s %-20s %-12s %-10s%n",
                    "ID", "Username", "Full Name", "Type", "Plan ID");
            for (int i = 0; i < clients.size(); i++) {
                User client = clients.get(i);
                System.out.printf("%-4d %-15s %-20s %-12s %-10d%n",
                        client.getId(), client.getUsername(),
                        client.getFullName(), client.getClientType(),
                        client.getTariffPlanId());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void assignTariffPlan() {
        try {
            viewAllClients();
            System.out.print("Client ID: ");
            int clientId = readInt();
            User client = userDAO.get(clientId);
            if (client == null) {
                System.out.println("Client not found.");
                return;
            }
            viewAllTariffPlans();
            System.out.print("Plan ID: ");
            int planId = readInt();
            client.setTariffPlanId(planId);
            userDAO.update(client);
            System.out.println("Plan assigned.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteClient() {
        try {
            System.out.print("Client ID to delete: ");
            int clientId = readInt();
            User client = userDAO.get(clientId);
            if (client == null) {
                System.out.println("Client not found.");
                return;
            }
            userDAO.delete(client);
            System.out.println("Client deleted.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---- Tariff Plan Management ----

    private static void manageTariffPlans() {
        System.out.println("\n--- Manage Tariff Plans ---");
        System.out.println("1. View all plans");
        System.out.println("2. Add plan");
        System.out.println("3. Add tier to plan");
        System.out.println("4. Delete plan");
        System.out.println("5. Back");
        System.out.print("Choice: ");
        int choice = readInt();

        switch (choice) {
            case 1: viewAllTariffPlans(); break;
            case 2: addTariffPlan(); break;
            case 3: addTierToPlan(); break;
            case 4: deleteTariffPlan(); break;
            default: break;
        }
    }

    private static void viewAllTariffPlans() {
        try {
            List<TariffPlan> plans = tariffPlanDAO.getAll();
            if (plans.isEmpty()) {
                System.out.println("No tariff plans.");
                return;
            }
            System.out.printf("%-4s %-20s %-12s %-10s %-8s%n",
                    "ID", "Name", "Type", "Flat Rate", "Tiered");
            for (int i = 0; i < plans.size(); i++) {
                printTariffPlanRow(plans.get(i));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printTariffPlanRow(TariffPlan plan) {
        String tieredStr;
        if (plan.isTiered()) {
            tieredStr = "Yes";
        } else {
            tieredStr = "No";
        }
        System.out.printf("%-4d %-20s %-12s %-10.2f %-8s%n",
                plan.getId(), plan.getName(), plan.getPlanType(),
                plan.getFlatRate(), tieredStr);
    }

    private static void addTariffPlan() {
        try {
            TariffPlan plan = promptNewTariffPlan();
            tariffPlanDAO.insert(plan);
            System.out.println("Plan created with ID " + plan.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static TariffPlan promptNewTariffPlan() {
        System.out.print("Plan name: ");
        String name = scanner.nextLine();
        System.out.print("Type (residential/commercial): ");
        String type = scanner.nextLine();
        System.out.print("Flat rate per kWh: ");
        double flatRate = readDouble();
        System.out.print("Use tiered pricing? (yes/no): ");
        String tieredInput = scanner.nextLine();
        boolean tiered = tieredInput.equalsIgnoreCase("yes");

        if ("commercial".equals(type)) {
            return new CommercialPlan(name, flatRate, tiered);
        } else {
            return new ResidentialPlan(name, flatRate, tiered);
        }
    }

    private static void addTierToPlan() {
        try {
            viewAllTariffPlans();
            System.out.print("Plan ID: ");
            int planId = readInt();
            System.out.print("Up to kWh: ");
            double upToKwh = readDouble();
            System.out.print("Rate per kWh: ");
            double rate = readDouble();

            TariffTier tier = new TariffTier(upToKwh, rate);
            tier.setTariffPlanId(planId);
            tariffTierDAO.insert(tier);
            System.out.println("Tier added.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteTariffPlan() {
        try {
            System.out.print("Plan ID to delete: ");
            int planId = readInt();
            TariffPlan plan = tariffPlanDAO.get(planId);
            if (plan == null) {
                System.out.println("Plan not found.");
                return;
            }
            tariffPlanDAO.delete(plan);
            System.out.println("Plan deleted.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---- Preset Management ----

    private static void managePresets() {
        System.out.println("\n--- Manage Presets ---");
        System.out.println("1. View all presets");
        System.out.println("2. Add preset");
        System.out.println("3. Delete preset");
        System.out.println("4. Back");
        System.out.print("Choice: ");
        int choice = readInt();

        switch (choice) {
            case 1: viewAllPresets(); break;
            case 2: addPreset(); break;
            case 3: deletePreset(); break;
            default: break;
        }
    }

    private static void viewAllPresets() {
        try {
            List<PresetAppliance> presets = presetDAO.getAll();
            if (presets.isEmpty()) {
                System.out.println("No presets.");
                return;
            }
            System.out.printf("%-4s %-20s %-15s %-10s%n",
                    "ID", "Name", "Category", "Watts");
            for (int i = 0; i < presets.size(); i++) {
                PresetAppliance preset = presets.get(i);
                System.out.printf("%-4d %-20s %-15s %-10.0f%n",
                        preset.getId(), preset.getName(),
                        preset.getCategory(), preset.getDefaultWatts());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addPreset() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Category: ");
            String category = scanner.nextLine();
            System.out.print("Default watts: ");
            double watts = readDouble();

            PresetAppliance preset = new PresetAppliance(name, category, watts);
            presetDAO.insert(preset);
            System.out.println("Preset added.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deletePreset() {
        try {
            System.out.print("Preset ID to delete: ");
            int presetId = readInt();
            PresetAppliance preset = presetDAO.get(presetId);
            if (preset == null) {
                System.out.println("Preset not found.");
                return;
            }
            presetDAO.delete(preset);
            System.out.println("Preset deleted.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---- View All Bills (Admin) ----

    private static void viewAllBills() {
        try {
            List<User> clients = userDAO.getAllClients();
            if (clients.isEmpty()) {
                System.out.println("No clients.");
                return;
            }
            System.out.printf("%-4s %-15s %-10s %-12s%n",
                    "ID", "Username", "kWh", "Cost");
            for (int i = 0; i < clients.size(); i++) {
                printClientBillRow(clients.get(i));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printClientBillRow(User client) throws SQLException {
        List<Appliance> appliances = applianceDAO.getByUserId(client.getId());
        double totalKwh = 0;
        for (int i = 0; i < appliances.size(); i++) {
            totalKwh = totalKwh + appliances.get(i).getMonthlyKwh();
        }
        double totalCost = calculateClientCost(client, totalKwh);
        System.out.printf("%-4d %-15s %-10.2f %-12.2f%n",
                client.getId(), client.getUsername(), totalKwh, totalCost);
    }

    private static double calculateClientCost(User client,
                                              double totalKwh) throws SQLException {
        if (client.getTariffPlanId() <= 0) {
            return 0;
        }
        TariffPlan plan = tariffPlanDAO.get(client.getTariffPlanId());
        if (plan == null) {
            return 0;
        }
        return plan.calculateCost(totalKwh);
    }

    // ---- Client Menu ----

    private static void showClientMenu() {
        boolean running = true;
        while (running) {
            printClientMenuOptions();
            int choice = readInt();
            running = handleClientChoice(choice);
        }
        logout();
    }

    private static void printClientMenuOptions() {
        System.out.println("\n=== My Dashboard ===");
        System.out.println("1. Add appliance");
        System.out.println("2. Add from preset");
        System.out.println("3. View appliances");
        System.out.println("4. Edit appliance");
        System.out.println("5. Delete appliance");
        System.out.println("6. View bill breakdown");
        System.out.println("7. View category summary");
        System.out.println("8. Set monthly budget");
        System.out.println("9. Export report");
        System.out.println("10. Logout");
        System.out.print("Choice: ");
    }

    private static boolean handleClientChoice(int choice) {
        switch (choice) {
            case 1: addAppliance(); break;
            case 2: addFromPreset(); break;
            case 3: viewAppliances(); break;
            case 4: editAppliance(); break;
            case 5: deleteAppliance(); break;
            case 6: viewBillBreakdown(); break;
            case 7: viewCategorySummary(); break;
            case 8: setMonthlyBudget(); break;
            case 9: exportReport(); break;
            case 10: return false;
            default: System.out.println("Invalid choice."); break;
        }
        return true;
    }

    // ---- Client: Add Appliance ----

    private static void addAppliance() {
        try {
            Appliance appliance = promptNewAppliance();
            User currentUser = session.getCurrentUser();
            appliance.setUserId(currentUser.getId());
            applianceDAO.insert(appliance);
            data.addAppliance(appliance);
            System.out.println("Appliance added.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Appliance promptNewAppliance() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Category: ");
        String category = scanner.nextLine();
        System.out.print("Watts: ");
        double watts = readDouble();
        System.out.print("Hours per day: ");
        double hours = readDouble();
        System.out.print("Days per month: ");
        double days = readDouble();
        System.out.print("Quantity: ");
        int qty = readInt();
        return new Appliance(name, category, watts, hours, days, qty);
    }

    // ---- Client: Add From Preset ----

    private static void addFromPreset() {
        try {
            PresetAppliance preset = selectPreset();
            if (preset == null) {
                return;
            }
            Appliance appliance = createFromPreset(preset);
            applianceDAO.insert(appliance);
            data.addAppliance(appliance);
            System.out.println("Appliance added from preset.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static PresetAppliance selectPreset() throws SQLException {
        viewAllPresets();
        System.out.print("Preset ID: ");
        int presetId = readInt();
        PresetAppliance preset = presetDAO.get(presetId);
        if (preset == null) {
            System.out.println("Preset not found.");
        }
        return preset;
    }

    private static Appliance createFromPreset(PresetAppliance preset) {
        System.out.println("Selected: " + preset.getName()
                + " (" + preset.getDefaultWatts() + "W)");
        System.out.print("Hours per day: ");
        double hours = readDouble();
        System.out.print("Days per month: ");
        double days = readDouble();
        System.out.print("Quantity: ");
        int qty = readInt();

        User currentUser = session.getCurrentUser();
        int userId = currentUser.getId();
        return preset.toAppliance(hours, days, qty, userId);
    }

    // ---- Client: View Appliances ----

    private static void viewAppliances() {
        List<Appliance> appliances = data.getAppliances();
        if (appliances.isEmpty()) {
            System.out.println("No appliances added yet.");
            return;
        }
        System.out.printf("%-4s %-20s %-15s %-10s %-10s %-10s %-6s %-10s%n",
                "ID", "Name", "Category", "Watts", "Hrs/Day",
                "Days/Mon", "Qty", "kWh/Mon");
        for (int i = 0; i < appliances.size(); i++) {
            printApplianceRow(appliances.get(i));
        }
    }

    private static void printApplianceRow(Appliance app) {
        System.out.printf("%-4d %-20s %-15s %-10.1f %-10.1f %-10.1f %-6d %-10.2f%n",
                app.getId(), app.getName(), app.getCategory(),
                app.getWatts(), app.getHoursPerDay(), app.getDaysPerMonth(),
                app.getQuantity(), app.getMonthlyKwh());
    }

    // ---- Client: Edit Appliance ----

    private static void editAppliance() {
        try {
            viewAppliances();
            System.out.print("Appliance ID to edit: ");
            int appId = readInt();
            Appliance appliance = findApplianceById(appId);
            if (appliance == null) {
                System.out.println("Appliance not found.");
                return;
            }
            promptEditAppliance(appliance);
            applianceDAO.update(appliance);
            System.out.println("Appliance updated.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Appliance findApplianceById(int appId) {
        List<Appliance> appliances = data.getAppliances();
        for (int i = 0; i < appliances.size(); i++) {
            if (appliances.get(i).getId() == appId) {
                return appliances.get(i);
            }
        }
        return null;
    }

    private static void promptEditAppliance(Appliance appliance) {
        System.out.println("Leave blank to keep current value.");

        System.out.print("Name [" + appliance.getName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            appliance.setName(name);
        }

        System.out.print("Category [" + appliance.getCategory() + "]: ");
        String category = scanner.nextLine();
        if (!category.isEmpty()) {
            appliance.setCategory(category);
        }

        editNumericFields(appliance);
    }

    private static void editNumericFields(Appliance appliance) {
        System.out.print("Watts [" + appliance.getWatts() + "]: ");
        String wattsStr = scanner.nextLine();
        if (!wattsStr.isEmpty()) {
            appliance.setWatts(Double.parseDouble(wattsStr));
        }

        System.out.print("Hours/day [" + appliance.getHoursPerDay() + "]: ");
        String hoursStr = scanner.nextLine();
        if (!hoursStr.isEmpty()) {
            appliance.setHoursPerDay(Double.parseDouble(hoursStr));
        }

        System.out.print("Days/month [" + appliance.getDaysPerMonth() + "]: ");
        String daysStr = scanner.nextLine();
        if (!daysStr.isEmpty()) {
            appliance.setDaysPerMonth(Double.parseDouble(daysStr));
        }

        System.out.print("Quantity [" + appliance.getQuantity() + "]: ");
        String qtyStr = scanner.nextLine();
        if (!qtyStr.isEmpty()) {
            appliance.setQuantity(Integer.parseInt(qtyStr));
        }
    }

    // ---- Client: Delete Appliance ----

    private static void deleteAppliance() {
        try {
            System.out.print("Appliance ID to delete: ");
            int appId = readInt();
            Appliance appliance = applianceDAO.get(appId);
            if (appliance == null) {
                System.out.println("Appliance not found.");
                return;
            }
            applianceDAO.delete(appliance);
            removeApplianceFromData(appId);
            System.out.println("Appliance deleted.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeApplianceFromData(int appId) {
        List<Appliance> appliances = data.getAppliances();
        for (int i = 0; i < appliances.size(); i++) {
            if (appliances.get(i).getId() == appId) {
                appliances.remove(i);
                break;
            }
        }
    }

    // ---- Client: Bill Breakdown ----

    private static void viewBillBreakdown() {
        System.out.println("\n--- Bill Breakdown ---");
        if (data.getTariffPlan() == null) {
            System.out.println("No tariff plan assigned. Contact admin.");
            return;
        }
        printApplianceKwhList();
        printTierBreakdown();
        printBillTotal();
    }

    private static void printApplianceKwhList() {
        List<Appliance> appliances = data.getAppliances();
        System.out.printf("%-20s %-15s %-10s%n", "Name", "Category", "kWh/Mon");
        for (int i = 0; i < appliances.size(); i++) {
            Appliance app = appliances.get(i);
            System.out.printf("%-20s %-15s %-10.2f%n",
                    app.getName(), app.getCategory(), app.getMonthlyKwh());
        }
        System.out.println();
    }

    private static void printTierBreakdown() {
        TariffPlan plan = data.getTariffPlan();
        System.out.println("Plan: " + plan.getName()
                + " (" + plan.getPlanType() + ")");

        if (plan instanceof ResidentialPlan && plan.isTiered()) {
            printResidentialTiers();
        } else if (plan instanceof CommercialPlan) {
            System.out.printf("Rate: %.2f CFA/kWh + 18%% tax%n",
                    plan.getFlatRate());
        } else {
            System.out.printf("Flat rate: %.2f CFA/kWh%n", plan.getFlatRate());
        }
        System.out.println();
    }

    private static void printResidentialTiers() {
        double remaining = data.getTotalKwh();
        double previousLimit = 0;
        TariffPlan plan = data.getTariffPlan();
        List<TariffTier> tiers = plan.getTiers();

        for (int i = 0; i < tiers.size(); i++) {
            if (remaining <= 0) {
                break;
            }
            TariffTier tier = tiers.get(i);
            double tierRange = tier.getUpToKwh() - previousLimit;
            double kwhInTier = Math.min(remaining, tierRange);
            double tierCost = kwhInTier * tier.getRatePerKwh();
            System.out.printf("  Up to %.0f kWh: %.2f kWh x %.2f = %.2f CFA%n",
                    tier.getUpToKwh(), kwhInTier, tier.getRatePerKwh(), tierCost);
            remaining = remaining - kwhInTier;
            previousLimit = tier.getUpToKwh();
        }
    }

    private static void printBillTotal() {
        System.out.printf("Total kWh:  %.2f%n", data.getTotalKwh());
        System.out.printf("Total Cost: %.2f %s%n",
                data.getTotalCost(), data.getCurrency());

        if (data.isOverBudget()) {
            double budget = data.getMonthlyBudget();
            System.out.println("*** WARNING: Bill exceeds budget of "
                    + String.format("%.2f", budget) + " "
                    + data.getCurrency() + "! ***");
        }
    }

    // ---- Client: Category Summary ----

    private static void viewCategorySummary() {
        List<CategorySummary> summaries = data.getCategorySummaries();
        if (summaries.isEmpty()) {
            System.out.println("No appliances to summarize.");
            return;
        }
        System.out.println("\n--- Category Summary ---");
        System.out.printf("%-15s %-10s %-12s %-8s%n",
                "Category", "kWh", "Cost", "%");
        for (int i = 0; i < summaries.size(); i++) {
            CategorySummary cs = summaries.get(i);
            System.out.printf("%-15s %-10.2f %-12.2f %-8.1f%%%n",
                    cs.getCategory(), cs.getTotalKwh(),
                    cs.getTotalCost(), cs.getPercentage());
        }
    }

    // ---- Client: Budget ----

    private static void setMonthlyBudget() {
        System.out.print("Monthly budget (" + data.getCurrency() + "): ");
        double budget = readDouble();
        data.setMonthlyBudget(budget);

        User user = session.getCurrentUser();
        user.setMonthlyBudget(budget);
        try {
            userDAO.update(user);
            System.out.println("Budget set to "
                    + String.format("%.2f", budget) + " " + data.getCurrency());
        } catch (SQLException e) {
            System.out.println("Error saving budget: " + e.getMessage());
        }
    }

    // ---- Client: Export ----

    private static void exportReport() {
        System.out.println("1. Export to CSV");
        System.out.println("2. Export to TXT");
        System.out.print("Choice: ");
        int choice = readInt();

        User currentUser = session.getCurrentUser();
        String username = currentUser.getUsername();

        if (choice == 1) {
            reportService.exportToCSV(data.getAppliances(),
                    username + "_bill.csv");
        } else if (choice == 2) {
            reportService.exportToTXT(data, username + "_bill.txt");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // ---- Helpers ----

    private static void logout() {
        session.logout();
        data.clear();
    }

    private static int readInt() {
        try {
            String line = scanner.nextLine();
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double readDouble() {
        try {
            String line = scanner.nextLine();
            return Double.parseDouble(line.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
