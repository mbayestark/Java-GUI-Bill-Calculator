package dao;

import db.DBConnection;
import model.Appliance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplianceDAO implements DAO<Appliance> {

    private Connection connection;

    public ApplianceDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public Appliance get(int id) throws SQLException {
        String sql = "SELECT * FROM appliances WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    @Override
    public List<Appliance> getAll() throws SQLException {
        String sql = "SELECT * FROM appliances";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Appliance> appliances = new ArrayList<>();
        while (rs.next()) {
            appliances.add(mapRow(rs));
        }
        return appliances;
    }

    @Override
    public int save(Appliance appliance) throws SQLException {
        if (appliance.getId() > 0) {
            return update(appliance);
        }
        return insert(appliance);
    }

    @Override
    public int insert(Appliance appliance) throws SQLException {
        String sql = "INSERT INTO appliances (name, category, watts, hours_per_day, days_per_month, quantity) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, appliance.getName());
        stmt.setString(2, appliance.getCategory());
        stmt.setDouble(3, appliance.getWatts());
        stmt.setDouble(4, appliance.getHoursPerDay());
        stmt.setDouble(5, appliance.getDaysPerMonth());
        stmt.setInt(6, appliance.getQuantity());
        int rows = stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            appliance.setId(keys.getInt(1));
        }
        return rows;
    }

    @Override
    public int update(Appliance appliance) throws SQLException {
        String sql = "UPDATE appliances SET name = ?, category = ?, watts = ?, hours_per_day = ?, days_per_month = ?, quantity = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, appliance.getName());
        stmt.setString(2, appliance.getCategory());
        stmt.setDouble(3, appliance.getWatts());
        stmt.setDouble(4, appliance.getHoursPerDay());
        stmt.setDouble(5, appliance.getDaysPerMonth());
        stmt.setInt(6, appliance.getQuantity());
        stmt.setInt(7, appliance.getId());
        return stmt.executeUpdate();
    }

    @Override
    public int delete(Appliance appliance) throws SQLException {
        String sql = "DELETE FROM appliances WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, appliance.getId());
        return stmt.executeUpdate();
    }

    private Appliance mapRow(ResultSet rs) throws SQLException {
        return new Appliance(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("watts"),
                rs.getDouble("hours_per_day"),
                rs.getDouble("days_per_month"),
                rs.getInt("quantity")
        );
    }
}
