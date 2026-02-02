CREATE TABLE IF NOT EXISTS user_config (
     user_id UUID PRIMARY KEY,
     address_id UUID,
     first_name VARCHAR(255),
     last_name VARCHAR(255),
     username VARCHAR(255) UNIQUE,
     email VARCHAR(255) UNIQUE,
     phone_number VARCHAR(50),
     encrypted_password VARCHAR(255),
     linkedin_url VARCHAR(255),
     github_url VARCHAR(255),
     created_at TIMESTAMP,
     updated_at TIMESTAMP,
     account_type VARCHAR(50),
     role VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS address (
     id UUID PRIMARY KEY,
     street VARCHAR(255),
     city VARCHAR(255),
     state VARCHAR(255),
     country VARCHAR(255),
     pincode VARCHAR(50),
     created_at TIMESTAMP,
     updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS company (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS seed_list (
    id UUID PRIMARY KEY,
    company TEXT NOT NULL,
    job_board TEXT,
    validation_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS company_jobs (
    id UUID PRIMARY KEY,
    seed_list_id UUID REFERENCES seed_list(id),
    job_title TEXT NOT NULL,
    location TEXT NOT NULL,
    application_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);