-- V1__initTables.sql

-- Create table for the User entity
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    firstName VARCHAR(20) NOT NULL,
    lastName VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    profilePicture VARCHAR(255),
    password VARCHAR(120),
    mobile VARCHAR(255) NOT NULL,
    isActive BOOLEAN DEFAULT TRUE,
    role VARCHAR(255),
    qrCode VARCHAR(255),
    otpCode VARCHAR(255),
    otpExpireAt TIMESTAMP,
    otpCreatedAt TIMESTAMP,
    token VARCHAR(255),
    createdAt TIMESTAMP,
    lastModifiedAt TIMESTAMP
);

-- Create table for the Vendor entity
CREATE TABLE IF NOT EXISTS vendors (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo VARCHAR(255),
    createdBy VARCHAR(36),
    lastModifiedBy VARCHAR(36),
    createdAt TIMESTAMP,
    lastModifiedAt TIMESTAMP,
    FOREIGN KEY (createdBy) REFERENCES users(id),
    FOREIGN KEY (lastModifiedBy) REFERENCES users(id)
);

-- Create table for the Merchant entity
CREATE TABLE IF NOT EXISTS merchants (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    logo VARCHAR(255),
    abbreviation VARCHAR(50),
    adminFirstName VARCHAR(256) NOT NULL,
    adminLastName VARCHAR(256) NOT NULL,
    adminEmail VARCHAR(256),
    mobile VARCHAR(256) NOT NULL,
    password VARCHAR(120),
    firstAddress VARCHAR(255),
    secondAddress VARCHAR(255),
    city VARCHAR(256) NOT NULL,
    state VARCHAR(256),
    country VARCHAR(256) NOT NULL,
    isActive BOOLEAN DEFAULT TRUE,
    createdBy VARCHAR(36),
    lastModifiedBy VARCHAR(36),
    createdAt TIMESTAMP,
    lastModifiedAt TIMESTAMP,
    FOREIGN KEY (createdBy) REFERENCES users(id),
    FOREIGN KEY (lastModifiedBy) REFERENCES users(id)
);

-- Create table for the Promotion entity
CREATE TABLE IF NOT EXISTS promotions (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    isActive BOOLEAN DEFAULT TRUE,
    status VARCHAR(255),
    startDate TIMESTAMP NOT NULL,
    endDate TIMESTAMP NOT NULL,
    vendorId VARCHAR(36),
    merchantId VARCHAR(36),
    createdBy VARCHAR(36),
    lastModifiedBy VARCHAR(36),
    createdAt TIMESTAMP,
    lastModifiedAt TIMESTAMP,
    FOREIGN KEY (vendorId) REFERENCES vendors(id),
    FOREIGN KEY (merchantId) REFERENCES merchants(id),
    FOREIGN KEY (createdBy) REFERENCES users(id),
    FOREIGN KEY (lastModifiedBy) REFERENCES users(id)
);

-- Create table for the Validator entity
CREATE TABLE IF NOT EXISTS validators (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    validatorKey VARCHAR(255) NOT NULL,
    encodedKey VARCHAR(255) NOT NULL,
    isActive BOOLEAN DEFAULT TRUE,
    createdBy VARCHAR(36),
    lastModifiedBy VARCHAR(36),
    vendorId VARCHAR(36),
    token VARCHAR(255),
    createdAt TIMESTAMP,
    lastModifiedAt TIMESTAMP,
    FOREIGN KEY (createdBy) REFERENCES users(id),
    FOREIGN KEY (lastModifiedBy) REFERENCES users(id),
    FOREIGN KEY (vendorId) REFERENCES vendors(id)
);

-- Create join table for the many-to-many relationship between promotions and users
CREATE TABLE IF NOT EXISTS promotion_user (
    promotion_id VARCHAR(36),
    user_id VARCHAR(36),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
