-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- SHA-256 hash
    phone VARCHAR(20),
    birth_date DATE,
    role VARCHAR(20) NOT NULL DEFAULT 'INDIVIDUAL', -- ADMIN, INDIVIDUAL, CORPORATE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    is_active BOOLEAN DEFAULT TRUE
);

-- Vehicles table
CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    vehicle_type VARCHAR(20) NOT NULL, -- AUTOMOBILE, HELICOPTER, MOTORCYCLE (managed in Java)
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(30),
    plate_number VARCHAR(20) UNIQUE NOT NULL,
    value_tl DECIMAL(12, 2) NOT NULL, -- Vehicle value in Turkish Lira
    capacity INTEGER, -- Passenger capacity
    engine_power VARCHAR(50), -- Engine specifications
    fuel_type VARCHAR(30), -- Gasoline, Diesel, Electric, etc.
    transmission VARCHAR(20), -- Manual, Automatic
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rentals table
CREATE TABLE rentals (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    vehicle_id INTEGER NOT NULL REFERENCES vehicles(id),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    pricing_type VARCHAR(20) NOT NULL, -- HOURLY, DAILY, WEEKLY, MONTHLY
    rental_duration INTEGER NOT NULL, -- Duration in units
    unit_price DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    deposit_amount DECIMAL(12, 2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, COMPLETED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Deposits table
CREATE TABLE deposits (
    id SERIAL PRIMARY KEY,
    rental_id INTEGER NOT NULL REFERENCES rentals(id),
    deposit_amount DECIMAL(12, 2) NOT NULL,
    deposit_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PAID, REFUNDED
    deposit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    refund_date TIMESTAMP,
    refund_amount DECIMAL(12, 2) DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Basic indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_vehicles_type ON vehicles(vehicle_type);
CREATE INDEX idx_vehicles_available ON vehicles(is_available);
CREATE INDEX idx_rentals_user ON rentals(user_id);
CREATE INDEX idx_rentals_vehicle ON rentals(vehicle_id);
CREATE INDEX idx_rentals_dates ON rentals(start_date, end_date);

-- Insert sample users (password is SHA-256 hash of "password123")
INSERT INTO users (first_name, last_name, email, password_hash, phone, birth_date, role) VALUES
('Admin', 'User', 'admin@vehiclerental.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '+90-555-0001', '1985-01-01', 'ADMIN'),
('John', 'Corporate', 'corporate@company.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '+90-555-0002', '1980-05-15', 'CORPORATE'),
('Jane', 'Individual', 'individual@email.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '+90-555-0003', '1990-08-20', 'INDIVIDUAL');

-- Insert sample vehicles
INSERT INTO vehicles (vehicle_type, brand, model, year, color, plate_number, value_tl, capacity, engine_power, fuel_type, transmission) VALUES
-- Automobiles
('AUTOMOBILE', 'BMW', 'X5', 2023, 'Black', '34 ABC 123', 1500000.00, 5, '3.0L Turbo', 'Gasoline', 'Automatic'),
('AUTOMOBILE', 'Mercedes', 'S-Class', 2024, 'White', '06 DEF 456', 2500000.00, 5, '4.0L V8', 'Gasoline', 'Automatic'),
('AUTOMOBILE', 'Toyota', 'Camry', 2022, 'Silver', '35 GHI 789', 800000.00, 5, '2.5L Hybrid', 'Hybrid', 'Automatic'),
('AUTOMOBILE', 'Volkswagen', 'Golf', 2023, 'Blue', '16 JKL 012', 650000.00, 5, '1.4L TSI', 'Gasoline', 'Manual'),
-- Helicopters
('HELICOPTER', 'Bell', '407', 2021, 'Blue', 'TC-HAB', 15000000.00, 7, '650 SHP', 'Jet Fuel', 'N/A'),
('HELICOPTER', 'Airbus', 'H125', 2023, 'Red', 'TC-HEL', 18000000.00, 6, '847 SHP', 'Jet Fuel', 'N/A'),
-- Motorcycles
('MOTORCYCLE', 'Harley Davidson', 'Street 750', 2023, 'Black', '34 MOT 111', 180000.00, 2, '749cc V-Twin', 'Gasoline', 'Manual'),
('MOTORCYCLE', 'Honda', 'CBR600RR', 2024, 'Red', '06 SPR 222', 220000.00, 2, '599cc Inline-4', 'Gasoline', 'Manual');

-- Sample rental data
INSERT INTO rentals (user_id, vehicle_id, start_date, end_date, pricing_type, rental_duration, unit_price, total_amount, deposit_amount, status) VALUES
(2, 1, '2024-01-15 10:00:00', '2024-01-17 10:00:00', 'DAILY', 2, 300.00, 600.00, 0, 'COMPLETED'),
(3, 3, '2024-01-20 09:00:00', '2024-01-22 09:00:00', 'DAILY', 2, 200.00, 400.00, 0, 'COMPLETED');

-- Sample deposit data
INSERT INTO deposits (rental_id, deposit_amount, deposit_status, refund_amount) VALUES
(1, 0, 'PAID', 0),
(2, 0, 'PAID', 0);