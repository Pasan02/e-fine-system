-- =============================================================================
-- V6__seed_fine_categories.sql
-- Seeds initial fine category data for Sri Lanka Traffic Fine System.
-- These are standard Sri Lanka Police traffic violation categories with
-- fixed fine amounts (as per Section 4.2 Key Design Decisions).
-- All amounts are in LKR (Sri Lankan Rupees).
-- =============================================================================

INSERT INTO fine_categories (category_code, description, amount, is_active) VALUES

-- Speed violations
('SPD01', 'Exceeding speed limit in urban area (< 20 km/h over)',            1500.00, TRUE),
('SPD02', 'Exceeding speed limit in urban area (20-40 km/h over)',           3000.00, TRUE),
('SPD03', 'Exceeding speed limit on highway (> 40 km/h over)',               6000.00, TRUE),

-- Signal & sign violations
('SIG01', 'Jumping a red traffic signal',                                    3000.00, TRUE),
('SIG02', 'Ignoring a stop sign',                                            1500.00, TRUE),
('SIG03', 'Failure to comply with traffic signs',                            1000.00, TRUE),

-- Seatbelt & helmet
('SFT01', 'Driver not wearing seatbelt',                                     1000.00, TRUE),
('SFT02', 'Passenger not wearing seatbelt',                                  1000.00, TRUE),
('SFT03', 'Motorcyclist not wearing helmet',                                 1500.00, TRUE),

-- Mobile phone use
('MOB01', 'Using mobile phone while driving',                                3000.00, TRUE),

-- Licensing & documentation
('LIC01', 'Driving without a valid driving license',                         5000.00, TRUE),
('LIC02', 'Driving with expired license',                                    2500.00, TRUE),
('LIC03', 'Vehicle without valid revenue license',                           5000.00, TRUE),
('LIC04', 'Vehicle without valid insurance',                                 5000.00, TRUE),

-- Parking violations
('PRK01', 'Parking in a no-parking zone',                                    1000.00, TRUE),
('PRK02', 'Double parking causing obstruction',                              2000.00, TRUE),
('PRK03', 'Parking on a pedestrian crossing',                                2000.00, TRUE),

-- Overtaking & lane violations
('OVT01', 'Overtaking in a no-overtaking zone',                              3000.00, TRUE),
('LAN01', 'Improper lane change / cutting lanes',                            1500.00, TRUE),
('LAN02', 'Driving on wrong side of the road',                               5000.00, TRUE),

-- Vehicle condition
('VEH01', 'Vehicle emitting excessive smoke / environmental violation',       2000.00, TRUE),
('VEH02', 'Using unauthorized modifications (tinted windows, etc.)',         3000.00, TRUE),

-- Alcohol & drugs
('ALC01', 'Driving under the influence of alcohol',                         25000.00, TRUE),
('ALC02', 'Driving under the influence of drugs',                           25000.00, TRUE)

ON CONFLICT (category_code) DO NOTHING;
