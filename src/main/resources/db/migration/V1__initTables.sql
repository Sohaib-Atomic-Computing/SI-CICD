-- V1__initTables.sql

-- Create table for the User entity
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    profile_picture VARCHAR(255),
    password VARCHAR(120),
    mobile VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    role VARCHAR(255),
    qr_code VARCHAR(255),
    otp_code VARCHAR(255),
    otp_expire_at TIMESTAMP,
    otp_created_at TIMESTAMP,
    token VARCHAR(255),
    created_at TIMESTAMP,
    last_modified_at TIMESTAMP
);

-- Create table for the Merchant entity
CREATE TABLE IF NOT EXISTS merchants (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    logo VARCHAR(255),
    abbreviation VARCHAR(50),
    admin_first_name VARCHAR(256) NOT NULL,
    admin_last_name VARCHAR(256) NOT NULL,
    admin_email VARCHAR(256),
    mobile VARCHAR(256) NOT NULL,
    password VARCHAR(120),
    first_address VARCHAR(255),
    second_address VARCHAR(255),
    city VARCHAR(256) NOT NULL,
    state VARCHAR(256),
    country VARCHAR(256) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(36),
    last_modified_by VARCHAR(36),
    created_at TIMESTAMP,
    last_modified_at TIMESTAMP
);

-- Create table for the Vendor entity
CREATE TABLE IF NOT EXISTS vendors (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo VARCHAR(255),
    created_by VARCHAR(36),
    last_modified_by VARCHAR(36),
    created_at TIMESTAMP,
    last_modified_at TIMESTAMP
);

-- Create table for the Application entity
CREATE TABLE IF NOT EXISTS applications (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL,
    secret_key VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(36),
    last_modified_by VARCHAR(36),
    created_at TIMESTAMP,
    last_modified_at TIMESTAMP
);

-- Create table for the ApiKey entity
CREATE TABLE IF NOT EXISTS api_keys (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    application_id VARCHAR(36),
    created_by VARCHAR(36),
    last_modified_by VARCHAR(36),
    created_at TIMESTAMP,
    last_modified_at TIMESTAMP
);

-- Create table for the Validator entity
CREATE TABLE IF NOT EXISTS validators (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    validator_key VARCHAR(255) NOT NULL,
    encoded_key VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(36),
    last_modified_by VARCHAR(36),
    vendor_id VARCHAR(36),
    token VARCHAR(255),
    created_at TIMESTAMP,
    last_modified_at TIMESTAMP
);

-- Create table for the Promotion entity
CREATE TABLE IF NOT EXISTS promotions (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    status VARCHAR(255),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    merchant VARCHAR(36),
    vendor_id VARCHAR(36),
    created_by VARCHAR(36),
    last_modified_by VARCHAR(36),
    created_at TIMESTAMP,
    last_modified_at TIMESTAMP
);

-- Create join table for the many-to-many relationship between promotions and users
CREATE TABLE IF NOT EXISTS promotion_user (
    promotion_id VARCHAR(36),
    user_id VARCHAR(36)
);

-- Add foreign key constraints
ALTER TABLE merchants
ADD CONSTRAINT FK_merchants_users_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE merchants
ADD CONSTRAINT FK_merchants_users_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users(id);

-- Alter table for the Vendor entity
ALTER TABLE vendors
ADD CONSTRAINT FK_vendors_users_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE vendors
ADD CONSTRAINT FK_vendors_users_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users(id);

-- Alter table for the Application entity
ALTER TABLE applications
ADD CONSTRAINT FK_applications_users_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE applications
ADD CONSTRAINT FK_applications_users_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users(id);

-- Alter table for the ApiKey entity
ALTER TABLE api_keys
ADD CONSTRAINT FK_api_keys_users_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE api_keys
ADD CONSTRAINT FK_api_keys_users_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users(id);

-- Alter table for the Validator entity
ALTER TABLE validators
ADD CONSTRAINT FK_validators_users_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE validators
ADD CONSTRAINT FK_validators_users_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users(id);

ALTER TABLE validators
ADD CONSTRAINT FK_validators_vendors FOREIGN KEY (vendor_id) REFERENCES vendors(id);

-- Alter table for the Promotion entity
ALTER TABLE promotions
ADD CONSTRAINT FK_promotions_merchants FOREIGN KEY (merchant) REFERENCES merchants(id);

ALTER TABLE promotions
ADD CONSTRAINT FK_promotions_vendors FOREIGN KEY (vendor_id) REFERENCES vendors(id);

ALTER TABLE promotions
ADD CONSTRAINT FK_promotions_users_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE promotions
ADD CONSTRAINT FK_promotions_users_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users(id);

ALTER TABLE promotion_user
    ADD CONSTRAINT FK_promotion_user_promotions
    FOREIGN KEY (promotion_id) REFERENCES promotions(id);

ALTER TABLE promotion_user
    ADD CONSTRAINT FK_promotion_user_users
    FOREIGN KEY (user_id) REFERENCES users(id);
