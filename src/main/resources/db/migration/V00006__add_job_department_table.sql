CREATE TABLE IF NOT EXISTS job_department_mapping (
    id UUID PRIMARY KEY,
    job_id TEXT NOT NULL,
    department TEXT NOT NULL,
    department_id TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

