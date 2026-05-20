package gui;

import dao.ApplianceDAO;
import dao.PresetApplianceDAO;
import dao.TariffPlanDAO;
import dao.TariffTierDAO;
import dao.UserDAO;
import model.Appliance;
import model.ClientTypePolicy;
import model.CommercialPlan;
import model.PresetAppliance;
import model.ResidentialPlan;
import model.Session;
import model.TariffPlan;
import model.TariffTier;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Calendar;

public class AdminPanel extends JPanel implements ActionListener {

    private MainFrame frame;
    private UserDAO userDAO;
    private TariffPlanDAO planDAO;
    private TariffTierDAO tierDAO;
    private PresetApplianceDAO presetDAO;
    private ApplianceDAO applianceDAO;

    private CardLayout contentLayout;
    private JPanel contentPanel;

    private DefaultTableModel clientsModel;
    private DefaultTableModel plansModel;
    private DefaultTableModel presetsModel;
    private DefaultTableModel billsModel;

    private JScrollPane clientsScroll;
    private JScrollPane plansScroll;
    private JScrollPane presetsScroll;

    private JButton activeButton;
    private JButton btnClients;
    private JButton btnPlans;
    private JButton btnPresets;
    private JButton btnBills;

    private JLabel statClients;
    private JLabel statRevenue;
    private JLabel statAvgKwh;

    private JLabel clientsError;
    private JLabel plansError;
    private JLabel presetsError;

    public AdminPanel(MainFrame frame) {
        this.frame = frame;
        initDAOs();
        setLayout(new BorderLayout());
        setBackground(Style.BG);
        add(createSidebar(), BorderLayout.WEST);

        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setBackground(Style.BG);
        rightSide.add(createStatsBar(), BorderLayout.NORTH);
        createContentPanel();
        rightSide.add(contentPanel, BorderLayout.CENTER);
        add(rightSide, BorderLayout.CENTER);
        setActiveButton(btnClients);
    }

    private void initDAOs() {
        this.userDAO = new UserDAO();
        this.planDAO = new TariffPlanDAO();
        this.tierDAO = new TariffTierDAO();
        this.presetDAO = new PresetApplianceDAO();
        this.applianceDAO = new ApplianceDAO();
    }

