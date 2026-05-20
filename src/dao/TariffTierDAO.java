package dao;

import db.DBConnection;
import model.TariffTier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TariffTierDAO implements DAO<TariffTier> {

    private Connection connection;

    public TariffTierDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public TariffTier get(int id) throws SQLException {
        String sql = "SELECT * FROM tariff_tiers WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    @Override
    public List<TariffTier> getAll() throws SQLException {
        String sql = "SELECT * FROM tariff_tiers ORDER BY up_to_kwh";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<TariffTier> tiers = new ArrayList<>();
        while (rs.next()) {
            tiers.add(mapRow(rs));
        }
        return tiers;
    }

    public List<TariffTier> getByPlanId(int planId) throws SQLException {
        String sql = "SELECT * FROM tariff_tiers WHERE tariff_plan_id = ? ORDER BY up_to_kwh";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, planId);
        ResultSet rs = stmt.executeQuery();
        List<TariffTier> tiers = new ArrayList<>();
        while (rs.next()) {
            tiers.add(mapRow(rs));
        }
        return tiers;
    }

    @Override
    public int save(TariffTier tier) throws SQLException {
        if (tier.getId() > 0) {
            return update(tier);
        }
        return insert(tier);
    }

    @Override
    public int insert(TariffTier tier) throws SQLException {
        String sql = "INSERT INTO tariff_tiers (tariff_plan_id, up_to_kwh, rate_per_kwh) "
                + "VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, tier.getTariffPlanId());
        stmt.setDouble(2, tier.getUpToKwh());
        stmt.setDouble(3, tier.getRatePerKwh());
        int rows = stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            tier.setId(keys.getInt(1));
        }
        return rows;
    }

    @Override
    public int update(TariffTier tier) throws SQLException {
        String sql = "UPDATE tariff_tiers SET tariff_plan_id = ?, up_to_kwh = ?, "
                + "rate_per_kwh = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, tier.getTariffPlanId());
        stmt.setDouble(2, tier.getUpToKwh());
        stmt.setDouble(3, tier.getRatePerKwh());
        stmt.setInt(4, tier.getId());
        return stmt.executeUpdate();
    }

    @Override
    public int delete(TariffTier tier) throws SQLException {
        String sql = "DELETE FROM tariff_tiers WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, tier.getId());
        return stmt.executeUpdate();
    }

    private TariffTier mapRow(ResultSet rs) throws SQLException {
        return new TariffTier(
                rs.getInt("id"),
                rs.getInt("tariff_plan_id"),
                rs.getDouble("up_to_kwh"),
                rs.getDouble("rate_per_kwh")
        );
    }
}
