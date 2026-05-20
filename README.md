# Electricity Bill Estimator

Java OOP project that estimates monthly electricity bills based on appliances and tariff pricing. Multiple users with role-based access, Swing GUI, PostgreSQL persistence, DAO pattern.

## Features

- Add, edit, delete appliances (wattage, hours/day, peak hours/day, days/month, quantity)
- Monthly kWh per appliance and total
- Peak and off-peak tracking with peak multiplier per plan
- Category breakdown (kWh, cost, percentage)
- Flat rate and tiered progressive pricing
- Residential plans (flat or tiered) and commercial plans (flat + 18% tax)
- Admin creates plans and assigns them to clients
- Client types: individual (5% subsidy), school (15% subsidy), hospital (20% subsidy)
- Base appliances auto-seeded on registration by client type
- IoT simulation: generates daily usage from day 1 to today
- Apply simulation averages to update appliance hours/day
- Bill prediction with correction factors and historical blending
- Bill history (monthly snapshots)
- Budget alerts
- Preset appliance library (general, medical, classroom, home)
- Admin dashboard (clients, revenue, avg consumption)
- CSV and TXT export
- Inline form validation
- Login/register with BCrypt hashing
- Dropdown selectors for client type, plan type, tiered, plan assignment

## Project Structure

```
src/
  Main.java
  model/
    Appliance.java
    UsageLog.java
    TariffTier.java
    TariffPlan.java
    ResidentialPlan.java
    CommercialPlan.java
    User.java
    PresetAppliance.java
    CategorySummary.java
    AppData.java
    Session.java
    BillRecord.java
    ClientTypePolicy.java
  dao/
    DAO.java
    ApplianceDAO.java
    TariffTierDAO.java
    TariffPlanDAO.java
    UserDAO.java
    PresetApplianceDAO.java
    BillHistoryDAO.java
    UsageLogDAO.java
  db/
    DBConnection.java
  service/
    AuthService.java
    BaseApplianceService.java
    ReportService.java
    BillPredictor.java
    UsageSimulator.java
  gui/
    Style.java
    MainFrame.java
    LoginPanel.java
    AdminPanel.java
    ClientPanel.java
resources/
  db.properties
  schema.sql
```

## Database Tables

- tariff_plans (id, name, plan_type, flat_rate, is_tiered, peak_multiplier)
- tariff_tiers (id, tariff_plan_id, up_to_kwh, rate_per_kwh)
- users (id, username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id)
- appliances (id, user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day)
- preset_appliances (id, name, category, default_watts)
- bill_history (id, user_id, month, year, total_kwh, total_cost, recorded_at)
- usage_log (id, appliance_id, log_date, hours_used)

## OOP Concepts

- Encapsulation: all fields private, validated setters
- Inheritance: ResidentialPlan and CommercialPlan extend TariffPlan
- Polymorphism: calculateCost() behaves differently per plan type
- Abstraction: TariffPlan is abstract, DAO<T> is a generic interface
- Singleton: AppData, DBConnection, Session
- Composition: AppData holds List<Appliance> and TariffPlan

## Client Type Differentiation

| Type | Subsidy | Prediction Factor | Preset Categories |
|---|---|---|---|
| individual | 5% domestic | 1.08 (evening peaks) | Home, Kitchen, Lighting, Entertainment, Bathroom |
| school | 15% education | 1.15 (weekday-heavy) | Classroom |
| hospital | 20% healthcare | 1.00 (24/7 flat) | Medical |

## Base Appliances

On registration, appliances are auto-seeded from presets based on client type.
- Individual: home/kitchen/lighting/entertainment/bathroom presets, 3 hrs/day, 30 days
- School: classroom presets, 6 hrs/day, 22 days
- Hospital: medical presets, 12 hrs/day, 30 days

## Peak Consumption

Each appliance has peak hours per day (subset of total hours). Each tariff plan has a peak multiplier (default 1.0). Peak surcharge = peak kWh x flat rate x (multiplier - 1). Bill breakdown shows peak vs off-peak when applicable.

## IoT Simulation

"Simulate Month" generates realistic daily usage for each appliance from day 1 of the current month to today.
- Schools: 0 hours on weekends, +/-10% variance on weekdays
- Hospitals: near-constant, +/-5% variance
- Individuals: +/-15% variance, 20% more usage on weekends

"Apply Averages" updates each appliance's hours/day to the simulated average.

## Bill Prediction

Without history: linear extrapolation adjusted by client type correction factor.
With history: blended 70% current projection + 30% historical average (last 3 months).

## Setup

1. Create the database:
   ```
   createdb electricity_db
   ```

2. Run schema.sql:
   ```
   psql -U your_user -d electricity_db -f resources/schema.sql
   ```

3. Configure resources/db.properties with your credentials.

4. Add dependencies:
   - PostgreSQL JDBC driver (postgresql-42.x.x.jar)
   - BCrypt (jbcrypt-0.4.jar)

5. Run gui.MainFrame for GUI or Main for console.

Default admin: admin / admin123
