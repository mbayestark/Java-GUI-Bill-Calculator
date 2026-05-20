package service;

import dao.ApplianceDAO;
import dao.PresetApplianceDAO;
import model.Appliance;
import model.PresetAppliance;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class BaseApplianceService {

    private PresetApplianceDAO presetDAO;
    private ApplianceDAO applianceDAO;

    public BaseApplianceService() {
        this.presetDAO = new PresetApplianceDAO();
        this.applianceDAO = new ApplianceDAO();
    }

    public void seedAppliances(User user) {
        try {
            List<PresetAppliance> presets = presetDAO.getAll();
            for (int i = 0; i < presets.size(); i++) {
                PresetAppliance preset = presets.get(i);
                if (matchesClientType(preset, user.getClientType())) {
                    Appliance app = createFromPreset(preset, user);
                    applianceDAO.insert(app);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error seeding appliances: " + e.getMessage());
        }
    }

    private boolean matchesClientType(PresetAppliance preset,
                                       String clientType) {
        String category = preset.getCategory();
        if (category == null) {
            return false;
        }
        if ("individual".equals(clientType)) {
            return isIndividualCategory(category);
        }
        if ("school".equals(clientType)) {
            return "Classroom".equals(category);
        }
        if ("hospital".equals(clientType)) {
            return "Medical".equals(category);
        }
        return false;
    }

    private boolean isIndividualCategory(String category) {
        if ("Home".equals(category)) { return true; }
        if ("Kitchen".equals(category)) { return true; }
        if ("Lighting".equals(category)) { return true; }
        if ("Entertainment".equals(category)) { return true; }
        if ("Bathroom".equals(category)) { return true; }
        return false;
    }

    private Appliance createFromPreset(PresetAppliance preset, User user) {
        double hours = getDefaultHours(user.getClientType());
        double days = getDefaultDays(user.getClientType());
        int quantity = user.getCapacity();
        Appliance app = preset.toAppliance(hours, days, quantity, user.getId());
        return app;
    }

    private double getDefaultHours(String clientType) {
        if ("hospital".equals(clientType)) { return 12; }
        if ("school".equals(clientType)) { return 6; }
        return 3;
    }

    private double getDefaultDays(String clientType) {
        if ("hospital".equals(clientType)) { return 30; }
        if ("school".equals(clientType)) { return 22; }
        return 30;
    }
}
