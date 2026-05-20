package dao;

import db.DBConnection;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements DAO<User> {

    private Connection connection;

    public UserDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public User get(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        String sql = "SELECT * FROM users";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(mapRow(rs));
        }
        return users;
    }

    public List<User> getAllClients() throws SQLException {
        String sql = "SELECT * FROM users WHERE role = 'client'";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<User> clients = new ArrayList<>();
        while (rs.next()) {
            clients.add(mapRow(rs));
        }
        return clients;
    }

    @Override
    public int save(User user) throws SQLException {
        if (user.getId() > 0) {
            return update(user);
        }
        return insert(user);
    }

    @Override
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, client_type, "
                + "full_name, monthly_budget, tariff_plan_id, capacity) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPasswordHash());
        stmt.setString(3, user.getRole());
        stmt.setString(4, user.getClientType());
        stmt.setString(5, user.getFullName());
        stmt.setDouble(6, user.getMonthlyBudget());
        if (user.getTariffPlanId() > 0) {
            stmt.setInt(7, user.getTariffPlanId());
        } else {
            stmt.setNull(7, Types.INTEGER);
        }
        stmt.setInt(8, user.getCapacity());
        int rows = stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            user.setId(keys.getInt(1));
        }
        return rows;
    }

    @Override
    public int update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ?, "
                + "client_type = ?, full_name = ?, monthly_budget = ?, "
                + "tariff_plan_id = ?, capacity = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPasswordHash());
        stmt.setString(3, user.getRole());
        stmt.setString(4, user.getClientType());
        stmt.setString(5, user.getFullName());
        stmt.setDouble(6, user.getMonthlyBudget());
        if (user.getTariffPlanId() > 0) {
            stmt.setInt(7, user.getTariffPlanId());
        } else {
            stmt.setNull(7, Types.INTEGER);
        }
        stmt.setInt(8, user.getCapacity());
        stmt.setInt(9, user.getId());
        return stmt.executeUpdate();
    }

    @Override
    public int delete(User user) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, user.getId());
        return stmt.executeUpdate();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("client_type"),
                rs.getString("full_name"),
                rs.getDouble("monthly_budget"),
                rs.getInt("tariff_plan_id"),
                rs.getInt("capacity")
        );
    }
}
