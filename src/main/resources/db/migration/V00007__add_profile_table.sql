CREATE TABLE IF NOT EXISTS user_preferences (
    id UUID PRIMARY KEY,
    user_id UUID,
    preferred_job_titles JSONB,
    preferred_locations JSONB,
    employment_type JSONB, -- full time, internship
    remote_ok BOOLEAN DEFAULT FALSE,
    willing_to_relocate BOOLEAN DEFAULT FALSE,
    preferred_yoe INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- optional tables since they are not required for the initial version of the app,
-- but can be added later to enhance user profiles and job matching capabilities
CREATE TABLE IF NOT EXISTS work_experience (
    id UUID PRIMARY KEY,
    user_id UUID,
    company_name TEXT,
    job_title TEXT,
    state TEXT,
    country TEXT,
    employment_type TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS education (
    id UUID PRIMARY KEY,
    user_id UUID,
    institution_name VARCHAR(255),
    degree VARCHAR(100),
    field_of_study VARCHAR(100),
    start_year INTEGER,
    end_year INTEGER,
    grade VARCHAR(20),
    state TEXT,
    country TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);