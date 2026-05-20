package dao;

import db.DBConnection;
import model.UsageLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsageLogDAO {

    private Connection connection;

    public UsageLogDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public int insert(UsageLog log) throws SQLException {
        String sql = "INSERT INTO usage_log (appliance_id, log_date, hours_used) "
                + "VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, log.getApplianceId());
        stmt.setDate(2, log.getLogDate());
        stmt.setDouble(3, log.getHoursUsed());
        int rows = stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            log.setId(keys.getInt(1));
        }
        return rows;
    }

    public void insertBatch(List<UsageLog> logs) throws SQLException {
        String sql = "INSERT INTO usage_log (appliance_id, log_date, hours_used) "
                + "VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < logs.size(); i++) {
            UsageLog log = logs.get(i);
            stmt.setInt(1, log.getApplianceId());
            stmt.setDate(2, log.getLogDate());
            stmt.setDouble(3, log.getHoursUsed());
            stmt.addBatch();
        }
        stmt.executeBatch();
    }

    public List<UsageLog> getByAppliance(int applianceId) throws SQLException {
        String sql = "SELECT * FROM usage_log WHERE appliance_id = ? "
                + "ORDER BY log_date";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, applianceId);
        ResultSet rs = stmt.executeQuery();
        List<UsageLog> logs = new ArrayList<>();
        while (rs.next()) {
            logs.add(mapRow(rs));
        }
        return logs;
    }

    public List<UsageLog> getByApplianceAndMonth(int applianceId,
                                                  int month, int year)
            throws SQLException {
        String sql = "SELECT * FROM usage_log WHERE appliance_id = ? "
                + "AND EXTRACT(MONTH FROM log_date) = ? "
                + "AND EXTRACT(YEAR FROM log_date) = ? "
                + "ORDER BY log_date";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, applianceId);
        stmt.setInt(2, month);
        stmt.setInt(3, year);
        ResultSet rs = stmt.executeQuery();
        List<UsageLog> logs = new ArrayList<>();
        while (rs.next()) {
            logs.add(mapRow(rs));
        }
        return logs;
    }

    public double getAverageHours(int applianceId, int month, int year)
            throws SQLException {
        String sql = "SELECT AVG(hours_used) FROM usage_log "
                + "WHERE appliance_id = ? "
                + "AND EXTRACT(MONTH FROM log_date) = ? "
                + "AND EXTRACT(YEAR FROM log_date) = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, applianceId);
        stmt.setInt(2, month);
        stmt.setInt(3, year);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble(1);
        }
        return 0;
    }

    public boolean hasLogsForMonth(int applianceId, int month, int year)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM usage_log WHERE appliance_id = ? "
                + "AND EXTRACT(MONTH FROM log_date) = ? "
                + "AND EXTRACT(YEAR FROM log_date) = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, applianceId);
        stmt.setInt(2, month);
        stmt.setInt(3, year);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    public void deleteByApplianceAndMonth(int applianceId,
                                           int month, int year)
            throws SQLException {
        String sql = "DELETE FROM usage_log WHERE appliance_id = ? "
                + "AND EXTRACT(MONTH FROM log_date) = ? "
                + "AND EXTRACT(YEAR FROM log_date) = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, applianceId);
        stmt.setInt(2, month);
        stmt.setInt(3, year);
        stmt.executeUpdate();
    }

    public void deleteByUserMonth(List<Integer> applianceIds,
                                   int month, int year)
            throws SQLException {
        for (int i = 0; i < applianceIds.size(); i++) {
            deleteByApplianceAndMonth(applianceIds.get(i), month, year);
        }
    }

    private UsageLog mapRow(ResultSet rs) throws SQLException {
        return new UsageLog(
                rs.getInt("id"),
                rs.getInt("appliance_id"),
                rs.getDate("log_date"),
                rs.getDouble("hours_used")
        );
    }
}
