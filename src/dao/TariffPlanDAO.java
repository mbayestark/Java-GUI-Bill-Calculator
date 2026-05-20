package dao;

import db.DBConnection;
import model.CommercialPlan;
import model.ResidentialPlan;
import model.TariffPlan;
import model.TariffTier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TariffPlanDAO implements DAO<TariffPlan> {

    private Connection connection;
    private TariffTierDAO tierDAO;

    public TariffPlanDAO() {
        this.connection = DBConnection.getInstance().getConnection();
        this.tierDAO = new TariffTierDAO();
    }

    @Override
    public TariffPlan get(int id) throws SQLException {
        String sql = "SELECT * FROM tariff_plans WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            TariffPlan plan = mapRow(rs);
            loadTiers(plan);
            return plan;
        }
        return null;
    }

    @Override
    public List<TariffPlan> getAll() throws SQLException {
        String sql = "SELECT * FROM tariff_plans";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<TariffPlan> plans = new ArrayList<>();
        while (rs.next()) {
            TariffPlan plan = mapRow(rs);
            loadTiers(plan);
            plans.add(plan);
        }
        return plans;
    }

    private void loadTiers(TariffPlan plan) throws SQLException {
        List<TariffTier> tiers = tierDAO.getByPlanId(plan.getId());
        for (int i = 0; i < tiers.size(); i++) {
            plan.addTier(tiers.get(i));
        }
    }

    @Override
    public int save(TariffPlan plan) throws SQLException {
        if (plan.getId() > 0) {
            return update(plan);
        }
        return insert(plan);
    }

    @Override
    public int insert(TariffPlan plan) throws SQLException {
        String sql = "INSERT INTO tariff_plans (name, plan_type, flat_rate, "
                + "is_tiered, peak_multiplier) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, plan.getName());
        stmt.setString(2, plan.getPlanType());
        stmt.setDouble(3, plan.getFlatRate());
        stmt.setBoolean(4, plan.isTiered());
        stmt.setDouble(5, plan.getPeakMultiplier());
        int rows = stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            plan.setId(keys.getInt(1));
        }
        return rows;
    }

    @Override
    public int update(TariffPlan plan) throws SQLException {
        String sql = "UPDATE tariff_plans SET name = ?, plan_type = ?, flat_rate = ?, "
                + "is_tiered = ?, peak_multiplier = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, plan.getName());
        stmt.setString(2, plan.getPlanType());
        stmt.setDouble(3, plan.getFlatRate());
        stmt.setBoolean(4, plan.isTiered());
        stmt.setDouble(5, plan.getPeakMultiplier());
        stmt.setInt(6, plan.getId());
        return stmt.executeUpdate();
    }

    @Override
    public int delete(TariffPlan plan) throws SQLException {
        String sql = "DELETE FROM tariff_plans WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, plan.getId());
        return stmt.executeUpdate();
    }

    private TariffPlan mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String planType = rs.getString("plan_type");
        double flatRate = rs.getDouble("flat_rate");
        boolean isTiered = rs.getBoolean("is_tiered");
        double peakMultiplier = rs.getDouble("peak_multiplier");

        TariffPlan plan;
        if ("commercial".equals(planType)) {
            plan = new CommercialPlan(id, name, flatRate, isTiered);
        } else {
            plan = new ResidentialPlan(id, name, flatRate, isTiered);
        }
        plan.setPeakMultiplier(peakMultiplier);
        return plan;
    }
}
