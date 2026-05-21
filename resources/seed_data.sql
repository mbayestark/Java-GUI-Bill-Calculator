-- Electricity Bill Estimator — Seed Data
-- Run AFTER schema.sql. Populates tariff plans, users, appliances, and bill history.
-- Passwords are all BCrypt hashes of "pass123" (cost factor 10).

-- ============================================================
-- TARIFF PLANS
-- ============================================================

-- Plan 1: Senelec Social (cheap flat rate for low-income households)
INSERT INTO tariff_plans (name, plan_type, flat_rate, is_tiered, peak_multiplier)
VALUES ('Senelec Social', 'residential', 90.47, false, 1.0);

-- Plan 2: Senelec Domestic (tiered progressive — mirrors real Senelec brackets)
INSERT INTO tariff_plans (name, plan_type, flat_rate, is_tiered, peak_multiplier)
VALUES ('Senelec Domestic', 'residential', 95.0, true, 1.3);

-- Plan 3: Senelec Comfort (flat rate with peak surcharge for AC-heavy homes)
INSERT INTO tariff_plans (name, plan_type, flat_rate, is_tiered, peak_multiplier)
VALUES ('Senelec Comfort', 'residential', 112.65, false, 1.5);

-- Plan 4: Senelec Institutional (tiered, generous brackets for schools/hospitals)
INSERT INTO tariff_plans (name, plan_type, flat_rate, is_tiered, peak_multiplier)
VALUES ('Senelec Institutional', 'residential', 85.0, true, 1.2);

-- Plan 5: Senelec Business Standard (commercial flat + 18% tax)
INSERT INTO tariff_plans (name, plan_type, flat_rate, is_tiered, peak_multiplier)
VALUES ('Senelec Business Standard', 'commercial', 108.0, false, 1.4);

-- Plan 6: Senelec Business Premium (commercial, higher rate, no peak surcharge)
INSERT INTO tariff_plans (name, plan_type, flat_rate, is_tiered, peak_multiplier)
VALUES ('Senelec Business Premium', 'commercial', 125.50, false, 1.0);

-- ============================================================
-- TARIFF TIERS (for tiered plans only)
-- ============================================================

-- Tiers for Plan 2: Senelec Domestic (progressive residential)
INSERT INTO tariff_tiers (tariff_plan_id, up_to_kwh, rate_per_kwh) VALUES
(2, 150, 90.47),
(2, 250, 101.64),
(2, 500, 112.65),
(2, 10000, 116.44);

-- Tiers for Plan 4: Senelec Institutional (generous institutional brackets)
INSERT INTO tariff_tiers (tariff_plan_id, up_to_kwh, rate_per_kwh) VALUES
(4, 500, 78.0),
(4, 1500, 88.50),
(4, 5000, 95.0),
(4, 50000, 102.0);

-- ============================================================
-- USERS
-- BCrypt hash for "pass123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ============================================================

-- Admin already seeded by the app, but let's add a second admin
INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('admin2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin', NULL, 'Fatou Diallo', 0, NULL, 1);

-- Individual clients
INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('moussa_ndiaye', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'individual', 'Moussa Ndiaye', 25000, 1, 1);

INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('awa_fall', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'individual', 'Awa Fall', 40000, 2, 1);

INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('ibrahima_sy', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'individual', 'Ibrahima Sy', 60000, 3, 1);

INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('marieme_ba', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'individual', 'Marieme Ba', 15000, 1, 1);

INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('cheikh_diop', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'individual', 'Cheikh Diop', 35000, 2, 1);

-- Schools
INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('daust', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'school', 'DAUST — Dakar American University of Science and Technology', 500000, 4, 25);

INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('lycee_lamine', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'school', 'Lycee Lamine Gueye', 200000, 4, 12);

INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('ecole_mariama', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'school', 'Ecole Mariama Ba', 80000, 4, 6);

-- Hospitals
INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('hopital_fann', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'hospital', 'Hopital de Fann', 1500000, 4, 120);

INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('clinique_pasteur', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'hospital', 'Clinique Pasteur', 600000, 4, 30);

INSERT INTO users (username, password_hash, role, client_type, full_name, monthly_budget, tariff_plan_id, capacity)
VALUES ('centre_sante_pikine', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'client', 'hospital', 'Centre de Sante de Pikine', 250000, 4, 15);

-- ============================================================
-- APPLIANCES
-- User IDs depend on insertion order. Assuming admin=1 (already exists),
-- admin2=2, then clients start at 3.
-- Moussa=3, Awa=4, Ibrahima=5, Marieme=6, Cheikh=7
-- DAUST=8, Lycee=9, Ecole=10
-- Fann=11, Pasteur=12, Pikine=13
-- ============================================================

