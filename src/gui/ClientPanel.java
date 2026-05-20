package gui;

import dao.ApplianceDAO;
import dao.BillHistoryDAO;
import dao.PresetApplianceDAO;
import dao.TariffPlanDAO;
import dao.UsageLogDAO;
import dao.UserDAO;
import model.AppData;
import model.Appliance;
import model.BillRecord;
import model.CategorySummary;
import model.ClientTypePolicy;
import model.CommercialPlan;
import model.PresetAppliance;
import model.ResidentialPlan;
import model.Session;
import model.TariffPlan;
import model.TariffTier;
import model.UsageLog;
import model.User;
import service.BillPredictor;
import service.ReportService;
import service.UsageSimulator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClientPanel extends JPanel implements ActionListener {

    private MainFrame frame;
    private ApplianceDAO applianceDAO;
    private BillHistoryDAO billHistoryDAO;
    private PresetApplianceDAO presetDAO;
    private TariffPlanDAO planDAO;
    private UsageLogDAO usageLogDAO;
    private UserDAO userDAO;

    private CardLayout contentLayout;
    private JPanel contentPanel;

    private DefaultTableModel appliancesModel;
    private DefaultTableModel categoryModel;
    private JScrollPane appliancesScroll;
    private JTextArea billArea;
    private JLabel billWarning;
    private JTextArea predictionArea;
    private JLabel predictionWarning;
    private JTextField budgetField;

    private DefaultTableModel simulationModel;
    private JLabel simulationError;

    private JLabel clientTypeLabel;
    private JLabel appliancesError;
    private JLabel settingsError;
    private JButton activeButton;
    private JButton btnAppliances;
    private JButton btnBill;
    private JButton btnCategories;
    private JButton btnPrediction;
    private JButton btnSimulation;
    private JButton btnSettings;

    public ClientPanel(MainFrame frame) {
        this.frame = frame;
        initDAOs();
        setLayout(new BorderLayout());
        setBackground(Style.BG);
        add(createSidebar(), BorderLayout.WEST);
        createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        setActiveButton(btnAppliances);
    }

    private void initDAOs() {
        this.applianceDAO = new ApplianceDAO();
        this.billHistoryDAO = new BillHistoryDAO();
        this.presetDAO = new PresetApplianceDAO();
        this.planDAO = new TariffPlanDAO();
        this.usageLogDAO = new UsageLogDAO();
        this.userDAO = new UserDAO();
    }

    private void createContentPanel() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Style.BG);
        contentPanel.add(createAppliancesView(), "appliances");
        contentPanel.add(createBillView(), "bill");
        contentPanel.add(createCategoryView(), "categories");
        contentPanel.add(createPredictionView(), "prediction");
        contentPanel.add(createSimulationView(), "simulation");
        contentPanel.add(createSettingsView(), "settings");
    }

    // ---- Sidebar ----

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Style.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1, Style.BORDER));

        JLabel title = Style.createHeading("Dashboard");
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));
        sidebar.add(title);

        clientTypeLabel = new JLabel("");
        clientTypeLabel.setFont(Style.SMALL);
        clientTypeLabel.setForeground(Style.TEXT_LIGHT);
        clientTypeLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        sidebar.add(clientTypeLabel);

        btnAppliances = addNavButton(sidebar, "Appliances", "appliances");
        btnBill = addNavButton(sidebar, "Bill", "bill");
        btnCategories = addNavButton(sidebar, "Categories", "categories");
        btnPrediction = addNavButton(sidebar, "Prediction", "prediction");
        btnSimulation = addNavButton(sidebar, "IoT Simulation", "simulation");
        btnSettings = addNavButton(sidebar, "Settings", "settings");
        sidebar.add(Box.createVerticalGlue());
        addNavButton(sidebar, "Logout", "logout");
        return sidebar;
    }

    private JButton addNavButton(JPanel sidebar, String text, String cmd) {
        JButton button = Style.createSidebarButton(text);
        button.setActionCommand(cmd);
        button.addActionListener(this);
        sidebar.add(button);
        return button;
    }

    private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(Style.SIDEBAR);
            activeButton.setForeground(Style.TEXT);
            activeButton.setBorder(BorderFactory.createEmptyBorder(
                    12, 20, 12, 20));
        }
        activeButton = button;
        activeButton.setBackground(Style.BG);
        activeButton.setForeground(Style.ACCENT);
        activeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, Style.ACCENT),
                BorderFactory.createEmptyBorder(12, 17, 12, 20)));
    }

    private void navigateTo(JButton button, String card) {
        setActiveButton(button);
        clearAllErrors();
        contentLayout.show(contentPanel, card);
    }

    private void clearAllErrors() {
        Style.clearError(appliancesError);
        Style.clearError(simulationError);
        Style.clearError(settingsError);
        simulationError.setForeground(Style.DANGER);
        settingsError.setForeground(Style.DANGER);
    }

    // ---- Content Views ----

    private JPanel createAppliancesView() {
        JPanel view = Style.createViewPanel("My Appliances");
        appliancesModel = Style.createTableModel(new String[]{
                "ID", "Name", "Category", "Watts",
                "Hrs/Day", "Peak Hrs", "Days/Mon", "Qty", "kWh/Mon"});
        appliancesScroll = Style.createScrollTable(appliancesModel);
        view.add(appliancesScroll, BorderLayout.CENTER);

        appliancesError = Style.createErrorLabel();
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBackground(Style.BG);
        appliancesError.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        south.add(appliancesError);
        south.add(Style.createButtonRow(
                new String[]{"Add", "Preset", "Edit", "Delete"},
                new String[]{"add", "preset", "edit", "delete"}, this));
        view.add(south, BorderLayout.SOUTH);
        return view;
    }

    private JPanel createBillView() {
        JPanel view = Style.createViewPanel("Bill Breakdown");
        billArea = new JTextArea();
        billArea.setFont(Style.MONO);
        billArea.setEditable(false);
        billArea.setBackground(Style.BG);
        billArea.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JScrollPane scroll = new JScrollPane(billArea);
        scroll.setBorder(BorderFactory.createLineBorder(Style.BORDER));
        view.add(scroll, BorderLayout.CENTER);

        billWarning = new JLabel(" ");
        billWarning.setFont(Style.BODY);
        billWarning.setForeground(Style.DANGER);
        billWarning.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        view.add(billWarning, BorderLayout.SOUTH);
        return view;
    }

    private JPanel createCategoryView() {
        JPanel view = Style.createViewPanel("Category Summary");
        categoryModel = Style.createTableModel(
                new String[]{"Category", "kWh", "Cost (CFA)", "%"});
        JScrollPane scroll = Style.createScrollTable(categoryModel);
        view.add(scroll, BorderLayout.CENTER);
        return view;
    }

    private JPanel createPredictionView() {
        JPanel view = Style.createViewPanel("Bill Prediction");
        predictionArea = new JTextArea();
        predictionArea.setFont(Style.MONO);
        predictionArea.setEditable(false);
        predictionArea.setBackground(Style.BG);
        predictionArea.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JScrollPane scroll = new JScrollPane(predictionArea);
        scroll.setBorder(BorderFactory.createLineBorder(Style.BORDER));
        view.add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBackground(Style.BG);

        predictionWarning = new JLabel(" ");
        predictionWarning.setFont(Style.BODY);
        predictionWarning.setForeground(Style.DANGER);
        predictionWarning.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        south.add(predictionWarning);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(Style.BG);
        btnRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JButton saveBtn = Style.createButton("Save as Bill Record");
        saveBtn.setActionCommand("save_bill_record");
        saveBtn.addActionListener(this);
        btnRow.add(saveBtn);
        south.add(btnRow);

        view.add(south, BorderLayout.SOUTH);
        return view;
    }

    private JPanel createSimulationView() {
        JPanel view = Style.createViewPanel("IoT Usage Simulation");
        simulationModel = Style.createTableModel(new String[]{
                "Appliance", "Date", "Hours Used"});
        JScrollPane scroll = Style.createScrollTable(simulationModel);
        view.add(scroll, BorderLayout.CENTER);

        simulationError = Style.createErrorLabel();
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBackground(Style.BG);
        simulationError.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        south.add(simulationError);
        south.add(Style.createButtonRow(
                new String[]{"Simulate Month", "Apply Averages"},
                new String[]{"simulate_month", "apply_averages"}, this));
        view.add(south, BorderLayout.SOUTH);
        return view;
    }

    private JPanel createSettingsView() {
        JPanel view = Style.createViewPanel("Settings");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Style.BG);
        content.add(createBudgetSection());
        content.add(Box.createVerticalStrut(30));
        content.add(createExportSection());
        view.add(content, BorderLayout.CENTER);
        return view;
    }

    private JPanel createBudgetSection() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Style.BG);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setBackground(Style.BG);
        row.add(Style.createLabel("Monthly Budget (CFA):"));
        budgetField = Style.createTextField();
        row.add(budgetField);
        JButton saveBtn = Style.createButton("Save");
        saveBtn.setActionCommand("save_budget");
        saveBtn.addActionListener(this);
        row.add(saveBtn);

        settingsError = Style.createErrorLabel();
        settingsError.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));

        wrapper.add(row);
        wrapper.add(settingsError);
        return wrapper;
    }

    private JPanel createExportSection() {
        JPanel section = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        section.setBackground(Style.BG);
        section.add(Style.createLabel("Export Report:"));

        JButton csvBtn = Style.createOutlineButton("CSV");
        csvBtn.setActionCommand("export_csv");
        csvBtn.addActionListener(this);
        section.add(csvBtn);

        JButton txtBtn = Style.createOutlineButton("TXT");
        txtBtn.setActionCommand("export_txt");
        txtBtn.addActionListener(this);
        section.add(txtBtn);
        return section;
    }

    // ---- Refresh ----

    public void refresh() {
        loadUserData();
        refreshClientTypeLabel();
        refreshAppliances();
        refreshBill();
        refreshCategories();
        refreshPrediction();
        refreshSimulation();
        refreshBudget();
    }

    private void refreshClientTypeLabel() {
        User user = Session.getInstance().getCurrentUser();
        String type = user.getClientType();
        if (type != null) {
            String label = type.substring(0, 1).toUpperCase() + type.substring(1);
            clientTypeLabel.setText(label + " account");
        } else {
            clientTypeLabel.setText("");
        }
    }

    private void loadUserData() {
        AppData data = AppData.getInstance();
        data.clear();
        User user = Session.getInstance().getCurrentUser();
        data.setMonthlyBudget(user.getMonthlyBudget());
        data.setClientType(user.getClientType());
        try {
            loadTariffPlan(user, data);
            loadAppliances(user, data);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void loadTariffPlan(User user, AppData data) throws SQLException {
        if (user.getTariffPlanId() > 0) {
            TariffPlan plan = planDAO.get(user.getTariffPlanId());
            data.setTariffPlan(plan);
        }
    }

    private void loadAppliances(User user, AppData data) throws SQLException {
        List<Appliance> appliances = applianceDAO.getByUserId(user.getId());
        for (int i = 0; i < appliances.size(); i++) {
            data.addAppliance(appliances.get(i));
        }
        applySimulationData(appliances);
    }

    private void applySimulationData(List<Appliance> appliances) {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        for (int i = 0; i < appliances.size(); i++) {
            Appliance app = appliances.get(i);
            try {
                List<UsageLog> logs = usageLogDAO.getByApplianceAndMonth(
                        app.getId(), month, year);
                if (logs.isEmpty()) {
                    continue;
                }
                updateFromLogs(app, logs);
            } catch (SQLException e) {
                System.out.println("Error loading simulation: " + e.getMessage());
            }
        }
    }

    private void updateFromLogs(Appliance app, List<UsageLog> logs) {
        double totalHours = 0;
        for (int i = 0; i < logs.size(); i++) {
            totalHours = totalHours + logs.get(i).getHoursUsed();
        }
        double avgHours = totalHours / logs.size();
        if (avgHours > 0) {
            app.setHoursPerDay(avgHours);
        }
        app.setDaysPerMonth(logs.size());
    }

    private void refreshAppliances() {
        appliancesModel.setRowCount(0);
        List<Appliance> appliances = AppData.getInstance().getAppliances();
        for (int i = 0; i < appliances.size(); i++) {
            Appliance app = appliances.get(i);
            appliancesModel.addRow(new Object[]{
                    app.getId(), app.getName(), app.getCategory(),
                    String.format("%.1f", app.getWatts()),
                    String.format("%.1f", app.getHoursPerDay()),
                    String.format("%.1f", app.getPeakHoursPerDay()),
                    String.format("%.1f", app.getDaysPerMonth()),
                    app.getQuantity(),
                    String.format("%.2f", app.getMonthlyKwh())});
        }
    }

    private void refreshBill() {
        AppData data = AppData.getInstance();
        TariffPlan plan = data.getTariffPlan();
        if (plan == null) {
            billArea.setText("No tariff plan assigned. Contact admin.");
            billWarning.setText(" ");
            return;
        }
        billArea.setText(buildBillText(plan, data));
        updateBillWarning(data);
    }

    private void updateBillWarning(AppData data) {
        if (data.getMonthlyBudget() <= 0) {
            billWarning.setText(" ");
            return;
        }
        if (data.isOverBudget()) {
            billWarning.setForeground(Style.DANGER);
            billWarning.setText("WARNING: Bill exceeds budget of "
                    + String.format("%.2f", data.getMonthlyBudget())
                    + " " + data.getCurrency() + "!");
        } else {
            billWarning.setForeground(Style.SUCCESS);
            billWarning.setText("Within budget ("
                    + String.format("%.2f", data.getMonthlyBudget())
                    + " " + data.getCurrency() + ")");
        }
    }

    private String buildBillText(TariffPlan plan, AppData data) {
        User user = Session.getInstance().getCurrentUser();
        String text = "Plan: " + plan.getName()
                + " (" + plan.getPlanType() + ")\n";
        text = text + "Client: " + ClientTypePolicy.getDescription(
                user.getClientType()) + "\n\n";

        if (plan instanceof ResidentialPlan && plan.isTiered()) {
            text = text + buildTierText(plan, data);
        } else if (plan instanceof CommercialPlan) {
            text = text + "Rate: " + String.format("%.2f", plan.getFlatRate())
                    + " CFA/kWh + 18% tax\n";
        } else {
            text = text + "Flat rate: " + String.format("%.2f", plan.getFlatRate())
                    + " CFA/kWh\n";
        }

        text = text + "\nTotal kWh:    " + String.format("%.2f", data.getTotalKwh());
        text = text + buildPeakText(data, plan);
        text = text + "\nRaw Cost:     " + String.format("%.2f", data.getRawCost())
                + " " + data.getCurrency();
        text = text + "\nSubsidy:      -" + String.format("%.2f", data.getDiscount())
                + " " + data.getCurrency()
                + "  (" + ClientTypePolicy.getDiscountLabel(user.getClientType()) + ")";
        text = text + "\nFinal Cost:   " + String.format("%.2f", data.getTotalCost())
                + " " + data.getCurrency() + "\n";
        return text;
    }

    private String buildTierText(TariffPlan plan, AppData data) {
        String text = "";
        double remaining = data.getTotalKwh();
        double previousLimit = 0;
        List<TariffTier> tiers = plan.getTiers();

        for (int i = 0; i < tiers.size(); i++) {
            if (remaining <= 0) {
                break;
            }
            TariffTier tier = tiers.get(i);
            double tierRange = tier.getUpToKwh() - previousLimit;
            double kwhInTier = Math.min(remaining, tierRange);
            double tierCost = kwhInTier * tier.getRatePerKwh();
            text = text + String.format("  Up to %.0f kWh: %.2f kWh x %.2f = %.2f CFA\n",
                    tier.getUpToKwh(), kwhInTier, tier.getRatePerKwh(), tierCost);
            remaining = remaining - kwhInTier;
            previousLimit = tier.getUpToKwh();
        }
        return text;
    }

    private String buildPeakText(AppData data, TariffPlan plan) {
        double peakKwh = data.getTotalPeakKwh();
        if (peakKwh <= 0 || plan.getPeakMultiplier() <= 1.0) {
            return "";
        }
        String text = "\n  Peak kWh:   " + String.format("%.2f", peakKwh);
        text = text + "  (x" + String.format("%.1f", plan.getPeakMultiplier()) + ")";
        text = text + "\n  Off-peak:   " + String.format("%.2f",
                data.getTotalKwh() - peakKwh);
        text = text + "\n  Surcharge:  +" + String.format("%.2f",
                data.getPeakSurcharge()) + " " + data.getCurrency();
        return text;
    }

    private void refreshCategories() {
        categoryModel.setRowCount(0);
        List<CategorySummary> summaries = AppData.getInstance().getCategorySummaries();
        for (int i = 0; i < summaries.size(); i++) {
            CategorySummary cs = summaries.get(i);
            categoryModel.addRow(new Object[]{
                    cs.getCategory(),
                    String.format("%.2f", cs.getTotalKwh()),
                    String.format("%.2f", cs.getTotalCost()),
                    String.format("%.1f%%", cs.getPercentage())});
        }
    }

    private void refreshBudget() {
        double budget = AppData.getInstance().getMonthlyBudget();
        budgetField.setText(String.format("%.2f", budget));
    }

    // ---- Action Dispatch ----

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("appliances".equals(cmd)) { navigateTo(btnAppliances, "appliances"); }
        else if ("bill".equals(cmd)) { navigateTo(btnBill, "bill"); }
        else if ("categories".equals(cmd)) { navigateTo(btnCategories, "categories"); }
        else if ("prediction".equals(cmd)) { navigateTo(btnPrediction, "prediction"); }
        else if ("simulation".equals(cmd)) { navigateTo(btnSimulation, "simulation"); }
        else if ("settings".equals(cmd)) { navigateTo(btnSettings, "settings"); }
        else if ("logout".equals(cmd)) { handleLogout(); }
        else if ("add".equals(cmd)) { showAddApplianceDialog(); }
        else if ("preset".equals(cmd)) { showAddFromPresetDialog(); }
        else if ("edit".equals(cmd)) { showEditApplianceDialog(); }
        else if ("delete".equals(cmd)) { deleteSelectedAppliance(); }
        else if ("save_budget".equals(cmd)) { saveBudget(); }
        else if ("export_csv".equals(cmd)) { exportCSV(); }
        else if ("export_txt".equals(cmd)) { exportTXT(); }
        else if ("save_bill_record".equals(cmd)) { saveBillRecord(); }
        else if ("simulate_month".equals(cmd)) { simulateMonth(); }
        else if ("apply_averages".equals(cmd)) { applySimulationAverages(); }
    }

    private void handleLogout() {
        Session.getInstance().logout();
        AppData.getInstance().clear();
        frame.showLogin();
    }

    // ---- Add Appliance ----

    private void showAddApplianceDialog() {
        String[] labels = {"Name:", "Category:", "Watts:",
                "Hours/day:", "Peak hrs/day:", "Days/month:", "Quantity:"};
        String[] defaults = {"", "", "", "", "0", "", "1"};
        JTextField[] fields = Style.showFormDialog(this,
                "Add Appliance", labels, defaults);
        if (fields != null) {
            saveNewAppliance(fields);
        }
    }

    private void saveNewAppliance(JTextField[] fields) {
        try {
            Appliance appliance = new Appliance(
                    fields[0].getText().trim(),
                    fields[1].getText().trim(),
                    Double.parseDouble(fields[2].getText().trim()),
                    Double.parseDouble(fields[3].getText().trim()),
                    Double.parseDouble(fields[5].getText().trim()),
                    Integer.parseInt(fields[6].getText().trim()));
            appliance.setPeakHoursPerDay(
                    Double.parseDouble(fields[4].getText().trim()));
            User currentUser = Session.getInstance().getCurrentUser();
            appliance.setUserId(currentUser.getId());
            applianceDAO.insert(appliance);
            AppData.getInstance().addAppliance(appliance);
            refreshAfterChange();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ---- Add From Preset ----

    private void showAddFromPresetDialog() {
        try {
            List<PresetAppliance> presets = presetDAO.getAll();
            if (presets.isEmpty()) {
                Style.showInlineError(appliancesError, "No presets available.");
                return;
            }
            PresetAppliance selected = pickPreset(presets);
            if (selected != null) {
                promptPresetDetails(selected);
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private PresetAppliance pickPreset(List<PresetAppliance> presets) {
        String[] names = new String[presets.size()];
        for (int i = 0; i < presets.size(); i++) {
            PresetAppliance preset = presets.get(i);
            names[i] = preset.getName() + " (" + preset.getDefaultWatts() + "W)";
        }
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a preset:", "Add from Preset",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
        if (selected == null) {
            return null;
        }
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(selected)) {
                return presets.get(i);
            }
        }
        return null;
    }

    private void promptPresetDetails(PresetAppliance preset) {
        String[] labels = {"Hours/day:", "Days/month:", "Quantity:"};
        JTextField[] fields = Style.showFormDialog(this,
                "Add " + preset.getName(), labels);
        if (fields != null) {
            saveFromPreset(preset, fields);
        }
    }

    private void saveFromPreset(PresetAppliance preset, JTextField[] fields) {
        try {
            double hours = Double.parseDouble(fields[0].getText().trim());
            double days = Double.parseDouble(fields[1].getText().trim());
            int qty = Integer.parseInt(fields[2].getText().trim());
            User currentUser = Session.getInstance().getCurrentUser();
            int userId = currentUser.getId();
            Appliance appliance = preset.toAppliance(hours, days, qty, userId);
            applianceDAO.insert(appliance);
            AppData.getInstance().addAppliance(appliance);
            refreshAfterChange();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ---- Edit Appliance ----

    private void showEditApplianceDialog() {
        JTable table = Style.getTableFromScroll(appliancesScroll);
        int row = table.getSelectedRow();
        if (row < 0) {
            Style.showInlineError(appliancesError, "Select an appliance first.");
            return;
        }
        Style.clearError(appliancesError);
        int appId = (int) appliancesModel.getValueAt(row, 0);
        Appliance appliance = findApplianceById(appId);
        if (appliance == null) {
            showError("Appliance not found.");
            return;
        }
        showEditForm(appliance);
    }

    private Appliance findApplianceById(int appId) {
        List<Appliance> appliances = AppData.getInstance().getAppliances();
        for (int i = 0; i < appliances.size(); i++) {
            if (appliances.get(i).getId() == appId) {
                return appliances.get(i);
            }
        }
        return null;
    }

    private void showEditForm(Appliance appliance) {
        String[] labels = {"Name:", "Category:", "Watts:",
                "Hours/day:", "Peak hrs/day:", "Days/month:", "Quantity:"};
        String[] defaults = {
                appliance.getName(), appliance.getCategory(),
                String.valueOf(appliance.getWatts()),
                String.valueOf(appliance.getHoursPerDay()),
                String.valueOf(appliance.getPeakHoursPerDay()),
                String.valueOf(appliance.getDaysPerMonth()),
                String.valueOf(appliance.getQuantity())};
        JTextField[] fields = Style.showFormDialog(this,
                "Edit Appliance", labels, defaults);
        if (fields != null) {
            saveEditedAppliance(appliance, fields);
        }
    }

    private void saveEditedAppliance(Appliance appliance, JTextField[] fields) {
        try {
            appliance.setName(fields[0].getText().trim());
            appliance.setCategory(fields[1].getText().trim());
            appliance.setWatts(Double.parseDouble(fields[2].getText().trim()));
            appliance.setHoursPerDay(Double.parseDouble(fields[3].getText().trim()));
            appliance.setPeakHoursPerDay(Double.parseDouble(fields[4].getText().trim()));
            appliance.setDaysPerMonth(Double.parseDouble(fields[5].getText().trim()));
            appliance.setQuantity(Integer.parseInt(fields[6].getText().trim()));
            applianceDAO.update(appliance);
            refreshAfterChange();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ---- Delete Appliance ----

    private void deleteSelectedAppliance() {
        JTable table = Style.getTableFromScroll(appliancesScroll);
        int row = table.getSelectedRow();
        if (row < 0) {
            Style.showInlineError(appliancesError, "Select an appliance first.");
            return;
        }
        Style.clearError(appliancesError);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this appliance?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deleteAppliance((int) appliancesModel.getValueAt(row, 0));
        }
    }

    private void deleteAppliance(int appId) {
        try {
            Appliance appliance = applianceDAO.get(appId);
            if (appliance != null) {
                applianceDAO.delete(appliance);
            }
            removeFromData(appId);
            refreshAfterChange();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void removeFromData(int appId) {
        List<Appliance> appliances = AppData.getInstance().getAppliances();
        for (int i = 0; i < appliances.size(); i++) {
            if (appliances.get(i).getId() == appId) {
                appliances.remove(i);
                break;
            }
        }
    }

    // ---- Budget ----

    private void saveBudget() {
        settingsError.setForeground(Style.DANGER);
        String input = budgetField.getText().trim();
        if (input.isEmpty()) {
            Style.showInlineError(settingsError, "Budget cannot be empty.");
            return;
        }
        try {
            double budget = Double.parseDouble(input);
            if (budget < 0) {
                Style.showInlineError(settingsError, "Budget cannot be negative.");
                return;
            }
            Style.clearError(settingsError);
            AppData.getInstance().setMonthlyBudget(budget);
            User user = Session.getInstance().getCurrentUser();
            user.setMonthlyBudget(budget);
            userDAO.update(user);
            refreshBill();
            refreshPrediction();
            settingsError.setForeground(Style.SUCCESS);
            settingsError.setText("Budget saved.");
        } catch (NumberFormatException e) {
            Style.showInlineError(settingsError, "Enter a valid number.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ---- Export ----

    private void exportCSV() {
        ReportService reportService = new ReportService();
        User currentUser = Session.getInstance().getCurrentUser();
        String filename = currentUser.getUsername() + "_bill.csv";
        reportService.exportToCSV(AppData.getInstance().getAppliances(), filename);
        JOptionPane.showMessageDialog(this, "Exported to " + filename);
    }

    private void exportTXT() {
        ReportService reportService = new ReportService();
        User currentUser = Session.getInstance().getCurrentUser();
        String filename = currentUser.getUsername() + "_bill.txt";
        reportService.exportToTXT(AppData.getInstance(), filename);
        JOptionPane.showMessageDialog(this, "Exported to " + filename);
    }

    // ---- Prediction ----

    private void refreshPrediction() {
        AppData data = AppData.getInstance();
        TariffPlan plan = data.getTariffPlan();
        User user = Session.getInstance().getCurrentUser();

        if (plan == null) {
            predictionArea.setText("No tariff plan assigned. Contact admin.");
            predictionWarning.setText(" ");
            return;
        }

        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        double consumedKwh = data.getTotalKwh();
        predictionArea.setText(buildPredictionText(
                user, plan, consumedKwh, dayOfMonth,
                daysInMonth, currentMonth, currentYear));
        updatePredictionWarning(user, plan, consumedKwh,
                dayOfMonth, daysInMonth);
    }

    private void updatePredictionWarning(User user, TariffPlan plan,
                                          double consumedKwh,
                                          int dayOfMonth, int daysInMonth) {
        double budget = user.getMonthlyBudget();
        if (budget <= 0) {
            predictionWarning.setText(" ");
            return;
        }
        BillPredictor predictor = new BillPredictor();
        double predictedKwh = predictor.predictMonthlyKwh(
                consumedKwh, dayOfMonth, daysInMonth, user.getClientType());
        double rawCost = predictor.predictMonthlyCost(predictedKwh, plan);
        double predictedCost = ClientTypePolicy.applyDiscount(
                rawCost, user.getClientType());

        if (predictor.willExceedBudget(predictedCost, budget)) {
            double surplus = predictor.predictedSurplus(predictedCost, budget);
            predictionWarning.setForeground(Style.DANGER);
            predictionWarning.setText("OVER BUDGET — excess: "
                    + String.format("%.0f", surplus) + " CFA");
        } else {
            predictionWarning.setForeground(Style.SUCCESS);
            predictionWarning.setText("Within budget ("
                    + String.format("%.0f", budget) + " CFA)");
        }
    }

    private String buildPredictionText(User user, TariffPlan plan,
                                       double consumedKwh, int dayOfMonth,
                                       int daysInMonth, int month, int year) {
        BillPredictor predictor = new BillPredictor();
        String text = buildPredictionHeader(month, year, dayOfMonth, daysInMonth);
        text = text + buildCurrentStats(consumedKwh, dayOfMonth, user);

        try {
            List<BillRecord> history = billHistoryDAO.getLastN(user.getId(), 3);
            double predictedKwh;

            if (history.isEmpty()) {
                predictedKwh = predictor.predictMonthlyKwh(
                        consumedKwh, dayOfMonth, daysInMonth,
                        user.getClientType());
                text = text + "Prediction method :  Linear extrapolation\n";
            } else {
                predictedKwh = predictor.predictWithHistory(
                        consumedKwh, dayOfMonth, daysInMonth, history);
                double histAvg = getHistoricalAvg(history);
                text = text + String.format("Historical average :  %.2f kWh  (last %d months)\n",
                        histAvg, history.size());
                text = text + "Prediction method :  Blended (70/30)\n";
            }

            text = text + buildPredictionResult(predictor, predictedKwh, plan, user);
        } catch (SQLException e) {
            text = text + "Error loading history: " + e.getMessage() + "\n";
        }
        return text;
    }

    private String buildPredictionHeader(int month, int year,
                                         int dayOfMonth, int daysInMonth) {
        String[] monthNames = {"", "January", "February", "March", "April",
                "May", "June", "July", "August", "September",
                "October", "November", "December"};
        return "=== Bill Prediction — " + monthNames[month] + " " + year
                + " (Day " + dayOfMonth + " of " + daysInMonth + ") ===\n\n";
    }

    private String buildCurrentStats(double consumedKwh, int dayOfMonth,
                                     User user) {
        double dailyAvg = 0;
        if (dayOfMonth > 0) {
            dailyAvg = consumedKwh / dayOfMonth;
        }
        String text = String.format("Consumed so far   :  %.2f kWh\n", consumedKwh);
        text = text + String.format("Daily average     :  %.2f kWh/day\n", dailyAvg);
        String type = user.getClientType();
        if (type == null) {
            type = "N/A";
        }
        text = text + "Client type       :  " + type + "\n";
        return text;
    }

    private String buildPredictionResult(BillPredictor predictor,
                                         double predictedKwh, TariffPlan plan,
                                         User user) {
        double rawCost = predictor.predictMonthlyCost(predictedKwh, plan);
        double predictedCost = ClientTypePolicy.applyDiscount(
                rawCost, user.getClientType());
        double budget = user.getMonthlyBudget();

        String text = String.format("\nPredicted total   :  %.2f kWh\n", predictedKwh);
        text = text + "Tariff plan       :  " + plan.getName()
                + " (" + plan.getPlanType() + ")\n";
        text = text + String.format("Raw cost          :  %.0f CFA\n", rawCost);
        text = text + "Subsidy           :  "
                + ClientTypePolicy.getDiscountLabel(user.getClientType()) + "\n";
        text = text + String.format("Predicted cost    :  %.0f CFA\n", predictedCost);

        if (budget > 0) {
            text = text + String.format("Monthly budget    :  %.0f CFA\n", budget);
        }
        return text;
    }

    private double getHistoricalAvg(List<BillRecord> records) {
        double sum = 0;
        for (int i = 0; i < records.size(); i++) {
            sum = sum + records.get(i).getTotalKwh();
        }
        return sum / records.size();
    }

    // ---- Save Bill Record ----

    private void saveBillRecord() {
        User user = Session.getInstance().getCurrentUser();
        AppData data = AppData.getInstance();
        TariffPlan plan = data.getTariffPlan();

        if (plan == null) {
            showError("No tariff plan assigned.");
            return;
        }

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        try {
            if (billHistoryDAO.existsForMonth(user.getId(), month, year)) {
                showError("A record for this month already exists.");
                return;
            }
            double totalKwh = data.getTotalKwh();
            double totalCost = data.getTotalCost();
            BillRecord record = new BillRecord(user.getId(), month, year,
                    totalKwh, totalCost);
            billHistoryDAO.insert(record);
            JOptionPane.showMessageDialog(this,
                    "Bill record saved for " + month + "/" + year + ".");
            refreshPrediction();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // ---- IoT Simulation ----

    private void refreshSimulation() {
        simulationModel.setRowCount(0);
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        List<Appliance> appliances = AppData.getInstance().getAppliances();

        try {
            for (int i = 0; i < appliances.size(); i++) {
                Appliance app = appliances.get(i);
                List<UsageLog> logs = usageLogDAO.getByApplianceAndMonth(
                        app.getId(), month, year);
                for (int j = 0; j < logs.size(); j++) {
                    UsageLog log = logs.get(j);
                    simulationModel.addRow(new Object[]{
                            app.getName(),
                            log.getLogDate().toString(),
                            String.format("%.1f", log.getHoursUsed())});
                }
            }
        } catch (SQLException e) {
            Style.showInlineError(simulationError, e.getMessage());
        }
    }

    private void simulateMonth() {
        User user = Session.getInstance().getCurrentUser();
        List<Appliance> appliances = AppData.getInstance().getAppliances();
        if (appliances.isEmpty()) {
            Style.showInlineError(simulationError, "No appliances to simulate.");
            return;
        }

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        try {
            deleteExistingLogs(appliances, month, year);
            UsageSimulator simulator = new UsageSimulator();
            List<UsageLog> logs = simulator.simulateMonth(
                    appliances, user.getClientType());
            usageLogDAO.insertBatch(logs);
            refreshSimulation();
            simulationError.setForeground(Style.SUCCESS);
            simulationError.setText("Simulated " + logs.size()
                    + " daily records for " + appliances.size() + " appliances.");
        } catch (SQLException e) {
            Style.showInlineError(simulationError, e.getMessage());
        }
    }

    private void deleteExistingLogs(List<Appliance> appliances,
                                     int month, int year)
            throws SQLException {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < appliances.size(); i++) {
            ids.add(appliances.get(i).getId());
        }
        usageLogDAO.deleteByUserMonth(ids, month, year);
    }

    private void applySimulationAverages() {
        List<Appliance> appliances = AppData.getInstance().getAppliances();
        if (appliances.isEmpty()) {
            Style.showInlineError(simulationError, "No appliances available.");
            return;
        }

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int updated = 0;

        try {
            for (int i = 0; i < appliances.size(); i++) {
                Appliance app = appliances.get(i);
                double avgHours = usageLogDAO.getAverageHours(
                        app.getId(), month, year);
                if (avgHours > 0) {
                    app.setHoursPerDay(avgHours);
                    applianceDAO.update(app);
                    updated = updated + 1;
                }
            }
            if (updated > 0) {
                refreshAfterChange();
                simulationError.setForeground(Style.SUCCESS);
                simulationError.setText("Updated hours/day for "
                        + updated + " appliances from simulation data.");
            } else {
                Style.showInlineError(simulationError,
                        "No simulation data found. Run Simulate Month first.");
            }
        } catch (Exception e) {
            Style.showInlineError(simulationError, e.getMessage());
        }
    }

    // ---- Helpers ----

    private void refreshAfterChange() {
        refreshAppliances();
        refreshBill();
        refreshCategories();
        refreshPrediction();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
