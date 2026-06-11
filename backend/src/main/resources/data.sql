-- =============================================================================
-- SEED DATA FOR SYSTEM (Admin User & Categories)
-- =============================================================================

-- 1. Preseed an Admin User (Username: admin, Password: password123)
-- BCrypt hash for 'password123' is '$2b$10$IbE2vq8DEz.gWkaO.LgLl.h6Nk1ggeyxtPQDx9by0..0qVU52VvYS'
INSERT INTO users (username, password_hash, full_name, phone_number, role, created_at)
VALUES ('admin', '$2b$10$IbE2vq8DEz.gWkaO.LgLl.h6Nk1ggeyxtPQDx9by0..0qVU52VvYS', 'System Administrator', '0711234567', 'ADMIN', CURRENT_TIMESTAMP)
ON CONFLICT (username) DO UPDATE SET password_hash = EXCLUDED.password_hash;

-- 2. Preseed Fine Categories
INSERT INTO fine_categories (category_code, description, amount, is_active, created_at, updated_at) VALUES

-- Speed violations
('SPD01', 'Exceeding speed limit in urban area (< 20 km/h over)',            1500.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SPD02', 'Exceeding speed limit in urban area (20-40 km/h over)',           3000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SPD03', 'Exceeding speed limit on highway (> 40 km/h over)',               6000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Signal & sign violations
('SIG01', 'Jumping a red traffic signal',                                    3000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SIG02', 'Ignoring a stop sign',                                            1500.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SIG03', 'Failure to comply with traffic signs',                            1000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Seatbelt & helmet
('SFT01', 'Driver not wearing seatbelt',                                     1000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SFT02', 'Passenger not wearing seatbelt',                                  1000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SFT03', 'Motorcyclist not wearing helmet',                                 1500.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Mobile phone use
('MOB01', 'Using mobile phone while driving',                                3000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Licensing & documentation
('LIC01', 'Driving without a valid driving license',                         5000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LIC02', 'Driving with expired license',                                    2500.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LIC03', 'Vehicle without valid revenue license',                           5000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LIC04', 'Vehicle without valid insurance',                                 5000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Parking violations
('PRK01', 'Parking in a no-parking zone',                                    1000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PRK02', 'Double parking causing obstruction',                              2000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PRK03', 'Parking on a pedestrian crossing',                                2000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Overtaking & lane violations
('OVT01', 'Overtaking in a no-overtaking zone',                              3000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LAN01', 'Improper lane change / cutting lanes',                            1500.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LAN02', 'Driving on wrong side of the road',                               5000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Vehicle condition
('VEH01', 'Vehicle emitting excessive smoke / environmental violation',       2000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VEH02', 'Using unauthorized modifications (tinted windows, etc.)',         3000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Alcohol & drugs
('ALC01', 'Driving under the influence of alcohol',                         25000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ALC02', 'Driving under the influence of drugs',                           25000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)

ON CONFLICT (category_code) DO NOTHING;
