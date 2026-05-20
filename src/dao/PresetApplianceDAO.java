package dao;

import db.DBConnection;
import model.PresetAppliance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PresetApplianceDAO implements DAO<PresetAppliance> {

    private Connection connection;

    public PresetApplianceDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public PresetAppliance get(int id) throws SQLException {
        String sql = "SELECT * FROM preset_appliances WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    @Override
    public List<PresetAppliance> getAll() throws SQLException {
        String sql = "SELECT * FROM preset_appliances ORDER BY category, name";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<PresetAppliance> presets = new ArrayList<>();
        while (rs.next()) {
            presets.add(mapRow(rs));
        }
        return presets;
    }

    @Override
    public int save(PresetAppliance preset) throws SQLException {
        if (preset.getId() > 0) {
            return update(preset);
        }
        return insert(preset);
    }

    @Override
    public int insert(PresetAppliance preset) throws SQLException {
        String sql = "INSERT INTO preset_appliances (name, category, default_watts) "
                + "VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, preset.getName());
        stmt.setString(2, preset.getCategory());
        stmt.setDouble(3, preset.getDefaultWatts());
        int rows = stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            preset.setId(keys.getInt(1));
        }
        return rows;
    }

    @Override
    public int update(PresetAppliance preset) throws SQLException {
        String sql = "UPDATE preset_appliances SET name = ?, category = ?, "
                + "default_watts = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, preset.getName());
        stmt.setString(2, preset.getCategory());
        stmt.setDouble(3, preset.getDefaultWatts());
        stmt.setInt(4, preset.getId());
        return stmt.executeUpdate();
    }

    @Override
    public int delete(PresetAppliance preset) throws SQLException {
        String sql = "DELETE FROM preset_appliances WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, preset.getId());
        return stmt.executeUpdate();
    }

    private PresetAppliance mapRow(ResultSet rs) throws SQLException {
        return new PresetAppliance(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("default_watts")
        );
    }
}