-- ---- Moussa Ndiaye (individual, budget 25000, Social plan — modest household) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(3, 'LED Bulb', 'Lighting', 10, 6, 30, 5, 3),
(3, 'Ceiling Fan', 'Home', 75, 8, 30, 2, 4),
(3, 'Television', 'Entertainment', 100, 4, 30, 1, 3),
(3, 'Refrigerator', 'Kitchen', 150, 24, 30, 1, 8),
(3, 'Phone Charger', 'Home', 5, 3, 30, 3, 1),
(3, 'Iron', 'Home', 1200, 0.5, 8, 1, 0.5);

-- ---- Awa Fall (individual, budget 40000, Domestic tiered — mid-range household) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(4, 'LED Bulb', 'Lighting', 10, 7, 30, 8, 4),
(4, 'Ceiling Fan', 'Home', 75, 10, 30, 3, 5),
(4, 'Television', 'Entertainment', 100, 5, 30, 2, 4),
(4, 'Refrigerator', 'Kitchen', 150, 24, 30, 1, 8),
(4, 'Microwave', 'Kitchen', 1000, 0.3, 25, 1, 0.2),
(4, 'Washing Machine', 'Laundry', 500, 1, 8, 1, 0),
(4, 'Water Heater', 'Bathroom', 3000, 0.5, 30, 1, 0.5),
(4, 'Electric Kettle', 'Kitchen', 1500, 0.2, 30, 1, 0.1),
(4, 'Laptop', 'Office', 60, 4, 25, 1, 2);

-- ---- Ibrahima Sy (individual, budget 60000, Comfort plan — upscale with AC) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(5, 'Air Conditioner', 'Cooling', 1500, 10, 30, 3, 6),
(5, 'LED Bulb', 'Lighting', 10, 8, 30, 12, 5),
(5, 'Television', 'Entertainment', 100, 6, 30, 3, 4),
(5, 'Refrigerator', 'Kitchen', 150, 24, 30, 2, 8),
(5, 'Microwave', 'Kitchen', 1000, 0.5, 30, 1, 0.3),
(5, 'Electric Oven', 'Kitchen', 2500, 1, 20, 1, 0.5),
(5, 'Washing Machine', 'Laundry', 500, 1.5, 10, 1, 0),
(5, 'Water Heater', 'Bathroom', 3000, 1, 30, 2, 1),
(5, 'Desktop Computer', 'Office', 200, 8, 25, 1, 4),
(5, 'Hair Dryer', 'Bathroom', 1800, 0.2, 20, 1, 0.2),
(5, 'Blender', 'Kitchen', 400, 0.1, 15, 1, 0);

-- ---- Marieme Ba (individual, budget 15000, Social plan — small apartment) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(6, 'LED Bulb', 'Lighting', 10, 5, 30, 3, 3),
(6, 'Ceiling Fan', 'Home', 75, 6, 30, 1, 3),
(6, 'Television', 'Entertainment', 100, 3, 30, 1, 2),
(6, 'Phone Charger', 'Home', 5, 2, 30, 2, 0);

-- ---- Cheikh Diop (individual, budget 35000, Domestic tiered — family home) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(7, 'LED Bulb', 'Lighting', 10, 7, 30, 6, 4),
(7, 'Ceiling Fan', 'Home', 75, 9, 30, 4, 5),
(7, 'Air Conditioner', 'Cooling', 1500, 6, 30, 1, 4),
(7, 'Television', 'Entertainment', 100, 5, 30, 2, 4),
(7, 'Refrigerator', 'Kitchen', 150, 24, 30, 1, 8),
(7, 'Microwave', 'Kitchen', 1000, 0.3, 20, 1, 0.2),
(7, 'Iron', 'Home', 1200, 0.5, 10, 1, 0.5),
(7, 'Laptop', 'Office', 60, 5, 28, 2, 2);

-- ---- DAUST (school, 25 classrooms, Institutional tiered) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(8, 'Projector', 'Classroom', 300, 6, 22, 25, 3),
(8, 'Desktop Lab Computer', 'Classroom', 250, 6, 22, 25, 3),
(8, 'Smartboard', 'Classroom', 150, 6, 22, 25, 3),
(8, 'Ceiling Fan', 'Classroom', 75, 8, 22, 25, 4),
(8, 'Air Conditioner', 'Cooling', 1500, 8, 22, 10, 5),
(8, 'LED Bulb', 'Lighting', 10, 10, 22, 100, 4),
(8, 'Desktop Computer', 'Office', 200, 8, 22, 15, 4),
(8, 'Printer', 'Office', 500, 3, 22, 5, 1),
(8, 'Water Dispenser', 'Kitchen', 100, 10, 22, 8, 4);

-- ---- Lycee Lamine Gueye (school, 12 classrooms) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(9, 'Projector', 'Classroom', 300, 5, 22, 12, 2),
(9, 'Smartboard', 'Classroom', 150, 5, 22, 12, 2),
(9, 'Ceiling Fan', 'Classroom', 75, 7, 22, 12, 3),
(9, 'LED Bulb', 'Lighting', 10, 8, 22, 50, 3),
(9, 'Desktop Lab Computer', 'Classroom', 250, 4, 22, 8, 2);