    private void createContentPanel() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Style.BG);
        contentPanel.add(createClientsView(), "clients");
        contentPanel.add(createPlansView(), "plans");
        contentPanel.add(createPresetsView(), "presets");
        contentPanel.add(createBillsView(), "bills");
    }

    // ---- Stats Bar ----

    private JPanel createStatsBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        bar.setBackground(Style.SIDEBAR);
        bar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Style.BORDER));

        statClients = Style.createLabel("Clients: 0");
        statRevenue = Style.createLabel("Revenue: 0 CFA");
        statAvgKwh = Style.createLabel("Avg kWh: 0");

        bar.add(createStatCard("Total Clients", statClients));
        bar.add(createStatCard("Total Revenue", statRevenue));
        bar.add(createStatCard("Avg Consumption", statAvgKwh));
        return bar;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Style.SIDEBAR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Style.SMALL);
        titleLabel.setForeground(Style.TEXT_LIGHT);

        valueLabel.setFont(Style.HEADING);
        valueLabel.setForeground(Style.TEXT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        return card;
    }

    // ---- Sidebar ----

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Style.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1, Style.BORDER));

        JLabel title = Style.createHeading("Admin");
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.add(title);

        btnClients = addNavButton(sidebar, "Clients", "clients");
        btnPlans = addNavButton(sidebar, "Plans", "plans");
        btnPresets = addNavButton(sidebar, "Presets", "presets");
        btnBills = addNavButton(sidebar, "Bills", "bills");
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
        Style.clearError(clientsError);
        Style.clearError(plansError);
        Style.clearError(presetsError);
    }

    // ---- Content Views ----

    private JPanel createClientsView() {
        JPanel view = Style.createViewPanel("Clients");
        clientsModel = Style.createTableModel(
                new String[]{"ID", "Username", "Full Name", "Type", "Capacity", "Plan ID"});
        clientsScroll = Style.createScrollTable(clientsModel);
        view.add(clientsScroll, BorderLayout.CENTER);
        clientsError = Style.createErrorLabel();
        JPanel south = createSouthPanel(clientsError,
                new String[]{"Assign Plan", "Delete"},
                new String[]{"assign_plan", "delete_client"});
        view.add(south, BorderLayout.SOUTH);
        return view;
    }

    private JPanel createPlansView() {
        JPanel view = Style.createViewPanel("Tariff Plans");
        plansModel = Style.createTableModel(
                new String[]{"ID", "Name", "Type", "Flat Rate", "Tiered", "Peak x"});
        plansScroll = Style.createScrollTable(plansModel);
        view.add(plansScroll, BorderLayout.CENTER);
        plansError = Style.createErrorLabel();
        JPanel south = createSouthPanel(plansError,
                new String[]{"Add Plan", "Add Tier", "View Tiers", "Delete"},
                new String[]{"add_plan", "add_tier", "view_tiers", "delete_plan"});
        view.add(south, BorderLayout.SOUTH);
        return view;
    }

    private JPanel createPresetsView() {
        JPanel view = Style.createViewPanel("Preset Appliances");
        presetsModel = Style.createTableModel(
                new String[]{"ID", "Name", "Category", "Watts"});
        presetsScroll = Style.createScrollTable(presetsModel);
        view.add(presetsScroll, BorderLayout.CENTER);
        presetsError = Style.createErrorLabel();
        JPanel south = createSouthPanel(presetsError,
                new String[]{"Add Preset", "Delete"},
                new String[]{"add_preset", "delete_preset"});
        view.add(south, BorderLayout.SOUTH);
        return view;
    }

    private JPanel createSouthPanel(JLabel errorLabel, String[] labels,
                                    String[] commands) {
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBackground(Style.BG);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        south.add(errorLabel);
        south.add(Style.createButtonRow(labels, commands, this));
        return south;
    }

    private JPanel createBillsView() {
        JPanel view = Style.createViewPanel("All Client Bills");
        billsModel = Style.createTableModel(
                new String[]{"ID", "Username", "Total kWh", "Total Cost (CFA)"});
        JScrollPane scroll = Style.createScrollTable(billsModel);
        view.add(scroll, BorderLayout.CENTER);
        return view;
    }

    // ---- Refresh ----

    public void refresh() {
        refreshClients();
        refreshPlans();
        refreshPresets();
        refreshBills();
        refreshStats();
    }

    private void refreshClients() {
        clientsModel.setRowCount(0);
        try {
            List<User> clients = userDAO.getAllClients();
            for (int i = 0; i < clients.size(); i++) {
                User client = clients.get(i);
                clientsModel.addRow(new Object[]{
                        client.getId(), client.getUsername(),
                        client.getFullName(), client.getClientType(),
                        client.getCapacity(), client.getTariffPlanId()});
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void refreshPlans() {
        plansModel.setRowCount(0);
        try {
            List<TariffPlan> plans = planDAO.getAll();
            for (int i = 0; i < plans.size(); i++) {
                addPlanRow(plans.get(i));
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void addPlanRow(TariffPlan plan) {
        String tiered;
        if (plan.isTiered()) {
            tiered = "Yes";
        } else {
            tiered = "No";
        }
        plansModel.addRow(new Object[]{
                plan.getId(), plan.getName(),
                plan.getPlanType(), plan.getFlatRate(), tiered,
                String.format("%.1f", plan.getPeakMultiplier())});
    }

    private void refreshPresets() {
        presetsModel.setRowCount(0);
        try {
            List<PresetAppliance> presets = presetDAO.getAll();
            for (int i = 0; i < presets.size(); i++) {
                PresetAppliance preset = presets.get(i);
                presetsModel.addRow(new Object[]{
                        preset.getId(), preset.getName(),
                        preset.getCategory(), preset.getDefaultWatts()});
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void refreshBills() {
        billsModel.setRowCount(0);
        try {
            List<User> clients = userDAO.getAllClients();
            for (int i = 0; i < clients.size(); i++) {
                addBillRow(clients.get(i));
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void addBillRow(User client) throws SQLException {
        List<Appliance> appliances = applianceDAO.getByUserId(client.getId());
        double totalKwh = 0;
        for (int i = 0; i < appliances.size(); i++) {
            totalKwh = totalKwh + appliances.get(i).getMonthlyKwh();
        }
        double cost = calculateCost(client, totalKwh);
        billsModel.addRow(new Object[]{
                client.getId(), client.getUsername(),
                String.format("%.2f", totalKwh),
                String.format("%.2f", cost)});
    }

    private double calculateCost(User client, double kwh) throws SQLException {
        if (client.getTariffPlanId() <= 0) {
            return 0;
        }
        TariffPlan plan = planDAO.get(client.getTariffPlanId());
        if (plan == null) {
            return 0;
        }
        double rawCost = plan.calculateCost(kwh);
        return ClientTypePolicy.applyDiscount(rawCost, client.getClientType());
    }

    private void refreshStats() {
        try {
            List<User> clients = userDAO.getAllClients();
            int clientCount = clients.size();
            double totalRevenue = 0;
            double totalKwh = 0;

            for (int i = 0; i < clients.size(); i++) {
                User client = clients.get(i);
                List<Appliance> appliances = applianceDAO.getByUserId(client.getId());
                double clientKwh = 0;
                for (int j = 0; j < appliances.size(); j++) {
                    clientKwh = clientKwh + appliances.get(j).getMonthlyKwh();
                }
                totalKwh = totalKwh + clientKwh;
                totalRevenue = totalRevenue + calculateCost(client, clientKwh);
            }

            statClients.setText(String.valueOf(clientCount));
            statRevenue.setText(String.format("%.0f CFA", totalRevenue));
            if (clientCount > 0) {
                statAvgKwh.setText(String.format("%.1f kWh", totalKwh / clientCount));
            } else {
                statAvgKwh.setText("0 kWh");
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // ---- Action Dispatch ----

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("clients".equals(cmd)) { navigateTo(btnClients, "clients"); }
        else if ("plans".equals(cmd)) { navigateTo(btnPlans, "plans"); }
        else if ("presets".equals(cmd)) { navigateTo(btnPresets, "presets"); }
        else if ("bills".equals(cmd)) { navigateTo(btnBills, "bills"); }
        else if ("logout".equals(cmd)) { handleLogout(); }
        else if ("assign_plan".equals(cmd)) { showAssignPlanDialog(); }
        else if ("delete_client".equals(cmd)) { deleteSelectedClient(); }
        else if ("add_plan".equals(cmd)) { showAddPlanDialog(); }
        else if ("add_tier".equals(cmd)) { showAddTierDialog(); }
        else if ("view_tiers".equals(cmd)) { showViewTiersDialog(); }
        else if ("delete_plan".equals(cmd)) { deleteSelectedPlan(); }
        else if ("add_preset".equals(cmd)) { showAddPresetDialog(); }
        else if ("delete_preset".equals(cmd)) { deleteSelectedPreset(); }
    }

    private void handleLogout() {
        Session.getInstance().logout();
        frame.showLogin();
    }

    // ---- Client Actions ----

    private void showAssignPlanDialog() {
        JTable table = Style.getTableFromScroll(clientsScroll);
        int row = table.getSelectedRow();
        if (row < 0) {
            Style.showInlineError(clientsError, "Select a client first.");
            return;
        }
        Style.clearError(clientsError);
        int clientId = (int) clientsModel.getValueAt(row, 0);
        try {
            List<TariffPlan> plans = planDAO.getAll();
            if (plans.isEmpty()) {
                Style.showInlineError(clientsError, "No plans available. Create a plan first.");
                return;
            }
            String[] planNames = new String[plans.size()];
            for (int i = 0; i < plans.size(); i++) {
                TariffPlan plan = plans.get(i);
                planNames[i] = plan.getId() + " — " + plan.getName()
                        + " (" + plan.getPlanType() + ")";
            }
            String selected = (String) JOptionPane.showInputDialog(this,
                    "Select a plan:", "Assign Plan",
                    JOptionPane.PLAIN_MESSAGE, null, planNames, planNames[0]);
            if (selected != null) {
                int selectedIndex = findSelectedIndex(planNames, selected);
                assignPlan(clientId, plans.get(selectedIndex).getId());
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private int findSelectedIndex(String[] options, String selected) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(selected)) {
                return i;
            }
        }
        return 0;
    }

    private void assignPlan(int clientId, int planId) {
        try {
            User client = userDAO.get(clientId);
            if (client == null) {
                showError("Client not found.");
                return;
            }
            client.setTariffPlanId(planId);
            userDAO.update(client);
            refreshClients();
            refreshBills();
            refreshStats();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void deleteSelectedClient() {
        JTable table = Style.getTableFromScroll(clientsScroll);
        int row = table.getSelectedRow();
        if (row < 0) {
            Style.showInlineError(clientsError, "Select a client first.");
            return;
        }
        Style.clearError(clientsError);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this client?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deleteClient((int) clientsModel.getValueAt(row, 0));
        }
    }

    private void deleteClient(int clientId) {
        try {
            User client = userDAO.get(clientId);
            if (client != null) {
                userDAO.delete(client);
                refreshClients();
                refreshBills();
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // ---- Plan Actions ----

    private void showAddPlanDialog() {
        String[] types = {"residential", "commercial"};
        String type = (String) JOptionPane.showInputDialog(this,
                "Select plan type:", "Plan Type",
                JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
        if (type == null) {
            return;
        }

        String[] tieredOptions = {"No", "Yes"};
        String tieredChoice = (String) JOptionPane.showInputDialog(this,
                "Is this plan tiered?", "Tiered",
                JOptionPane.PLAIN_MESSAGE, null, tieredOptions, tieredOptions[0]);
        if (tieredChoice == null) {
            return;
        }

        String[] labels = {"Plan Name:", "Flat Rate:", "Peak Multiplier:"};
        String[] defaults = {"", "", "1.0"};
        JTextField[] fields = Style.showFormDialog(this,
                "Add Tariff Plan", labels, defaults);
        if (fields == null) {
            return;
        }

        saveNewPlan(fields, type, tieredChoice);
    }

    private void saveNewPlan(JTextField[] fields, String type,
                             String tieredChoice) {
        try {
            String name = fields[0].getText().trim();
            double flatRate = Double.parseDouble(fields[1].getText().trim());
            double peakMult = Double.parseDouble(fields[2].getText().trim());
            boolean tiered = "Yes".equals(tieredChoice);

            TariffPlan plan;
            if ("commercial".equals(type)) {
                plan = new CommercialPlan(name, flatRate, tiered);
            } else {
                plan = new ResidentialPlan(name, flatRate, tiered);
            }
            plan.setPeakMultiplier(peakMult);
            planDAO.insert(plan);
            refreshPlans();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showAddTierDialog() {
        JTable table = Style.getTableFromScroll(plansScroll);
        int row = table.getSelectedRow();
        if (row < 0) {
            Style.showInlineError(plansError, "Select a plan first.");
            return;
        }
        Style.clearError(plansError);
        int planId = (int) plansModel.getValueAt(row, 0);
        String[] labels = {"Up to kWh:", "Rate per kWh:"};
        JTextField[] fields = Style.showFormDialog(this, "Add Tier", labels);
        if (fields != null) {
            saveNewTier(planId, fields);
        }
    }

    private void saveNewTier(int planId, JTextField[] fields) {
        try {
            double upToKwh = Double.parseDouble(fields[0].getText().trim());
            double rate = Double.parseDouble(fields[1].getText().trim());
            if (tierAlreadyExists(planId, upToKwh)) {
                Style.showInlineError(plansError,
                        "A tier with limit " + upToKwh + " kWh already exists.");
                return;
            }
            TariffTier tier = new TariffTier(upToKwh, rate);
            tier.setTariffPlanId(planId);
            tierDAO.insert(tier);
            JOptionPane.showMessageDialog(this, "Tier added.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private boolean tierAlreadyExists(int planId, double upToKwh)
            throws SQLException {
        List<TariffTier> existing = tierDAO.getByPlanId(planId);
        for (int i = 0; i < existing.size(); i++) {
            if (existing.get(i).getUpToKwh() == upToKwh) {
                return true;
            }
        }
        return false;
    }

    private void showViewTiersDialog() {
        JTable table = Style.getTableFromScroll(plansScroll);
        int row = table.getSelectedRow();
        if (row < 0) {
            Style.showInlineError(plansError, "Select a plan first.");
            return;
        }
        Style.clearError(plansError);
        int planId = (int) plansModel.getValueAt(row, 0);
        String planName = (String) plansModel.getValueAt(row, 1);
        showTiersForPlan(planId, planName);
    }

    private void showTiersForPlan(int planId, String planName) {
        try {
            List<TariffTier> tiers = tierDAO.getByPlanId(planId);
            if (tiers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No tiers defined for this plan.");
                return;
            }
            String text = buildTiersText(tiers);
            JTextArea area = new JTextArea(text);
            area.setFont(Style.MONO);
            area.setEditable(false);
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(350, 200));
            JOptionPane.showMessageDialog(this, scroll,
                    "Tiers — " + planName, JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private String buildTiersText(List<TariffTier> tiers) {
        String text = String.format("%-6s %-15s %-15s%n",
                "#", "Up to (kWh)", "Rate (CFA/kWh)");
        text = text + "--------------------------------------\n";
        for (int i = 0; i < tiers.size(); i++) {
            TariffTier tier = tiers.get(i);
            text = text + String.format("%-6d %-15.0f %-15.2f%n",
                    (i + 1), tier.getUpToKwh(), tier.getRatePerKwh());
        }
        return text;
    }

    private void deleteSelectedPlan() {
        JTable table = Style.getTableFromScroll(plansScroll);
        int row = table.getSelectedRow();
        if (row < 0) {
            Style.showInlineError(plansError, "Select a plan first.");
            return;
        }
        Style.clearError(plansError);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this plan?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deletePlan((int) plansModel.getValueAt(row, 0));
        }
    }

    private void deletePlan(int planId) {
        try {
            TariffPlan plan = planDAO.get(planId);
            if (plan != null) {
                planDAO.delete(plan);
                refreshPlans();
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // ---- Preset Actions ----

    private void showAddPresetDialog() {
        String[] labels = {"Name:", "Category:", "Default Watts:"};
        JTextField[] fields = Style.showFormDialog(this,
                "Add Preset", labels);
        if (fields != null) {
            saveNewPreset(fields);
        }
    }

    private void saveNewPreset(JTextField[] fields) {
        try {
            String name = fields[0].getText().trim();
            String category = fields[1].getText().trim();
            double watts = Double.parseDouble(fields[2].getText().trim());
            PresetAppliance preset = new PresetAppliance(name, category, watts);
            presetDAO.insert(preset);
            refreshPresets();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void deleteSelectedPreset() {
        JTable table = Style.getTableFromScroll(presetsScroll);
        int row = table.getSelectedRow();
        if (row < 0) {
            Style.showInlineError(presetsError, "Select a preset first.");
            return;
        }
        Style.clearError(presetsError);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this preset?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deletePreset((int) presetsModel.getValueAt(row, 0));
        }
    }

    private void deletePreset(int presetId) {
        try {
            PresetAppliance preset = presetDAO.get(presetId);
            if (preset != null) {
                presetDAO.delete(preset);
                refreshPresets();
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // ---- Helper ----

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
