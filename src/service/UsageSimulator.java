package service;

import model.Appliance;
import model.UsageLog;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class UsageSimulator {

    private Random random;

    public UsageSimulator() {
        this.random = new Random();
    }

    public List<UsageLog> simulateMonth(List<Appliance> appliances,
                                         String clientType) {
        List<UsageLog> logs = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        for (int i = 0; i < appliances.size(); i++) {
            Appliance app = appliances.get(i);
            List<UsageLog> appLogs = simulateAppliance(
                    app, clientType, year, month, today);
            for (int j = 0; j < appLogs.size(); j++) {
                logs.add(appLogs.get(j));
            }
        }
        return logs;
    }

    private List<UsageLog> simulateAppliance(Appliance app,
                                              String clientType,
                                              int year, int month,
                                              int today) {
        List<UsageLog> logs = new ArrayList<>();
        for (int day = 1; day <= today; day++) {
            Calendar dayCal = Calendar.getInstance();
            dayCal.set(year, month, day);
            int dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK);
            double hours = generateHours(app, clientType, dayOfWeek);
            Date logDate = new Date(dayCal.getTimeInMillis());
            UsageLog log = new UsageLog(app.getId(), logDate, hours);
            logs.add(log);
        }
        return logs;
    }

    private double generateHours(Appliance app, String clientType,
                                  int dayOfWeek) {
        double baseHours = app.getHoursPerDay();
        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY
                || dayOfWeek == Calendar.SUNDAY);

        if ("school".equals(clientType)) {
            return generateSchoolHours(baseHours, isWeekend);
        }
        if ("hospital".equals(clientType)) {
            return generateHospitalHours(baseHours);
        }
        return generateIndividualHours(baseHours, isWeekend);
    }

    private double generateSchoolHours(double baseHours, boolean isWeekend) {
        if (isWeekend) {
            return 0;
        }
        return addVariance(baseHours, 0.10);
    }

    private double generateHospitalHours(double baseHours) {
        return addVariance(baseHours, 0.05);
    }

    private double generateIndividualHours(double baseHours,
                                            boolean isWeekend) {
        if (isWeekend) {
            return addVariance(baseHours * 1.2, 0.15);
        }
        return addVariance(baseHours, 0.15);
    }

    private double addVariance(double base, double maxVariance) {
        double variance = (random.nextDouble() * 2 - 1) * maxVariance;
        double result = base * (1.0 + variance);
        if (result < 0) {
            result = 0;
        }
        if (result > 24) {
            result = 24;
        }
        return Math.round(result * 10.0) / 10.0;
    }
}