-- ---- Ecole Mariama Ba (school, 6 classrooms — small primary school) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(10, 'Ceiling Fan', 'Classroom', 75, 6, 22, 6, 3),
(10, 'LED Bulb', 'Lighting', 10, 6, 22, 24, 2),
(10, 'Projector', 'Classroom', 300, 3, 22, 3, 1),
(10, 'Television', 'Classroom', 100, 2, 22, 2, 1);

-- ---- Hopital de Fann (hospital, 120 beds — major referral hospital) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(11, 'Hospital Bed (Electric)', 'Medical', 200, 12, 30, 120, 6),
(11, 'X-Ray Machine', 'Medical', 5000, 4, 30, 3, 2),
(11, 'Ultrasound Scanner', 'Medical', 500, 6, 30, 5, 3),
(11, 'Sterilizer', 'Medical', 2000, 8, 30, 4, 4),
(11, 'Air Conditioner', 'Cooling', 1500, 18, 30, 40, 10),
(11, 'LED Bulb', 'Lighting', 10, 20, 30, 300, 8),
(11, 'Refrigerator', 'Kitchen', 150, 24, 30, 10, 8),
(11, 'Water Heater', 'Bathroom', 3000, 4, 30, 8, 2),
(11, 'Desktop Computer', 'Office', 200, 10, 30, 20, 5),
(11, 'Elevator', 'Medical', 3000, 8, 30, 2, 4);

-- ---- Clinique Pasteur (hospital, 30 beds — private clinic) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(12, 'Hospital Bed (Electric)', 'Medical', 200, 12, 30, 30, 6),
(12, 'Ultrasound Scanner', 'Medical', 500, 5, 30, 2, 2),
(12, 'Sterilizer', 'Medical', 2000, 6, 30, 2, 3),
(12, 'Air Conditioner', 'Cooling', 1500, 14, 30, 12, 8),
(12, 'LED Bulb', 'Lighting', 10, 18, 30, 80, 6),
(12, 'Refrigerator', 'Kitchen', 150, 24, 30, 3, 8),
(12, 'Desktop Computer', 'Office', 200, 8, 30, 5, 4);

-- ---- Centre de Sante de Pikine (hospital, 15 beds — community health center) ----
INSERT INTO appliances (user_id, name, category, watts, hours_per_day, days_per_month, quantity, peak_hours_per_day) VALUES
(13, 'Hospital Bed (Electric)', 'Medical', 200, 10, 30, 15, 5),
(13, 'Sterilizer', 'Medical', 2000, 4, 30, 1, 2),
(13, 'Ceiling Fan', 'Home', 75, 12, 30, 10, 6),
(13, 'LED Bulb', 'Lighting', 10, 14, 30, 40, 5),
(13, 'Refrigerator', 'Kitchen', 150, 24, 30, 2, 8),
(13, 'Desktop Computer', 'Office', 200, 6, 30, 3, 3);

-- ============================================================
-- BILL HISTORY (3 months of historical data for prediction)
-- ============================================================

-- Moussa Ndiaye (modest household — bills around 15k-20k CFA)
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(3, 2, 2026, 185.4, 16772),
(3, 3, 2026, 192.1, 17378),
(3, 4, 2026, 201.6, 18237);

-- Awa Fall (mid-range — bills around 30k-35k CFA)
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(4, 2, 2026, 310.5, 29830),
(4, 3, 2026, 325.8, 31500),
(4, 4, 2026, 298.2, 28650);

-- Ibrahima Sy (upscale with AC — bills around 50k-70k CFA)
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(5, 2, 2026, 520.3, 55620),
(5, 3, 2026, 580.1, 62480),
(5, 4, 2026, 610.7, 66340);

-- Cheikh Diop (family home — bills around 25k-30k CFA)
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(7, 2, 2026, 280.4, 26950),
(7, 3, 2026, 295.0, 28340),
(7, 4, 2026, 310.2, 29800);

-- DAUST (big school — bills around 350k-400k CFA)
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(8, 2, 2026, 4120.5, 348500),
(8, 3, 2026, 4350.0, 372000),
(8, 4, 2026, 3980.2, 335200);

-- Lycee Lamine Gueye
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(9, 2, 2026, 1250.3, 105800),
(9, 3, 2026, 1310.0, 112500),
(9, 4, 2026, 1180.5, 98400);

-- Hopital de Fann (major hospital — bills around 1.2M+ CFA)
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(11, 2, 2026, 14520.0, 1185600),
(11, 3, 2026, 14890.5, 1218400),
(11, 4, 2026, 15100.2, 1240800);

-- Clinique Pasteur
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(12, 2, 2026, 3850.0, 310200),
(12, 3, 2026, 4010.5, 325800),
(12, 4, 2026, 3920.8, 318400);

-- Centre de Sante de Pikine
INSERT INTO bill_history (user_id, month, year, total_kwh, total_cost) VALUES
(13, 2, 2026, 1520.0, 121600),
(13, 3, 2026, 1480.3, 118400),
(13, 4, 2026, 1550.8, 124000);
