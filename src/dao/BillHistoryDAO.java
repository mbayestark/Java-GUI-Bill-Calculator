package dao;

import db.DBConnection;
import model.BillRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillHistoryDAO {

    private Connection connection;

    public BillHistoryDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public int insert(BillRecord record) throws SQLException {
        String sql = "INSERT INTO bill_history (user_id, month, year, "
                + "total_kwh, total_cost) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, record.getUserId());
        stmt.setInt(2, record.getMonth());
        stmt.setInt(3, record.getYear());
        stmt.setDouble(4, record.getTotalKwh());
        stmt.setDouble(5, record.getTotalCost());
        int rows = stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            record.setId(keys.getInt(1));
        }
        return rows;
    }

    public List<BillRecord> getByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM bill_history WHERE user_id = ? "
                + "ORDER BY year DESC, month DESC";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        List<BillRecord> records = new ArrayList<>();
        while (rs.next()) {
            records.add(mapRow(rs));
        }
        return records;
    }

    public List<BillRecord> getLastN(int userId, int count) throws SQLException {
        String sql = "SELECT * FROM bill_history WHERE user_id = ? "
                + "ORDER BY year DESC, month DESC LIMIT ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2, count);
        ResultSet rs = stmt.executeQuery();
        List<BillRecord> records = new ArrayList<>();
        while (rs.next()) {
            records.add(mapRow(rs));
        }
        return records;
    }

    public boolean existsForMonth(int userId, int month, int year)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM bill_history "
                + "WHERE user_id = ? AND month = ? AND year = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2, month);
        stmt.setInt(3, year);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    private BillRecord mapRow(ResultSet rs) throws SQLException {
        return new BillRecord(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("month"),
                rs.getInt("year"),
                rs.getDouble("total_kwh"),
                rs.getDouble("total_cost")
        );
    }
}
