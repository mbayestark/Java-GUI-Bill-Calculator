-- Electricity Bill Estimator — Database Schema
-- Run this script against your PostgreSQL database to create all tables.

CREATE TABLE tariff_plans (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    plan_type VARCHAR(20) NOT NULL,
    flat_rate DOUBLE PRECISION NOT NULL DEFAULT 0,
    is_tiered BOOLEAN NOT NULL DEFAULT false,
    peak_multiplier DOUBLE PRECISION NOT NULL DEFAULT 1.0
);

CREATE TABLE tariff_tiers (
    id SERIAL PRIMARY KEY,
    tariff_plan_id INT NOT NULL REFERENCES tariff_plans(id) ON DELETE CASCADE,
    up_to_kwh DOUBLE PRECISION NOT NULL,
    rate_per_kwh DOUBLE PRECISION NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'client',
    client_type VARCHAR(50),
    full_name VARCHAR(100),
    monthly_budget DOUBLE PRECISION DEFAULT 0,
    tariff_plan_id INT REFERENCES tariff_plans(id) ON DELETE SET NULL,
    capacity INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE appliances (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    watts DOUBLE PRECISION NOT NULL,
    hours_per_day DOUBLE PRECISION NOT NULL,
    days_per_month DOUBLE PRECISION NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    peak_hours_per_day DOUBLE PRECISION NOT NULL DEFAULT 0
);

CREATE TABLE preset_appliances (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    default_watts DOUBLE PRECISION NOT NULL
);

-- Seed preset appliances (general)
INSERT INTO preset_appliances (name, category, default_watts) VALUES
('Refrigerator', 'Kitchen', 150),
('Microwave', 'Kitchen', 1000),
('Air Conditioner', 'Cooling', 1500),
('LED Bulb', 'Lighting', 10),
('Television', 'Entertainment', 100),
('Washing Machine', 'Laundry', 500),
('Electric Kettle', 'Kitchen', 1500),
('Laptop', 'Office', 60),
('Desktop Computer', 'Office', 200),
('Water Heater', 'Bathroom', 3000);

-- Seed preset appliances (hospital)
INSERT INTO preset_appliances (name, category, default_watts) VALUES
('X-Ray Machine', 'Medical', 5000),
('Ultrasound Scanner', 'Medical', 500),
('Hospital Bed (Electric)', 'Medical', 200),
('Sterilizer', 'Medical', 2000);

-- Seed preset appliances (school)
INSERT INTO preset_appliances (name, category, default_watts) VALUES
('Projector', 'Classroom', 300),
('Desktop Lab Computer', 'Classroom', 250),
('Smartboard', 'Classroom', 150),
('Ceiling Fan', 'Classroom', 75);

-- Seed preset appliances (home/individual)
INSERT INTO preset_appliances (name, category, default_watts) VALUES
('Iron', 'Home', 1200),
('Blender', 'Home', 400),
('Hair Dryer', 'Home', 1800),
('Electric Oven', 'Home', 2500);

CREATE TABLE bill_history (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    month INT NOT NULL,
    year INT NOT NULL,
    total_kwh DOUBLE PRECISION NOT NULL,
    total_cost DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE usage_log (
    id SERIAL PRIMARY KEY,
    appliance_id INT NOT NULL REFERENCES appliances(id) ON DELETE CASCADE,
    log_date DATE NOT NULL,
    hours_used DOUBLE PRECISION NOT NULL
);

-- The application automatically creates a default admin on first run:
-- username: admin, password: admin123
