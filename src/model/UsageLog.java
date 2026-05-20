package model;

import java.sql.Date;

public class UsageLog {
    private int id;
    private int applianceId;
    private Date logDate;
    private double hoursUsed;

    public UsageLog(int applianceId, Date logDate, double hoursUsed) {
        this.setApplianceId(applianceId);
        this.setLogDate(logDate);
        this.setHoursUsed(hoursUsed);
    }

    public UsageLog(int id, int applianceId, Date logDate, double hoursUsed) {
        this.setId(id);
        this.setApplianceId(applianceId);
        this.setLogDate(logDate);
        this.setHoursUsed(hoursUsed);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getApplianceId() { return applianceId; }
    public void setApplianceId(int applianceId) {
        if (applianceId <= 0) {
            throw new IllegalArgumentException("Appliance ID must be positive");
        }
        this.applianceId = applianceId;
    }

    public Date getLogDate() { return logDate; }
    public void setLogDate(Date logDate) {
        if (logDate == null) {
            throw new IllegalArgumentException("Log date cannot be null");
        }
        this.logDate = logDate;
    }

    public double getHoursUsed() { return hoursUsed; }
    public void setHoursUsed(double hoursUsed) {
        if (hoursUsed < 0 || hoursUsed > 24) {
            throw new IllegalArgumentException("Hours must be between 0 and 24");
        }
        this.hoursUsed = hoursUsed;
    }
}
