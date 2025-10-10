CREATE TABLE IF NOT EXISTS authentication_request (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS authentication_response (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    token TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS registration_request (
    registration_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT ,
    username TEXT NOT NULL UNIQUE,
    email TEXT UNIQUE,
    password TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
