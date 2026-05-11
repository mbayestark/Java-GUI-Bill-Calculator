# Electricity Bill Estimator

A Java OOP project that estimates monthly electricity bills based on household appliances and tariff pricing.

## Features

- **Appliance tracking** — define appliances with wattage, daily usage hours, days per month, and quantity
- **kWh calculation** — computes monthly energy consumption per appliance and total
- **Flat rate billing** — calculate cost using a single rate per kWh
- **Tiered pricing** — supports multiple tariff tiers with progressive rates
- **PostgreSQL database** — singleton DB connection via properties file
- **Singleton data model** — centralized `AppData` instance for managing appliances and tariffs

## Project Structure

```
src/
├── Main.java              # Entry point
├── model/
│   ├── Appliance.java     # Appliance model with kWh and cost calculation
│   ├── TariffTier.java    # Tariff tier with threshold and rate
│   └── AppData.java       # Singleton holding appliances, tariffs, and billing logic
└── db/
    └── DBConnection.java  # Singleton PostgreSQL connection
```

## Setup

1. Add a `db.properties` file to the classpath:
   ```properties
   db.host=localhost
   db.port=5432
   db.name=electricity_db
   db.user=your_user
   db.password=your_password
   ```
2. Add the PostgreSQL JDBC driver to your project dependencies.
3. Compile and run `Main.java`.
